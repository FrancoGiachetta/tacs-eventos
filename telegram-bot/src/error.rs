use thiserror::Error;

#[derive(Debug, Error)]
pub enum BotError {
    #[error("Couldn't find TELOXIDE_TOKEN env variable")]
    TokenNotFound,
    #[error(transparent)]
    DotEnvError(#[from] dotenv::Error),
}
