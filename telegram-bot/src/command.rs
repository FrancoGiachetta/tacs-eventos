use teloxide::{Bot, macros::BotCommands, types::Message};

use crate::error::BotError;

#[derive(BotCommands, Clone)]
pub enum Command {}

pub async fn handle_command(msg: Message, bot: Bot, cmd: Command) -> Result<(), BotError> {
    Ok(())
}
