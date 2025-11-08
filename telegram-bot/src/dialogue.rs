use teloxide::{
    dispatching::{
        UpdateHandler,
        dialogue::{self, InMemStorage},
    },
    dptree,
    prelude::Dialogue,
    types::Update,
    utils::command::BotCommands,
};

use crate::{
    bot::BotResult,
    command::Command,
    controller::general_controller::GeneralController,
    error::{BotError, dialogue_error::DialogueError},
};

use crate::dialogue::event_creation_dialogue::EventCreationStep;
use registration_dialogue::State as RegisterState;

pub(crate) mod event_creation_dialogue;
pub mod registration_dialogue;

pub type DialogueResult<T> = Result<T, Box<DialogueError>>;

pub type MyDialogue = Dialogue<State, InMemStorage<State>>;
pub type DialogueStorage = InMemStorage<State>;

#[derive(Clone, Default, Debug)]
pub enum State {
    #[default]
    Start,
    /// The user is performing the login
    Registration(RegisterState),
    /// The user is logged-in and can use the app
    Authenticated(UseCase),
}

#[derive(Clone, Debug)]
pub enum UseCase {
    /// The user can enter a new command
    EnterCommand,
    /// The user has entered the command for event creation, and must provide the neccesary information for creating a new event
    EventCreation(EventCreationStep),
}

/// Creates a handler for commands.
///
/// Each branch matches a state and executes its respective endpoint.
pub fn create_dialogue_handler() -> UpdateHandler<BotError> {
    dialogue::enter::<Update, DialogueStorage, State, _>()
        .branch(dptree::case![State::Start].endpoint(greetings))
        // Check user auth method.
        .branch(registration_dialogue::schema())
        .branch(event_creation_dialogue::schema())
}

async fn greetings(ctl: GeneralController) -> BotResult<()> {
    // This bot is supposed to be used by individual users (not channels),
    // there's no way the sender or the user's name are None.
    let username = ctl
        .message()
        .from
        .ok_or(BotError::CustomError(
            "Couldn't find message's sender".to_string(),
        ))?
        .full_name();
    let greetings_message = format!(
        "👋 ¡Hola, {}!\n\n\
Bienvenido al bot de TACS Eventos 🎉\n\n\
Soy tu asistente para descubrir y participar en eventos.\n\n\
<b>¿Qué podés hacer?</b>\n\n\
🔍 Buscar eventos por precio, fecha o categoría\n\
📋 Ver detalles de cada evento\n\
🎟️ Inscribirte a los que te interesen\n\
📅 Consultar tus inscripciones\n\n\
<b>Comandos disponibles:</b>\n\n\
{}\n\n\
🔐 <b>Para comenzar, necesitás una cuenta</b>\n\n\
Elegí una opción:\n\n\
✍️ A) Registrarme\n\
🔑 B) Iniciar sesión\n\n\
¿Qué deseas hacer?",
        username,
        &Command::descriptions()
    );
    ctl.send_message(&greetings_message).await?;
    ctl.update_dialogue_state(State::Registration(RegisterState::CheckUser))
        .await?;

    Ok(())
}
