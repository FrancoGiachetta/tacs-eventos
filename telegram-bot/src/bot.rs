use std::sync::Arc;

use teloxide::{
    dispatching::{dialogue as teloxide_dialogue, UpdateFilterExt},
    dptree,
    prelude::{Dispatcher, LoggingErrorHandler, Requester},
    types::Update,
    utils::command::BotCommands,
    Bot,
};
use tracing::{info, warn};

use crate::{
    auth::in_memory_auth::InMemoryAuth,
    command::{self, Command},
    controller::Controller,
    dialogue::{self, DialogueStorage, State},
    error::BotError,
    request_client::RequestClient,
};

use crate::callback;

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
        )
        .branch(
            Update::filter_callback_query()
                .filter_map(Controller::new)
                .endpoint(callback::handle_callback),
        );

        let req_client = Arc::new(RequestClient::new()?);
        let authenticator = Arc::new(InMemoryAuth::new(req_client.clone()));

        Dispatcher::builder(bot.clone(), handler)
            .dependencies(dptree::deps![
                req_client,
                DialogueStorage::new(),
                authenticator
            ])
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
