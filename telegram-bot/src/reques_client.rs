use std::time::Duration;

use lazy_static::lazy_static;
use reqwest::{Client, Response};
use serde_json::Value;
use thiserror::Error;

use crate::command::EventFilter;

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
    TimeOut,
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
        let client = Client::builder()
            .timeout(Duration::from_secs(*CLIENT_TIMEOUT_SECS))
            .build()?;

        Ok(Self { client })
    }

    pub async fn send_get_events_list_request(
        &self,
        filters: EventFilter,
    ) -> Result<Value, RequestClientError> {
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

        let response = self
            .send_request_with_retry(url, RequestMethod::Get(filter_query))
            .await?;

        Ok(response)
    }
    
    async fn send_request_with_retry<'req>(
        &self,
        url: String,
        method: RequestMethod<'req>,
    ) -> Result<Value, RequestClientError> {
        Self::retry(async || self.send_request(&url, &method).await).await
    }

    async fn send_request<'req>(
        &self,
        url: &str,
        method: &RequestMethod<'req>,
    ) -> Result<Response, reqwest::Error> {
        let request = match method {
            RequestMethod::Get(params) => self.client.get(url).query(&params),
            RequestMethod::Post(body) => self.client.post(url).json(&body),
            _ => todo!(),
        };

        request.send().await
    }

    /// Retries sending a request
    ///
    /// Whenever the request return a timeout, this function will retry sending
    /// it. If the maximum number of attempts has been reached or if another
    /// error (different from a timeout) is returned, it will stop retrying.
    async fn retry(
        request: impl AsyncFn() -> Result<Response, reqwest::Error>,
    ) -> Result<Value, RequestClientError> {
        for attempt in 0..(*MAX_RETRIES) {
            match request().await {
                Ok(r) => r.json::<Value>(),
                Err(e) if e.is_timeout() => {
                    tracing::warn!(
                        "Retrying request, remaining tries: {}",
                        *MAX_RETRIES - attempt
                    );

                    let backoff_timeout = {
                        let backoff_timeout = rand::random_range(0..2u64.pow(attempt as u32));
                        Duration::from_secs(backoff_timeout)
                    };
                    std::thread::sleep(backoff_timeout);

                    continue;
                }
                Err(err) => return Err(RequestClientError::ReqwestError(err)),
            };
        }
        Err(RequestClientError::TimeOut)
    }
}
