use crate::{controller::MessageController, error::BotError, schemas::event::EventFilter};
use event::parse_event_filters;
use teloxide::{prelude::Requester, utils::command::BotCommands};
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

pub async fn handle_command(msg_ctl: MessageController, cmd: Command) -> Result<(), BotError> {
    match cmd {
        Command::ListEvents(filters) => event::list_events(msg_ctl, filters).await?,
        Command::Register => {
            info!("Execution /register!");
        }
        Command::Login => {
            info!("Execution /login!");
        }
        Command::Help => {
            info!("Execution /help!");

            msg_ctl
                .bot
                .send_message(msg_ctl.chat_id, Command::descriptions().to_string())
                .await?;
        }
    }

    Ok(())
}
