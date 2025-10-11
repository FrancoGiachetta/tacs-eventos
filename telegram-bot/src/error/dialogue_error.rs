use teloxide::dispatching::dialogue::InMemStorageError;
use thiserror::Error;

#[derive(Error, Debug)]
pub enum DialogueError {
    #[error(transparent)]
    RegexError(#[from] fancy_regex::Error),
    #[error(transparent)]
    StorageError(#[from] InMemStorageError),
}
