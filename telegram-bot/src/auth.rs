use teloxide::types::ChatId;

use crate::{
    controller::general_controller::GeneralController, error::auth_error::AuthError,
    schemas::user::Token,
};
use async_trait::async_trait;

pub mod in_memory_auth;

#[async_trait]
pub trait Authenticator {
    fn validate_session(&self, chat_id: &ChatId) -> Result<bool, AuthError>;
    async fn reset_token(&self, chat_id: &ChatId) -> Result<(), AuthError>;
    async fn new_session(
        &self,
        chat_id: ChatId,
        password: String,
        token: Token,
    ) -> Result<(), AuthError>;
}

/// Checks wether the session associated to the current chat id is still valid.
///
/// If the session is not valid, then it resets the token to make is valid
/// again.
/// Returns an `AuthError` if the session does not exist.
pub async fn check_session(ctl: GeneralController) -> Result<(), AuthError> {
    let authenticator = ctl.auth();

    if !authenticator.validate_session(&ctl.chat_id())? {
        authenticator.reset_token(&ctl.chat_id()).await?;
    }

    Ok(())
}
