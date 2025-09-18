use std::sync::Arc;

use teloxide::{Bot, prelude::Requester, types::Message, utils::command::BotCommands};
use tracing::info;

use crate::error::BotError;

#[derive(BotCommands, Clone)]
#[command(rename_rule = "lowercase")]
pub enum Command {
    #[command(description = "Display help message.")]
    Help,
    #[command(description = "Register a with account.")]
    Register,
    #[command(description = "Login with an existing account.")]
    Login,
}

pub async fn handle_command(msg: Message, bot: Arc<Bot>, cmd: Command) -> Result<(), BotError> {
    match cmd {
        Command::Help => {
            info!("Execution /help!");

            bot.send_message(msg.chat.id, Command::descriptions().to_string())
                .await?;
        }
        Command::Register => {
            info!("Execution /register!");
        }
        Command::Login => {
            info!("Execution /login!");
        }
    }
    Ok(())
}
