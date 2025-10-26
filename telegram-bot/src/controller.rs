use std::sync::Arc;

use crate::{
    auth::in_memory_auth::InMemoryAuth,
    bot::BotResult,
    dialogue::{DialogueResult, MyDialogue, State},
    error::dialogue_error::DialogueError,
    request_client::RequestClient,
};
use teloxide::prelude::CallbackQuery;
use teloxide::{
    payloads::SendMessageSetters,
    prelude::Requester,
    types::{ChatId, InlineKeyboardMarkup, Message, ParseMode},
    Bot,
};

/// Utility struct which centralizes most of the actions we need to perform.
#[derive(Clone)]
pub struct Controller {
    chat_id: ChatId,
    bot: Arc<Bot>,
    req_client: Arc<RequestClient>,
    msg: Message,
    dialogue: MyDialogue,
    auth: Arc<InMemoryAuth>,
}

impl Controller {
    pub fn new(
        msg: Message,
        bot: Arc<Bot>,
        req_client: Arc<RequestClient>,
        auth: Arc<InMemoryAuth>,
        dialogue: MyDialogue,
    ) -> Option<Self> {
        Some(Self {
            chat_id: msg.chat.id,
            msg,
            bot,
            req_client,
            auth,
            dialogue,
        })
    }

    pub fn new_from_callback_query(
        callback_query: CallbackQuery,
        bot: Arc<Bot>,
        req_client: Arc<RequestClient>,
        auth: Arc<InMemoryAuth>,
        dialogue: MyDialogue,
    ) -> Option<Self> {
        let chat_id = callback_query.clone().message.map(|msg| msg.chat().id)?;
        let msg = callback_query.message?.regular_message()?.clone();
        Some(Self {
            chat_id,
            msg,
            bot,
            req_client,
            auth,
            dialogue,
        })
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

    pub async fn send_message(&self, msg: &str) -> BotResult<()> {
        self.bot
            .send_message(self.chat_id, msg)
            .parse_mode(ParseMode::Html)
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

    pub async fn send_message_with_markup(
        &self,
        msg: &str,
        markup: InlineKeyboardMarkup,
    ) -> BotResult<()> {
        self.bot
            .send_message(self.chat_id, msg)
            .parse_mode(ParseMode::Html)
            .reply_markup(markup)
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

    pub async fn reset_dialogue(&self) -> DialogueResult<()> {
        self.dialogue
            .reset()
            .await
            .map_err(|e| Box::new(DialogueError::from(e)))
    }
}
