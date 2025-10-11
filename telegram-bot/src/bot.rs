use std::sync::Arc;

use teloxide::{
    Bot,
    dispatching::{UpdateFilterExt, dialogue as teloxide_dialogue},
    dptree,
    prelude::{Dispatcher, LoggingErrorHandler, Requester},
    types::Update,
    utils::command::BotCommands,
};
use tracing::{info, warn};

use crate::{
    command::{self, Command},
    controller::Controller,
    dialogue::{self, DialogueStorage, State},
    error::BotError,
    request_client::RequestClient,
};

pub type BotResult<T> = Result<T, BotError>;

pub async fn run() -> BotResult<()> {
    let bot = Arc::new(Bot::from_env());

    bot.set_my_commands(Command::bot_commands()).await?;

    let mut dispatcher = {
        let handler = teloxide_dialogue::enter::<Update, DialogueStorage, State, _>().branch(
            Update::filter_message()
                .filter_map(Controller::new)
                .branch(command::create_command_handler())
                .branch(dialogue::create_dialogue_handler()),
        );

        let req_client = RequestClient::new()?;

        Dispatcher::builder(bot.clone(), handler)
            .dependencies(dptree::deps![Arc::new(req_client), DialogueStorage::new()])
            .default_handler(|upd| async move {
                warn!("Unhandled update: {upd:?}");
            })
            .error_handler(LoggingErrorHandler::with_custom_text(
                "An error has occurred with the dispatcher",
            ))
            .enable_ctrlc_handler()
            .build()
    };

    info!("Initiating tacs-eventos-bot");

    dispatcher.dispatch().await;

    Ok(())
}
