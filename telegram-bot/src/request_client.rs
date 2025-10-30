use std::{env, time::Duration};

use lazy_static::lazy_static;
use reqwest::{Client, Response};
use serde_json::Value;
use tracing::info;

use crate::{
    error::request_client_error::RequestClientError,
    schemas::{
        event::{Event, EventFilter},
        inscription::Inscription,
        user::{Token, UserIn, UserOut},
    },
};

lazy_static! {
    static ref URL_BASE: String = env::var("URL_BASE").unwrap();
    static ref CLIENT_TIMEOUT_SECS: u64 = env::var("CLIENT_TIMEOUT_SECS").unwrap().parse().unwrap();
    static ref DEFAULT_MAX_RETRIES: u8 = env::var("DEFAULT_MAX_RETRIES").unwrap().parse().unwrap();
}

pub struct RequestClient {
    client: Client,
}

#[derive(Debug)]
pub enum RequestMethod<'req> {
    Get(&'req [(&'req str, String)]),
    Post(Value),
    Put(Value),
    Patch(Value),
    Delete(Value),
}

impl RequestClient {
    pub fn new() -> Result<Self, RequestClientError> {
        // Create a client with a custom timeout for every request.
        let client = Client::builder()
            .timeout(Duration::from_secs(*CLIENT_TIMEOUT_SECS))
            .build()?;

        Ok(Self { client })
    }

    pub async fn send_get_me(&self, token: &str) -> Result<UserIn, RequestClientError> {
        let response = self
            .send_request_with_retry("usuario/me", RequestMethod::Get(&[]), Some(token))
            .await?;

        Ok(serde_json::from_value(response)?)
    }

    pub async fn send_get_events_list_request(
        &self,
        filters: EventFilter,
        token: &str,
    ) -> Result<Vec<Event>, RequestClientError> {
        let mut filter_query = Vec::new();

        if let Some(max_price) = filters.max_price {
            filter_query.push(("precioPesosMax", max_price.to_string()));
        }
        if let Some(min_price) = filters.min_price {
            filter_query.push(("precioPesosMin", min_price.to_string()));
        }
        if let Some(min_price) = filters.min_date {
            filter_query.push(("fechaInicioMin", min_price.to_string()));
        }
        if let Some(max_date) = filters.max_date {
            filter_query.push(("fechaInicioMax", max_date.to_string()));
        }
        if let Some(category) = filters.category {
            filter_query.push(("categoria", category));
        }
        // TODO: add palabrasClave query.
        let response = self
            .send_request_with_retry("evento", RequestMethod::Get(&filter_query), Some(token))
            .await?;

        Ok(serde_json::from_value(response)?)
    }

    pub async fn send_get_my_inscriptions_request(
        &self,
        token: &str,
    ) -> Result<Vec<Inscription>, RequestClientError> {
        let response = self
            .send_request_with_retry(
                "usuario/mis-inscripciones",
                RequestMethod::Get(&[]),
                Some(token),
            )
            .await?;

        Ok(serde_json::from_value(response)?)
    }

    pub async fn send_user_registration_request(
        &self,
        user_data: UserOut,
    ) -> Result<Token, RequestClientError> {
        let response = self
            .send_request_with_retry(
                "auth/register",
                RequestMethod::Post(serde_json::to_value(&user_data)?),
                None,
            )
            .await?;

        Ok(serde_json::from_value(response)?)
    }

    pub async fn send_user_login_request(
        &self,
        user_data: UserOut,
    ) -> Result<Token, RequestClientError> {
        let response = self
            .send_request_with_retry(
                "auth/login",
                RequestMethod::Post(serde_json::to_value(user_data)?),
                None,
            )
            .await?;

        Ok(serde_json::from_value(response)?)
    }

    async fn send_request_with_retry<'req>(
        &self,
        url: &str,
        method: RequestMethod<'req>,
        token: Option<&str>,
    ) -> Result<Value, RequestClientError> {
        let url = format!("{}/{}", *URL_BASE, url);
        let response = Self::retry(|| self.send_request(&url, &method, token)).await?;

        response
            .json::<Value>()
            .await
            .map_err(RequestClientError::from)
    }

    async fn send_request<'req>(
        &self,
        url: &str,
        method: &RequestMethod<'req>,
        token: Option<&str>,
    ) -> Result<Response, reqwest::Error> {
        let request = match method {
            RequestMethod::Get(params) => {
                info!("Sending GET request to {url}");
                self.client.get(url).query(&params)
            }
            RequestMethod::Post(body) => {
                info!("Sending POST request to {url}");
                self.client.post(url).json(&body)
            }
            RequestMethod::Patch(body) => {
                info!("Sending PATCH request to {url}");
                self.client.patch(url).json(&body)
            }
            RequestMethod::Put(body) => {
                info!("Sending PUT request to {url}");
                self.client.put(url).json(&body)
            }
            RequestMethod::Delete(body) => {
                info!("Sending DELETE request to {url}");
                self.client.delete(url).json(&body)
            }
        };

        if let Some(token) = token {
            request.bearer_auth(token).send().await
        } else {
            request.send().await
        }
    }

    /// Retries sending a request.
    ///
    /// Whenever the request return a timeout, this function will retry sending
    /// it. If the maximum number of attempts has been reached or if another
    /// error (different from a timeout) is returned, it will stop retrying.
    async fn retry<F, Fut>(request: F) -> Result<Response, RequestClientError>
    where
        F: Fn() -> Fut,
        Fut: Future<Output = Result<Response, reqwest::Error>>,
    {
        let max_retries = env::var("MAX_RETRIES")
            .ok()
            .and_then(|r| r.parse::<u8>().ok())
            .unwrap_or(*DEFAULT_MAX_RETRIES);

        for attempt in 0..max_retries {
            let response = request().await;

            match response {
                Ok(r) => return r.error_for_status().map_err(RequestClientError::from),
                Err(e) if e.is_timeout() => {
                    tracing::warn!(
                        "Retrying request, remaining tries: {}",
                        max_retries - attempt
                    );

                    let backoff_timeout = {
                        let backoff_timeout = rand::random_range(0..2u64.pow(attempt as u32));
                        Duration::from_secs(backoff_timeout)
                    };

                    tokio::time::sleep(backoff_timeout).await;

                    continue;
                }
                Err(err) => return Err(RequestClientError::Reqwest(err)),
            }
        }
        Err(RequestClientError::TimeOut)
    }
}
