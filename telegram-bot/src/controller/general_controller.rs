use std::sync::Arc;

use crate::{
    auth::in_memory_auth::InMemoryAuth,
    dialogue::{DialogueResult, MyDialogue, State},
    error::dialogue_error::DialogueError,
    request_client::RequestClient,
};
use teloxide::{
    Bot,
    payloads::SendMessageSetters,
    prelude::Requester,
    types::{ChatId, InlineKeyboardMarkup, Message, ParseMode},
};

/// Utility struct which centralizes most of the actions we need to perform.
#[derive(Clone)]
pub struct GeneralController {
    chat_id: ChatId,
    bot: Arc<Bot>,
    req_client: Arc<RequestClient>,
    msg: Message,
    dialogue: MyDialogue,
    auth: Arc<InMemoryAuth>,
}

impl GeneralController {
    pub fn new(
        msg: Message,
        bot: Arc<Bot>,
        req_client: Arc<RequestClient>,
        auth: Arc<InMemoryAuth>,
        dialogue: MyDialogue,
    ) -> Self {
        Self {
            chat_id: msg.chat.id,
            msg,
            bot,
            req_client,
            auth,
            dialogue,
        }
    }

    pub fn new_option(
        msg: Message,
        bot: Arc<Bot>,
        req_client: Arc<RequestClient>,
        auth: Arc<InMemoryAuth>,
        dialogue: MyDialogue,
    ) -> Option<Self> {
        Some(Self::new(msg, bot, req_client, auth, dialogue))
    }

    pub fn chat_id(&self) -> ChatId {
        self.chat_id
    }

    pub fn message(&self) -> Message {
        self.msg.clone()
    }

    pub fn request_client(&self) -> Arc<RequestClient> {
        self.req_client.clone()
    }

    pub fn auth(&self) -> Arc<InMemoryAuth> {
        self.auth.clone()
    }

    pub async fn send_message(&self, msg: &str) -> Result<(), teloxide::RequestError> {
        self.bot
            .send_message(self.chat_id, msg)
            .parse_mode(ParseMode::Html)
            .await?;

        Ok(())
    }

    pub async fn send_message_with_callback(
        &self,
        msg: &str,
        markup: InlineKeyboardMarkup,
    ) -> Result<(), teloxide::RequestError> {
        self.bot
            .send_message(self.chat_id(), msg)
            .parse_mode(ParseMode::Html)
            .reply_markup(markup)
            .await?;

        Ok(())
    }

    pub async fn send_error_message(&self, msg: &str) -> Result<(), teloxide::RequestError> {
        self.bot
            .send_message(self.chat_id, format!("❌ {}", msg))
            .parse_mode(ParseMode::Html)
            .await?;

        Ok(())
    }

    pub async fn get_dialogue_state(&self) -> DialogueResult<Option<State>> {
        self.dialogue
            .get()
            .await
            .map_err(|e| Box::new(DialogueError::from(e)))
    }

    pub async fn update_dialogue_state(&self, state: State) -> DialogueResult<()> {
        self.dialogue
            .update(state)
            .await
            .map_err(|e| Box::new(DialogueError::from(e)))
    }
}
