use crate::{
    auth::check_session, bot::BotResult, controller::Controller, dialogue::State, error::BotError,
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
    #[command(description = "Display help message.")]
    Help,
    #[command(
        description = "List the available events",
        // Tell teloxide how to parse filters.
        parse_with = parse_event_filters
    )]
    ListEvents(EventFilter),
}

/// Creates a handler for commands.
///
/// Each branch matches a command and executes its respective endpoint.
pub fn create_command_handler() -> UpdateHandler<BotError> {
    dptree::entry().filter_command::<Command>().branch(
        // A command can only be handled if the current State is State::Start.
        dptree::case![State::Authenticated]
            // Check if session is still valid. If not, retrieve new token.
            .map_async(check_session)
            .branch(dptree::case![Command::ListEvents(filters)].endpoint(event::handle_list_events))
            .branch(dptree::case![Command::Help].endpoint(handle_help_command)),
    )
}

async fn handle_help_command(msg_ctl: Controller) -> BotResult<()> {
    msg_ctl
        .send_message(&Command::descriptions().to_string())
        .await?;

    Ok(())
}
