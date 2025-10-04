use tracing::info;

use crate::{bot::BotResult, controller::Controller};

/// Begins user registration.
///
/// Updates the dialogue's state to begin registration.
pub async fn handle_register(ctl: Controller) -> BotResult<()> {
    info!("Starting registration!");

    ctl.send_message("Para registrarte, primero voy a necesitar que me digas to email")
        .await?;

    Ok(())
}
