use thiserror::Error;
use tracing::subscriber::SetGlobalDefaultError;

use crate::error::{
    auth_error::AuthError, dialogue_error::DialogueError, request_client_error::RequestClientError,
};

pub mod auth_error;
pub mod dialogue_error;
pub mod request_client_error;

#[derive(Debug, Error)]
pub enum BotError {
    #[error(transparent)]
    AuthError(#[from] AuthError),
    #[error("{}", .0)]
    CustomError(String),
    #[error(transparent)]
    DialogueError(#[from] Box<DialogueError>),
    #[error(transparent)]
    DotEnvError(#[from] dotenv::Error),
    #[error(transparent)]
    GlobalSubscriberError(#[from] SetGlobalDefaultError),
    #[error(transparent)]
    RequestError(#[from] RequestClientError),
    #[error("Couldn't find TELOXIDE_TOKEN env variable")]
    TokenNotFound,
    #[error(transparent)]
    TeloxideError(#[from] teloxide::RequestError),
}
