use teloxide::{
    dispatching::{
        UpdateHandler,
        dialogue::{self, InMemStorage},
    },
    dptree,
    prelude::Dialogue,
    types::Update,
};

use crate::error::BotError;

mod register;

pub type MyDialogue = Dialogue<State, InMemStorage<State>>;
pub type DialogueStorage = InMemStorage<State>;

#[derive(Clone, Default)]
pub enum State {
    #[default]
    Start,
    RegisterEmail,
    RegisterPassword {
        email: String,
    },
    ConfirmPassword {
        email: String,
        password: String,
    },
}

/// Creates a handler for commands.
///
/// Each branch matches a state and executes its respective endpoint.
pub fn create_dialogue_handler() -> UpdateHandler<BotError> {
    dialogue::enter::<Update, DialogueStorage, State, _>()
        .branch(dptree::case![State::Start])
        .branch(dptree::case![State::RegisterEmail].endpoint(register::handle_register_email))
        .branch(
            dptree::case![State::RegisterPassword { email }]
                .endpoint(register::handle_register_password),
        )
        .branch(
            dptree::case![State::ConfirmPassword { email, password }]
                .endpoint(register::handle_confirm_password),
        )
}
