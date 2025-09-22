use std::sync::Arc;

use chrono::NaiveDate;
use teloxide::{
    Bot,
    prelude::Requester,
    types::Message,
    utils::command::{BotCommands, ParseError},
};
use tracing::info;

use crate::{error::BotError, reques_client::RequestClient};

#[derive(BotCommands, Clone)]
#[command(rename_rule = "lowercase")]
pub enum Command {
    #[command(description = "Display help message.")]
    Help,
    #[command(description = "Register a with account.")]
    Register,
    #[command(description = "Login with an existing account.")]
    Login,
    #[command(
        description = "List the available events",
        parse_with = parse_event_filters
    )]
    ListEvents(EventFilter),
}

#[derive(Debug, Clone, PartialEq)]
pub struct EventFilter {
    pub max_price: Option<f32>,
    pub min_price: Option<f32>,
    pub max_date: Option<NaiveDate>,
    pub min_date: Option<NaiveDate>,
    pub category: Option<String>,
    pub key_words: Option<Vec<String>>,
}

pub async fn handle_command(
    msg: Message,
    bot: Arc<Bot>,
    cmd: Command,
    req_client: Arc<RequestClient>,
) -> Result<(), BotError> {
    match cmd {
        Command::ListEvents(filters) => {
            info!("Listing events!");

            let events_list = req_client.send_get_events_list_request(filters);

            todo!()
        }
        Command::Register => {
            info!("Execution /register!");
        }
        Command::Login => {
            info!("Execution /login!");
        }
        Command::Help => {
            info!("Execution /help!");

            bot.send_message(msg.chat.id, Command::descriptions().to_string())
                .await?;
        }
    }

    Ok(())
}

fn parse_event_filters(input: String) -> Result<(EventFilter,), ParseError> {
    todo!()
}
