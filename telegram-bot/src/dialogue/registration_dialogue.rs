use fancy_regex::Regex;
use lazy_static::lazy_static;
use tracing::error;

use crate::{
    auth::Authenticator, bot::BotResult, controller::Controller, dialogue::State,
    error::dialogue_error::DialogueError, schemas::user::UserOut,
};

#[derive(Clone, Debug)]
pub enum RegisterState {
    CheckUser,
    RegisterEmail,
    RegisterPassword { email: String },
    ConfirmPassword { email: String, password: String },
    LoginEmail,
    LoginPassword { email: String },
}

pub async fn handle_user_registration(ctl: Controller, state: RegisterState) -> BotResult<()> {
    match state {
        RegisterState::CheckUser => check_user_auth_selection(ctl).await,
        RegisterState::RegisterEmail | RegisterState::LoginEmail => {
            handle_register_email(ctl, state).await
        }
        RegisterState::RegisterPassword { .. } | RegisterState::LoginPassword { .. } => {
            handle_register_password(ctl, state).await
        }
        RegisterState::ConfirmPassword { email, password } => {
            handle_confirm_password(ctl, email, password).await
        }
    }
}

// User first choice, either registering a new account logging with an existing
// one.

pub async fn check_user_auth_selection(ctl: Controller) -> BotResult<()> {
    let msg = "Para comenzar, necesitas tener una cuenta activa. 🔐
Por favor, elige una opción para continuar:

A) Registrarme ✍️
B) Iniciar sesión 🔑

¿Qué te gustaría hacer? 💬";

    ctl.send_message(msg).await?;

    match &ctl.message().text().map(|m| m.to_lowercase()) {
        Some(m) if m == "a" => {
            let message = "<b>¡Perfecto! 🎉</b>\n\n\
Elegiste <i>crear una cuenta nueva</i>.\n\n\
<b>Para continuar:</b>\n\
Por favor, envíame tu dirección de email 📧";
            ctl.send_message(message).await?;
            ctl.update_dialogue_state(State::Registration(RegisterState::RegisterEmail))
                .await?
        }
        Some(m) if m == "b" => {
            let message = "<b>✅ ¡Genial!</b>\n\n\
Veo que ya tenés una cuenta.\n\n\
<b>Para acceder:</b>\n\
Envíame tu email 📧";
            ctl.send_message(message).await?;
            ctl.update_dialogue_state(State::Registration(RegisterState::LoginEmail))
                .await?
        }
        _ => {}
    }

    Ok(())
}

// User registration dialogue.

lazy_static! {
    static ref EMAIL_REGEX: Regex = Regex::new(r"^[^\s@]+@[^\s@]+\.[^\s@]+$").unwrap();
    static ref PASSWORD_REGEX: Regex = Regex::new(r"^(?=.*[A-Za-z])(?=.*\d).{8,72}$").unwrap();
}

pub async fn handle_register_email(ctl: Controller, state: RegisterState) -> BotResult<()> {
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

            match state {
                RegisterState::RegisterEmail => {
                    ctl.update_dialogue_state(State::Registration(
                        RegisterState::RegisterPassword {
                            email: email.to_string(),
                        },
                    ))
                    .await?
                }
                RegisterState::LoginEmail => {
                    ctl.update_dialogue_state(State::Registration(RegisterState::LoginPassword {
                        email: email.to_string(),
                    }))
                    .await?
                }
                _ => {
                    error!(
                        "Impossible state! {:?}. Should be RegisterEmail or LoginEmail",
                        state
                    );
                    ctl.reset_dialogue().await?;
                }
            }
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

pub async fn handle_register_password(ctl: Controller, state: RegisterState) -> BotResult<()> {
    match ctl.message().text() {
        Some(password)
            if PASSWORD_REGEX
                .is_match(password)
                .map_err(|e| Box::new(DialogueError::from(e)))? =>
        {
            match state {
                RegisterState::RegisterPassword { email } => {
                    ctl.send_message(
                        "<b>¡Bien! 👌</b>\n\n\
Para confirmar, enviame la <b>contraseña nuevamente</b> 🔒",
                    )
                    .await?;

                    ctl.update_dialogue_state(State::Registration(
                        RegisterState::ConfirmPassword {
                            email,
                            password: password.to_string(),
                        },
                    ))
                    .await?;
                }
                RegisterState::LoginPassword { email } => {
                    let token = ctl
                        .request_client()
                        .send_user_login_request(UserOut {
                            email: email,
                            password: password.to_string(),
                            user_type: None,
                        })
                        .await?;

                    // Create the new session.
                    ctl.auth()
                        .new_session(ctl.chat_id(), password.to_string(), token)
                        .await?;

                    ctl.send_message(
                        "<b>✅ ¡Ya estás logueado!</b>\n\n\
<i>Todo listo para empezar</i> 🎉",
                    )
                    .await?;

                    // Change to State::Authenticated so that the user can perform commands.
                    ctl.update_dialogue_state(State::Authenticated).await?;
                }
                _ => {
                    let error_msg = "<b>Oops, algo salió mal</b>\n\n\
No pudimos completar tu autenticación.\n\n\
<b>No te preocupes, elegí qué querés hacer:</b>\n\n\
🆕 <b>A)</b> Crear una cuenta nueva\n\
🔐 <b>B)</b> Iniciar sesión con tu cuenta existente";

                    error!(
                        "Impossible state! {:?}. Should be RegisterEmail or LoginEmail",
                        state
                    );

                    ctl.send_error_message(error_msg).await?;
                    ctl.update_dialogue_state(State::Registration(RegisterState::CheckUser))
                        .await?;
                }
            }
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
    email: String,
    password: String,
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
            ctl.update_dialogue_state(State::Authenticated).await?;
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
