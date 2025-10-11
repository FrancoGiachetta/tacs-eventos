use crate::{dialogue::error::DialogueError, request_client::RequestClientError};
use thiserror::Error;
use tracing::subscriber::SetGlobalDefaultError;

#[derive(Debug, Error)]
pub enum BotError {
    #[error("Couldn't find TELOXIDE_TOKEN env variable")]
    TokenNotFound,
    #[error(transparent)]
    DotEnvError(#[from] dotenv::Error),
    #[error(transparent)]
    TeloxideError(#[from] teloxide::RequestError),
    #[error(transparent)]
    GlobalSubscriberError(#[from] SetGlobalDefaultError),
    #[error(transparent)]
    RequestError(#[from] RequestClientError),
    #[error(transparent)]
    DialogueError(#[from] DialogueError),
    #[error("{}", .0)]
    CustomError(String),
}
