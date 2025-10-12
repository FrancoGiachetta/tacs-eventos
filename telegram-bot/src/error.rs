use thiserror::Error;
use tracing::subscriber::SetGlobalDefaultError;

use crate::error::{dialogue_error::DialogueError, request_client_error::RequestClientError};

pub mod dialogue_error;
pub mod request_client_error;

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
    DialogueError(#[from] Box<DialogueError>),
    #[error("{}", .0)]
    CustomError(String),
}
