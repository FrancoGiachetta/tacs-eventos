use crate::{
    auth::Authenticator,
    bot::BotResult,
    controller::Controller,
    dialogue::State as GlobalState,
    error::{dialogue_error::DialogueError, BotError},
    schemas::user::UserOut,
};
use fancy_regex::Regex;
use lazy_static::lazy_static;
use teloxide::{
    dispatching::{UpdateFilterExt, UpdateHandler},
    dptree,
    types::Update,
};
use tracing::error;

#[derive(Clone, Debug)]
pub enum State {
    CheckUser,
    RegisterEmail,
    RegisterPassword { email: String },
    ConfirmPassword { email: String, password: String },
    LoginEmail,
    LoginPassword { email: String },
}

pub fn schema() -> UpdateHandler<BotError> {
    Update::filter_message().branch(
        dptree::case![GlobalState::Registration(s)]
            .branch(dptree::case![State::CheckUser].endpoint(check_user_auth_selection))
            .branch(dptree::case![State::RegisterEmail].endpoint(handle_register_email))
            .branch(
                dptree::case![State::RegisterPassword { email }].endpoint(handle_register_password),
            )
            .branch(
                dptree::case![State::ConfirmPassword { email, password }]
                    .endpoint(handle_confirm_password),
            )
            .branch(dptree::case![State::LoginEmail].endpoint(handle_login_email))
            .branch(dptree::case![State::LoginPassword { email }].endpoint(handle_login_password)),
    )
}

// User first choice, either registering a new account logging with an existing
// one.

pub async fn check_user_auth_selection(ctl: Controller) -> BotResult<()> {
    match &ctl.message().text().map(|m| m.to_lowercase()) {
        Some(m) if m == "a" => {
            let message = "<b>¡Perfecto! 🎉</b>\n\n\
Elegiste <i>crear una cuenta nueva</i>.\n\n\
<b>Para continuar:</b>\n\
Por favor, envíame tu dirección de email 📧";
            ctl.send_message(message).await?;
            ctl.update_dialogue_state(GlobalState::Registration(State::RegisterEmail))
                .await?
        }
        Some(m) if m == "b" => {
            let message = "<b>✅ ¡Genial!</b>\n\n\
Veo que ya tenés una cuenta.\n\n\
<b>Para acceder:</b>\n\
Envíame tu email 📧";
            ctl.send_message(message).await?;
            ctl.update_dialogue_state(GlobalState::Registration(State::LoginEmail))
                .await?
        }
        _ => {
            let msg = "¡Esa no es una respueta valida! Para comenzar, necesitas tener una cuenta activa. 🔐
Por favor, elige una opción para continuar:

A) Registrarme ✍️
B) Iniciar sesión 🔑

¿Qué te gustaría hacer? 💬";

            ctl.send_message(msg).await?;
        }
    }

    Ok(())
}

// User registration dialogue.

lazy_static! {
    static ref EMAIL_REGEX: Regex = Regex::new(r"^[^\s@]+@[^\s@]+\.[^\s@]+$").unwrap();
    static ref PASSWORD_REGEX: Regex = Regex::new(r"^(?=.*[A-Za-z])(?=.*\d).{8,72}$").unwrap();
}

pub async fn handle_register_email(ctl: Controller) -> BotResult<()> {
    match ctl.message().text() {
        Some(email)
            if EMAIL_REGEX
                .is_match(email)
                .map_err(|e| Box::new(DialogueError::from(e)))? =>
        {
            ctl.send_message(
                "<b>Genial, ya casi estamos 🎯</b>\n\n\
Ahora necesito tu <b>contraseña</b> 🔒",
            )
            .await?;

            ctl.update_dialogue_state(GlobalState::Registration(State::RegisterPassword {
                email: email.to_string(),
            }))
            .await?
        }
        _ => {
            ctl.send_message(
                "<b>Email inválido</b>\n\n\
Por favor, envíame un email correcto:\n\n\
<code>usuario@gmail.com</code>",
            )
            .await?;
        }
    }

    Ok(())
}

