use teloxide::prelude::Requester;
use tracing::info;

use crate::{
    bot::BotResult,
    controller::MessageController,
    dialogue::{MyDialogue, State},
};

/// Begins user registration.
///
/// Updates the dialogue's state to begin registration.
pub async fn handle_register(msg_ctl: MessageController, dialogue: MyDialogue) -> BotResult {
    info!("Starting registration!");

    msg_ctl
        .bot
        .send_message(
            msg_ctl.chat_id,
            "Para registrarte, primero voy a necesitar que me digas to email",
        )
        .await?;
    dialogue.update(State::RegisterEmail).await?;

    Ok(())
}
