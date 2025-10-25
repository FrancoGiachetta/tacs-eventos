use crate::{
    auth::{Authenticator, check_session},
    bot::BotResult,
    controller::Controller,
    dialogue::State,
    error::BotError,
    schemas::event::EventFilter,
};
use event::parse_event_filters;
use teloxide::{
    dispatching::{HandlerExt, UpdateHandler},
    dptree,
    utils::command::BotCommands,
};

mod event;

#[derive(BotCommands, Clone)]
#[command(rename_rule = "lowercase")]
pub enum Command {
    #[command(description = "Mostrar mensaje de ayuda")]
    Help,
    #[command(description = "Resetear dialogo")]
    Reset,
    #[command(
        description = "Listar los eventos disponibles",
        // Tell teloxide how to parse filters.
        parse_with = parse_event_filters
    )]
    ListEvents(EventFilter),
}

/// Creates a handler for commands.
///
/// Each branch matches a command and executes its respective endpoint.
pub fn create_command_handler() -> UpdateHandler<BotError> {
    dptree::entry()
        .filter_command::<Command>()
        .branch(dptree::case![Command::Reset].endpoint(reset))
        .branch(
            // A command can only be handled if the current State is State::Start.
            dptree::case![State::Authenticated]
                // Check if session is still valid. If not, retrieve new token.
                .map_async(check_session)
                .branch(
                    dptree::case![Command::ListEvents(filters)].endpoint(event::handle_list_events),
                )
                .branch(dptree::case![Command::Help].endpoint(handle_help_command)),
        )
}

/// Reset dialogue state.
///
/// Checks wether there's a session associated to the chat. If there's one,
/// then it resets the dialogue to `State::Authenticated`. If not, it resets it
/// to `State::CheckUser` for the user to create a session.
async fn reset(ctl: Controller) -> BotResult<()> {
    let session_is_valid = ctl.auth().validate_session(&ctl.chat_id())?;

    if session_is_valid {
        ctl.update_dialogue_state(State::Authenticated).await?;
    } else {
        ctl.update_dialogue_state(State::CheckUser).await?;
    }

    Ok(())
}

async fn handle_help_command(ctl: Controller) -> BotResult<()> {
    ctl.send_message(&Command::descriptions().to_string())
        .await?;

    Ok(())
}
