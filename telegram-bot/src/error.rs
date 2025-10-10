use crate::request_client::RequestClientError;
use fancy_regex;
use teloxide::dispatching::dialogue::InMemStorageError;
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
    DialogueError(#[from] InMemStorageError),
    #[error(transparent)]
    RegexError(#[from] fancy_regex::Error),
    #[error("{}", .0)]
    CustomError(String),
}