pub async fn handle_register_password(ctl: Controller, email: String) -> BotResult<()> {
    match ctl.message().text() {
        Some(password)
            if PASSWORD_REGEX
                .is_match(password)
                .map_err(|e| Box::new(DialogueError::from(e)))? =>
        {
            ctl.send_message(
                "<b>¡Bien! 👌</b>\n\n\
Para confirmar, enviame la <b>contraseña nuevamente</b> 🔒",
            )
            .await?;

            ctl.update_dialogue_state(GlobalState::Registration(State::ConfirmPassword {
                email,
                password: password.to_string(),
            }))
            .await?;
        }
        _ => {
            ctl.send_error_message(
                "<b>Contraseña inválida</b>\n\n\
Tu contraseña debe tener:\n\
  • Mínimo <b>8 caracteres</b>\n\
  • Al menos <b>una letra</b>\n\
  • Al menos <b>un número</b>\n\n\
<i>Intentá de nuevo</i> 🔒",
            )
            .await?;
        }
    }

    Ok(())
}

pub async fn handle_confirm_password(
    ctl: Controller,
    (email, password): (String, String),
) -> BotResult<()> {
    match ctl.message().text() {
        Some(confirm_pass) if password == confirm_pass => {
            let token = ctl
                .request_client()
                .send_user_registration_request(UserOut {
                    email: email.clone(),
                    password: password.clone(),
                    user_type: Some("USUARIO".to_string()),
                })
                .await?;

            // Create the new session.
            ctl.auth()
                .new_session(ctl.chat_id(), password, token)
                .await?;

            ctl.send_message(
                "<b>✅ ¡Listo!</b>\n\n\
Tu cuenta fue creada <i>correctamente</i>.\n\
<b>Bienvenido</b> 👋",
            )
            .await?;
            // Change to State::Authenticated so that the user can perform commands.
            ctl.update_dialogue_state(GlobalState::Authenticated)
                .await?;
        }
        _ => {
            ctl.send_error_message(
                "<b>Las contraseñas no coinciden</b>\n\n\
Asegurate de escribir la <b>misma contraseña</b> en ambos campos.\n\n\
<i>Intentá de nuevo</i> 🔒",
            )
            .await?;
        }
    }

    Ok(())
}

// User Login Dialogue.

pub async fn handle_login_email(ctl: Controller) -> BotResult<()> {
    match ctl.message().text() {
        Some(email)
            if EMAIL_REGEX
                .is_match(email)
                .map_err(|e| Box::new(DialogueError::from(e)))? =>
        {
            ctl.send_message(
                "<b>Genial, ya casi estamos 🎯</b>\n\n\
Ahora necesito tu <b>contraseña</b> 🔒",
            )
            .await?;

            ctl.update_dialogue_state(GlobalState::Registration(State::LoginPassword {
                email: email.to_string(),
            }))
            .await?
        }
        _ => {
            ctl.send_message(
                "<b>Email inválido</b>\n\n\
Por favor, envíame un email correcto:\n\n\
<code>usuario@gmail.com</code>",
            )
            .await?;
        }
    }

    Ok(())
}

pub async fn handle_login_password(ctl: Controller, email: String) -> BotResult<()> {
    let password = ctl.message().text().unwrap_or_default().to_string();

    let login_result = ctl
        .request_client()
        .send_user_login_request(UserOut {
            email,
            password: password.clone(),
            user_type: None,
        })
        .await;

    match login_result {
        Ok(token) => {
            // Create the new session.
            ctl.auth()
                .new_session(ctl.chat_id(), password, token)
                .await?;

            ctl.send_message(
                "<b>✅ ¡Ya estás logueado!</b>\n\n\
<i>Todo listo para empezar</i> 🎉",
            )
            .await?;

            // Change to State::Authenticated so that the user can perform commands.
            ctl.update_dialogue_state(GlobalState::Authenticated)
                .await?;
        }
        Err(e) => {
            error!("Error al loguearse: {}", e);
            ctl.send_error_message("<b>Error de inicio de sesión</b>\n")
                .await?;
        }
    }

    Ok(())
}
