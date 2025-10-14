use thiserror::Error;

#[derive(Debug, Error)]
pub enum RequestClientError {
    #[error(transparent)]
    Reqwest(#[from] reqwest::Error),
    #[error("request has failed with due to timeout")]
    TimeOut,
    #[error(transparent)]
    JsonParse(#[from] serde_json::Error),
}
