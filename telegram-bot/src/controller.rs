use std::sync::Arc;

use teloxide::{
    Bot,
    prelude::Requester,
    types::{ChatId, Message},
};

use crate::{bot::BotResult, dialogue::MyDialogue, request_client::RequestClient};

/// Utility struct which centralizes most of the actions we need to perform.
#[derive(Clone)]
pub struct Controller {
    chat_id: ChatId,
    bot: Arc<Bot>,
    req_client: Arc<RequestClient>,
    msg: Message,
    dialogue: MyDialogue,
}

impl Controller {
    pub fn new(
        msg: Message,
        bot: Arc<Bot>,
        req_client: Arc<RequestClient>,

        dialogue: MyDialogue,
    ) -> Option<Self> {
        Some(Self {
            chat_id: msg.chat.id,
            msg,
            bot,
            req_client,
            dialogue,
        })
    }

    pub fn message(&self) -> Message {
        self.msg.clone()
    }

    pub fn request_client(&self) -> Arc<RequestClient> {
        self.req_client.clone()
    }

    pub async fn send_message(&self, msg: &str) -> BotResult<()> {
        self.bot.send_message(self.chat_id, msg).await?;

        Ok(())
    }

    pub async fn send_error_message(&self, msg: &str) -> BotResult<()> {
        self.bot
            .send_message(self.chat_id, format!("❌ {}", msg))
            .await?;

        Ok(())
    }
}
