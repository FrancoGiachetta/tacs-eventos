use crate::{controller::MessageController, error::BotError, schemas::event::EventFilter};
use event::parse_event_filters;
use teloxide::{
    dispatching::{HandlerExt, UpdateFilterExt, UpdateHandler},
    dptree,
    prelude::Requester,
    types::Update,
    utils::command::BotCommands,
};

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
        // Tell teloxide how to parse filters.
        parse_with = parse_event_filters
    )]
    ListEvents(EventFilter),
}

/// Creates a handler for commands.
///
/// Each branch matches a command, and executes its respective endpoint.
pub fn create_command_handler() -> UpdateHandler<BotError> {
    Update::filter_message()
        .filter_map(|msg, bot, req_client| Some(MessageController::new(msg, bot, req_client)))
        .branch(
            dptree::entry()
                .filter_command::<Command>()
                .branch(dptree::case![Command::ListEvents(filters)].endpoint(event::list_events))
                .branch(dptree::case![Command::Register])
                .branch(dptree::case![Command::Login])
                .branch(dptree::case![Command::Help].endpoint(handle_help_command)),
        )
}

async fn handle_help_command(msg_ctl: MessageController) -> Result<(), BotError> {
    msg_ctl
        .bot
        .send_message(msg_ctl.chat_id, Command::descriptions().to_string())
        .await?;
    Ok(())
}
