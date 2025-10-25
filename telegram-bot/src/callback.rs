mod change_event_state;

use std::sync::Arc;
use teloxide::{
    payloads::AnswerCallbackQuerySetters, prelude::Requester, types::CallbackQuery, Bot,
};

use crate::bot::BotResult;
use crate::callback::change_event_state::open_event;
use crate::callback::Callback::CloseEvent;
use crate::controller::Controller;

const CLOSE_EVENT_PREFIX: &str = "close_event:";

/// Callbacks that can be invoked by the buttons in the chatbot inline keyboard
pub enum Callback {
    CloseEvent(String),
}

impl Callback {
    pub fn query(&self) -> String {
        match self {
            CloseEvent(event_id) => format!("{}{}", CLOSE_EVENT_PREFIX, event_id),
            // Other(event_id) => format!("{}{}", OTHER_PREFIX, param), // This is how another callback would be added
        }
    }
    fn extract_from_query(query: &String) -> Option<Self> {
        query
            .strip_prefix(CLOSE_EVENT_PREFIX)
            .map(|event_id| CloseEvent(event_id.to_string()))
        // .or(query.strip_prefix(OTHER_PREFIX).map(|param| OTHER(param.to_string())) // This is how another callback would be added
    }
    fn acknowledged_message(&self) -> String {
        match self {
            CloseEvent(event_id) => format!("Cerrando evento {}", event_id),
        }
    }
    fn action_confirmation_message(&self) -> String {
        match self {
            CloseEvent(event_id) => format!("Evento {} cerrado", event_id),
        }
    }
}

// TODO: refactor this to make the command matching directly in the dptree
/// Handles the callbacks invoked by the buttons in the chatbot
pub async fn handle_callback(
    bot: Arc<Bot>,
    query: CallbackQuery,
    controller: Controller,
) -> BotResult<()> {
    let callback = query.data.as_ref().and_then(Callback::extract_from_query);
    send_acknowledged(&bot, &query, &callback).await?;
    // Performs the action trigerred by the callback
    match &callback {
        Some(CloseEvent(event_id)) => open_event(controller, event_id.to_string()).await?,
        None => (),
    }
    send_action_confirmation(bot.as_ref(), &query, &callback).await?;
    Ok(())
}

async fn send_acknowledged(
    bot: &Bot,
    query: &CallbackQuery,
    callback: &Option<Callback>,
) -> BotResult<()> {
    let reply = callback.as_ref().map(Callback::acknowledged_message);
    if let Some(reply) = reply {
        bot.answer_callback_query(query.id.clone())
            .text(reply)
            .await?;
    };
    Ok(())
}

async fn send_action_confirmation(
    bot: &Bot,
    query: &CallbackQuery,
    callback: &Option<Callback>,
) -> BotResult<()> {
    let chat_id = &query.message.as_ref().map(|msg| msg.chat().id);
    let done_message = callback.as_ref().map(Callback::action_confirmation_message);
    if let Some((chat, message)) = chat_id.zip(done_message) {
        bot.send_message(chat, message).await?;
    }
    Ok(())
}
