use std::sync::Arc;

use crate::{error::BotError, request_client::RequestClient, schemas::evento::EventFilter};
use event::parse_event_filters;
use teloxide::{Bot, prelude::Requester, types::Message, utils::command::BotCommands};
use tracing::info;

mod event;

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

pub async fn handle_command(
    msg: Message,
    bot: Arc<Bot>,
    cmd: Command,
    req_client: Arc<RequestClient>,
) -> Result<(), BotError> {
    match cmd {
        Command::ListEvents(filters) => {
            info!("Listing list_events!");

            let events_list = req_client.send_get_events_list_request(filters).await?;

            bot.send_message(msg.chat.id, format!("{events_list:?}"))
                .await?;
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
