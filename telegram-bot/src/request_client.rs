use std::{sync::Arc, time::Duration};

use lazy_static::lazy_static;
use reqwest::{Client, Response};
use serde_json::Value;
use thiserror::Error;
use tracing::info;

use crate::schemas::event::{Event, EventFilter};

lazy_static! {
    static ref CLIENT_TIMEOUT_SECS: u64 = 90;
    static ref URL_BASE: String = String::from("http://localhost:8080/api/v1");
    static ref MAX_RETRIES: u8 = 10;
}

pub struct RequestClient {
    client: Client,
}

#[derive(Debug, Error)]
pub enum RequestClientError {
    #[error(transparent)]
    ReqwestError(#[from] reqwest::Error),
    #[error("request has failed with due to timeout")]
    TimeOutError,
    #[error(transparent)]
    JsonParseError(#[from] serde_json::Error),
}

pub enum RequestMethod<'req> {
    Get(Vec<(&'req str, String)>),
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

    pub async fn send_get_events_list_request(
        &self,
        filters: EventFilter,
    ) -> Result<Vec<Event>, RequestClientError> {
        let url = format!("{}/evento", *URL_BASE);
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
            .send_request_with_retry(url, Arc::new(RequestMethod::Get(filter_query)))
            .await?;

        Ok(serde_json::from_value(response)?)
    }

    async fn send_request_with_retry<'req>(
        &self,
        url: String,
        method: Arc<RequestMethod<'req>>,
    ) -> Result<Value, RequestClientError> {
        Self::retry(|| self.send_request(&url, &method)).await
    }

    async fn send_request<'req>(
        &self,
        url: &str,
        method: &RequestMethod<'req>,
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

        request.send().await
    }

    /// Retries sending a request.
    ///
    /// Whenever the request return a timeout, this function will retry sending
    /// it. If the maximum number of attempts has been reached or if another
    /// error (different from a timeout) is returned, it will stop retrying.
    async fn retry<F, Fut>(request: F) -> Result<Value, RequestClientError>
    where
        F: Fn() -> Fut,
        Fut: Future<Output = Result<Response, reqwest::Error>>,
    {
        for attempt in 0..(*MAX_RETRIES) {
            let response = request().await;

            match response {
                Ok(r) => {
                    return r
                        .json::<Value>()
                        .await
                        .map_err(|err| RequestClientError::ReqwestError(err));
                }
                Err(e) if e.is_timeout() => {
                    tracing::warn!(
                        "Retrying request, remaining tries: {}",
                        *MAX_RETRIES - attempt
                    );

                    let backoff_timeout = {
                        let backoff_timeout = rand::random_range(0..2u64.pow(attempt as u32));
                        Duration::from_secs(backoff_timeout)
                    };

                    tokio::time::sleep(backoff_timeout).await;

                    continue;
                }
                Err(err) => return Err(RequestClientError::ReqwestError(err)),
            }
        }
        Err(RequestClientError::TimeOutError)
    }
}
