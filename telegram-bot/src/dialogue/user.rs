use fancy_regex::Regex;
use lazy_static::lazy_static;
use tracing::error;

use crate::{
    auth::Authenticator, bot::BotResult, controller::Controller, dialogue::State,
    error::dialogue_error::DialogueError, schemas::user::UserOut,
};

// User first choice, either registering a new account logging with an existing
// one.

pub async fn check_user_auth_selection(ctl: Controller) -> BotResult<()> {
    match &ctl.message().text().map(|m| m.to_lowercase()) {
        Some(m) if m == "a" => {
            let message = "Okay! Elegiste crearte una cuenta nueva. Para eso voy a necesitar que me envies un mail";

            ctl.send_message(message).await?;
            ctl.update_dialogue_state(State::RegisterEmail).await?
        }
        Some(m) if m == "b" => {
            let message = "Okay! Parece que ya tenes una cuenta registrada. Para eso voy a necesitar que me envies tu mail";

            ctl.send_message(message).await?;
            ctl.update_dialogue_state(State::LoginEmail).await?
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

pub async fn handle_register_email(ctl: Controller) -> BotResult<()> {
    match ctl.message().text() {
        Some(email)
            if EMAIL_REGEX
                .is_match(email)
                .map_err(|e| Box::new(DialogueError::from(e)))? =>
        {
            ctl.send_message("Ahora necesito una contrasena").await?;

            match ctl.get_dialogue_state().await? {
                Some(s) => match s {
                    State::RegisterEmail => {
                        ctl.update_dialogue_state(State::RegisterPassword {
                            email: email.to_string(),
                        })
                        .await?
                    }
                    State::LoginEmail => {
                        ctl.update_dialogue_state(State::LoginPassword {
                            email: email.to_string(),
                        })
                        .await?
                    }
                    _ => {
                        error!(
                            "Impossible state! {:?}. Should be RegisterEmail or LoginEmail",
                            s
                        );
                        ctl.reset_dialogue().await?;
                    }
                },
                None => {
                    error!("Got no state! There should be one when registering or logging");
                    ctl.reset_dialogue().await?;
                }
            }
        }
        _ => {
            ctl.send_message("Ese email no es valido!").await?;
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
            match ctl.get_dialogue_state().await? {
                Some(s) => match s {
                    State::RegisterPassword { .. } => {
                        ctl.send_message("Ahora necesito que confirmes las contrasena")
                            .await?;

                        ctl.update_dialogue_state(State::ConfirmPassword {
                            email,
                            password: password.to_string(),
                        })
                        .await?;
                    }
                    State::LoginPassword { .. } => {
                        let token = ctl
                            .request_client()
                            .send_user_login_request(UserOut {
                                email: email.clone(),
                                password: password.to_string(),
                                user_type: None,
                            })
                            .await?;

                        ctl.auth()
                            .new_session(ctl.chat_id(), password.to_string(), token)
                            .await?;

                        ctl.send_message("Ya te loggeaste!").await?;
                    }
                    _ => {
                        let error_msg = "❌ Oops, algo salió mal. No pudimos completar tu autenticación.\n\
\n\
No te preocupes, simplemente elige qué deseas hacer:\n\
\n\
A) Crear una cuenta nueva 🆕\n\
B) Iniciar sesión con tu cuenta existente 🔐";
                        error!(
                            "Impossible state! {:?}. Should be RegisterEmail or LoginEmail",
                            s
                        );
                        ctl.send_error_message(error_msg).await?;
                        ctl.update_dialogue_state(State::CheckUser).await?;
                    }
                },
                None => {
                    error!("Got no state! There should be one when registering or logging");
                    ctl.reset_dialogue().await?;
                }
            }
        }
        _ => {
            ctl.send_message("Esa contrasena es invalida!").await?;
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

            ctl.auth()
                .new_session(ctl.chat_id(), password, token)
                .await?;

            ctl.send_message("Ya creaste tu cuenta!").await?;

            // Change to State::Authenticated so that the user can perform commands.
            ctl.update_dialogue_state(State::Authenticated).await?;
        }
        _ => {
            ctl.send_message("Las contrasenas no coinciden!").await?;
        }
    }

    Ok(())
}
