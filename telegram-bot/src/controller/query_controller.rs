use std::sync::Arc;

use teloxide::{
    Bot, payloads::AnswerCallbackQuerySetters, prelude::Requester, types::CallbackQuery,
};

use crate::{
    auth::in_memory_auth::InMemoryAuth, controller::general_controller::GeneralController,
    dialogue::MyDialogue, request_client::RequestClient,
};

#[derive(Clone)]
pub struct QueryController {
    general_ctl: GeneralController,
    bot: Arc<Bot>,
    query: CallbackQuery,
}

impl QueryController {
    pub fn new(
        query: CallbackQuery,
        bot: Arc<Bot>,
        req_client: Arc<RequestClient>,
        auth: Arc<InMemoryAuth>,
        dialogue: MyDialogue,
    ) -> Option<Self> {
        let msg = query.message.clone()?.regular_message()?.clone();
        let general_ctl = GeneralController::new(msg, bot.clone(), req_client, auth, dialogue);

        Some(Self {
            general_ctl,
            query,
            bot,
        })
    }

    pub fn general_ctl(&self) -> &GeneralController {
        &self.general_ctl
    }

    pub fn query(&self) -> &CallbackQuery {
        &self.query
    }

    pub async fn reply_to_callback(&self, msg: &str) -> Result<(), teloxide::RequestError> {
        self.bot
            .answer_callback_query(self.query.id.clone())
            .text(msg)
            .await?;

        Ok(())
    }
}
