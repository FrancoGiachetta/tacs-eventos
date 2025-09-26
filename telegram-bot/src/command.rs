use std::sync::Arc;

use crate::{error::BotError, request_client::RequestClient, schemas::event::EventFilter};
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
        // Tell teloxide how to parse the command's arguments.
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
        Command::ListEvents(filters) => event::list_events(bot, &msg, &req_client, filters).await?,
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
