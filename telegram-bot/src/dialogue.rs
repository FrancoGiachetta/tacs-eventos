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
    bot::BotResult, command::Command, controller::Controller,
    dialogue::user::check_user_auth_selection, error::BotError,
};

mod user;

pub type MyDialogue = Dialogue<State, InMemStorage<State>>;
pub type DialogueStorage = InMemStorage<State>;

#[derive(Clone, Default, Debug)]
pub enum State {
    #[default]
    Start,
    CheckUser,
    RegisterEmail,
    RegisterPassword {
        email: String,
    },
    ConfirmPassword {
        email: String,
        password: String,
    },
    LoginEmail,
    LoginPassword {
        email: String,
    },
    Authenticated,
}

/// Creates a handler for commands.
///
/// Each branch matches a state and executes its respective endpoint.
pub fn create_dialogue_handler() -> UpdateHandler<BotError> {
    dialogue::enter::<Update, DialogueStorage, State, _>()
        .branch(dptree::case![State::Start].endpoint(greetings))
        // Check user auth method.
        .branch(dptree::case![State::CheckUser].endpoint(check_user_auth_selection))
        // Register user.
        .branch(dptree::case![State::RegisterEmail].endpoint(user::handle_register_email))
        .branch(
            dptree::case![State::RegisterPassword { email }]
                .endpoint(user::handle_register_password),
        )
        .branch(
            dptree::case![State::ConfirmPassword { email, password }]
                .endpoint(user::handle_confirm_password),
        )
        // Login user.
        .branch(dptree::case![State::LoginEmail].endpoint(user::handle_register_email))
        .branch(
            dptree::case![State::LoginPassword { email }].endpoint(user::handle_register_password),
        )
}

async fn greetings(ctl: Controller) -> BotResult<()> {
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
"¡Hola, {}! 👋 Bienvenido al bot de TACS Eventos 🎉

Soy tu asistente para descubrir y participar en eventos de forma rápida y sencilla. A través de mí podrás:

✅ Buscar eventos por precio, fecha, categoría o palabras clave 🔍.
✅ Ver los detalles de cada evento 📋.
✅ Inscribirte a los eventos que te interesen 🎟️.
✅ Consultar tus inscripciones activas 📅.

Los comandos disponibles son:

{}

Para comenzar, necesitas tener una cuenta activa. 🔐
Por favor, elige una opción para continuar:

A) Iniciar sesión 🔑
B) Registrarme ✍️

¿Qué te gustaría hacer? 💬
", username, &Command::descriptions());

    ctl.send_message(&greetings_message).await?;
    ctl.update_dialogue_state(State::CheckUser).await?;

    Ok(())
}
