mod change_event_state;

use std::sync::Arc;
use teloxide::{
    payloads::AnswerCallbackQuerySetters, prelude::Requester, types::CallbackQuery, Bot,
};

use crate::bot::BotResult;
use crate::callback::change_event_state::{close_event, open_event};
use crate::callback::Callback::{CloseEvent, OpenEvent};
use crate::controller::Controller;

const CLOSE_EVENT_PREFIX: &str = "close_event:";
const OPEN_EVENT_PREFIX: &str = "open_event:";

/// Callbacks that can be invoked by the buttons in the chatbot inline keyboard
pub enum Callback {
    CloseEvent(String),
    OpenEvent(String),
}

impl Callback {
    /// Constructs the query that is sent to the bot when the button is pressed.
    pub fn query(&self) -> String {
        match self {
            CloseEvent(event_id) => format!("{}{}", CLOSE_EVENT_PREFIX, event_id),
            OpenEvent(event_id) => format!("{}{}", OPEN_EVENT_PREFIX, event_id),
        }
    }
    /// Creates the Callback from the query sent to the bot (constructed by `query`)
    fn extract_from_query(query: &String) -> Option<Self> {
        query
            .strip_prefix(CLOSE_EVENT_PREFIX)
            .map(|event_id| CloseEvent(event_id.to_string()))
            .or(query
                .strip_prefix(OPEN_EVENT_PREFIX)
                .map(|event_id| OpenEvent(event_id.to_string())))
    }
    /// This message is sent to the user when the button has been pressed
    fn acknowledged_message(&self) -> String {
        match self {
            CloseEvent(event_id) => format!("Cerrando inscripciones a evento {}", event_id),
            OpenEvent(event_id) => format!("Abriendo inscripciones a evento {}", event_id),
        }
    }
    /// This message is sent to the user when the action has been performed
    fn action_confirmation_message(&self) -> String {
        match self {
            CloseEvent(event_id) => format!("Se cerraron las inscripciones al evento {}", event_id),
            OpenEvent(event_id) => format!("Se abrieron las inscripciones al evento {}", event_id),
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
        Some(CloseEvent(event_id)) => close_event(controller, event_id.to_string()).await?,
        Some(OpenEvent(event_id)) => open_event(controller, event_id.to_string()).await?,
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
