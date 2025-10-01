use std::sync::Arc;

use teloxide::{
    Bot,
    types::{ChatId, Message},
};

use crate::request_client::RequestClient;

#[derive(Clone)]
pub struct MessageController {
    pub chat_id: ChatId,
    pub bot: Arc<Bot>,
    pub req_client: Arc<RequestClient>,
    pub msg: Message,
}

impl MessageController {
    pub fn new(msg: Message, bot: Arc<Bot>, req_client: Arc<RequestClient>) -> Self {
        Self {
            chat_id: msg.chat.id,
            msg,
            bot,
            req_client,
        }
    }
}
