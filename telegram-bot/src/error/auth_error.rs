use thiserror::Error;

use crate::error::request_client_error::RequestClientError;

#[derive(Debug, Error)]
pub enum AuthError {
    #[error(transparent)]
    RequestClientError(#[from] RequestClientError),
    #[error("Couln't find session associated to chat id {} was found", .0)]
    SessionNotFound(String),
    #[error("Session lock got poisoned due to: {}", .0)]
    SessionLockError(String),
}
