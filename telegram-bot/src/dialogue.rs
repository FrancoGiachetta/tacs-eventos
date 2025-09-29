use teloxide::{
    dispatching::{UpdateFilterExt, UpdateHandler, dialogue::ErasedStorage},
    prelude::Dialogue,
    types::Update,
};

use crate::{controller::MessageController, error::BotError};

pub type MyDialogue = Dialogue<State, ErasedStorage<State>>;
pub type DialogueStorage = ErasedStorage<State>;

#[derive(Clone, Default)]
pub enum State {
    #[default]
    Start,
}

/// Creates a handler for commands.
///
/// Each branch matches a state, and executes its respective endpoint.
pub fn create_dialogue_handler() -> UpdateHandler<BotError> {
    Update::filter_message()
        .filter_map(|msg, bot, req_client| Some(MessageController::new(msg, bot, req_client)))
}
