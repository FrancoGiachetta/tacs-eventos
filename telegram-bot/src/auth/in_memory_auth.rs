use std::{
    collections::HashMap,
    sync::{Arc, RwLock},
};

use async_trait::async_trait;
use chrono::Utc;
use teloxide::types::ChatId;
use tracing::info;

use crate::{
    auth::Authenticator,
    error::auth_error::AuthError,
    request_client::RequestClient,
    schemas::user::{Session, Token, UserIn, UserOut},
};

pub struct InMemoryAuth {
    request_client: Arc<RequestClient>,
    sessions: RwLock<HashMap<ChatId, Session>>,
}

impl InMemoryAuth {
    pub fn new(request_client: Arc<RequestClient>) -> Self {
        Self {
            request_client,
            sessions: RwLock::new(HashMap::new()),
        }
    }

    /// Gets the session token associated to a chat.
    pub fn get_session_token(&self, chat_id: &ChatId) -> Result<String, AuthError> {
        let session = self
            .sessions
            .read()
            .map_err(|e| AuthError::SessionLockError(e.to_string()))?;

        Ok(session
            .get(chat_id)
            .ok_or(AuthError::SessionNotFound(chat_id.to_string()))?
            .token
            .token
            .clone())
    }
}

#[async_trait]
impl Authenticator for InMemoryAuth {
    async fn new_session(
        &self,
        chat_id: ChatId,
        password: String,
        token: Token,
    ) -> Result<(), AuthError> {
        info!("Creating new session");

        let UserIn { id, email, .. } = self.request_client.send_get_me(&token.token).await?;

        let session = Session {
            user_id: id,
            email,
            password,
            token,
            is_active: true,
        };

        self.sessions
            .write()
            .map_err(|e| AuthError::SessionNotFound(e.to_string()))?
            .insert(chat_id, session);

        Ok(())
    }

    async fn reset_token(&self, chat_id: &ChatId) -> Result<(), AuthError> {
        info!("Reseting Token");

        let (email, password) = {
            let sessions = self
                .sessions
                .read()
                .map_err(|e| AuthError::SessionLockError(e.to_string()))?;

            let Session {
                email, password, ..
            } = sessions
                .get(chat_id)
                .ok_or(AuthError::SessionNotFound(chat_id.to_string()))?;

            (email.clone(), password.clone())
        };

        let token = self
            .request_client
            .send_user_login_request(UserOut {
                email: email.to_owned(),
                password: password.clone(),
                user_type: None,
            })
            .await?;

        self.sessions
            .write()
            .map_err(|e| AuthError::SessionLockError(e.to_string()))?
            .get_mut(chat_id)
            .ok_or(AuthError::SessionNotFound(chat_id.to_string()))?
            .token = token;

        Ok(())
    }

    /// Validates wether a given token is still valid.
    ///
    /// If there's no session associated to the chat id, it returns false.
    fn validate_session(&self, chat_id: &ChatId) -> Result<bool, AuthError> {
        if let Some(session) = self
            .sessions
            .read()
            .map_err(|e| AuthError::SessionNotFound(e.to_string()))?
            .get(chat_id)
        {
            Ok(session.is_active && session.token.expires_at > Utc::now())
        } else {
            Ok(false)
        }
    }
}
