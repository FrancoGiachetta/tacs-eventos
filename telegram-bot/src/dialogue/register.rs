use lazy_static::lazy_static;
use regex::Regex;

use crate::{
    bot::BotResult,
    controller::Controller,
    dialogue::{MyDialogue, State},
    schemas::user::UserOut,
};

pub async fn handle_register_email(ctl: Controller, dialogue: MyDialogue) -> BotResult<()> {
    lazy_static! {
        static ref EMAIL_REGEX: Regex = Regex::new(r"/^[^\s@]+@[^\s@]+\.[^\s@]+$/").unwrap();
    }

    match ctl.message().text() {
        Some(email) if EMAIL_REGEX.is_match(email) => {
            ctl.send_message("Ahora necesito una contrasena").await?;

            dialogue
                .update(State::RegisterPassword {
                    email: email.to_string(),
                })
                .await?;
        }
        _ => {
            ctl.send_message("Ese email no es valido!").await?;
        }
    }

    Ok(())
}

pub async fn handle_register_password(
    ctl: Controller,
    dialogue: MyDialogue,
    email: String,
) -> BotResult<()> {
    lazy_static! {
        static ref PASSWORD_REGEX: Regex =
            Regex::new(r"/^(?=.*[A-Za-z])(?=.*\d).{8,72}$/").unwrap();
    }

    match ctl.message().text() {
        Some(password) if PASSWORD_REGEX.is_match(password) => {
            ctl.send_message("Ahora necesito que confirmes las contrasena")
                .await?;

            dialogue
                .update(State::ConfirmPassword {
                    email,
                    password: password.to_string(),
                })
                .await?;
        }
        _ => {
            ctl.send_message("Esa contrasena es invalida!").await?;
        }
    }

    Ok(())
}

pub async fn handle_confirm_password(
    ctl: Controller,
    dialogue: MyDialogue,
    (email, password): (String, String),
) -> BotResult<()> {
    match ctl.message().text() {
        Some(confirm_pass) if password == confirm_pass => {
            ctl.send_message("Ahora necesito que confirmes las contrasena")
                .await?;

            let token = ctl
                .request_client()
                .send_user_registration_request(UserOut {
                    email,
                    password,
                    r#type: "USUARIO".to_string(),
                })
                .await?;

            ctl.send_message(&format!("Your token! {}", token.token))
                .await?;
        }
        _ => {
            ctl.send_message("Esa contrasena es invalida!").await?;
        }
    }

    dialogue.exit().await?;

    Ok(())
}
