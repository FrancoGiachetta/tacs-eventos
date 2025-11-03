use crate::dialogue::UseCase::EnterCommand;
use crate::{
    auth::{check_session, Authenticator},
    bot::BotResult,
    controller::Controller,
    dialogue::{registration_dialogue::State as RegisterState, State},
    error::BotError,
    schemas::event::EventFilter,
};
use event::parse_event_filters;
use teloxide::{
    dispatching::{HandlerExt, UpdateHandler},
    dptree,
    utils::command::BotCommands,
};

mod create_event;
mod event;
mod inscriptions;

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
    #[command(description = "Listar inscripciones activas")]
    MyInscriptions,
    #[command(description = "Crear un nuevo evento")]
    CreateEvent,
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
            dptree::case![State::Authenticated(EnterCommand)]
                // Check if session is still valid. If not, retrieve new token.
                .map_async(check_session)
                .branch(
                    dptree::case![Command::ListEvents(filters)].endpoint(event::handle_list_events),
                )
                .branch(dptree::case![Command::MyInscriptions])
                .endpoint(inscriptions::handle_my_inscriptions)
                .branch(dptree::case![Command::CreateEvent])
                .endpoint(create_event::handle_create_event)
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
        ctl.update_dialogue_state(State::Authenticated(EnterCommand))
            .await?;
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
{}\n\n",
            username,
            &Command::descriptions()
        );
        ctl.send_message(&greetings_message).await?;
    } else {
        ctl.update_dialogue_state(State::Registration(RegisterState::CheckUser))
            .await?;
        ctl.send_message(
            "🔐 <b>Para comenzar, necesitás una cuenta</b>\n\n\
Elegí una opción:\n\n\
✍️ A) Registrarme\n\
🔑 B) Iniciar sesión\n\n\
¿Qué deseas hacer?",
        )
        .await?;
    }

    Ok(())
}

async fn handle_help_command(ctl: Controller) -> BotResult<()> {
    ctl.send_message(&Command::descriptions().to_string())
        .await?;

    Ok(())
}
