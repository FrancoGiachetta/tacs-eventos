use lazy_static::lazy_static;
use regex::Regex;
use teloxide::prelude::Requester;

use crate::{
    bot::BotResult,
    controller::MessageController,
    dialogue::{MyDialogue, State},
    schemas::user::UserOut,
};

pub async fn handle_register_email(msg_ctl: MessageController, dialogue: MyDialogue) -> BotResult {
    lazy_static! {
        static ref EMAIL_REGEX: Regex = Regex::new(r"/^[^\s@]+@[^\s@]+\.[^\s@]+$/").unwrap();
    }

    match msg_ctl.msg.text() {
        Some(email) if EMAIL_REGEX.is_match(email) => {
            msg_ctl
                .bot
                .send_message(msg_ctl.chat_id, "Ahora necesito una contrasena")
                .await?;

            dialogue
                .update(State::RegisterPassword {
                    email: email.to_string(),
                })
                .await?;
        }
        _ => {
            msg_ctl
                .bot
                .send_message(msg_ctl.chat_id, "Ese email no es valido!")
                .await?;
        }
    }

    Ok(())
}

pub async fn handle_register_password(
    msg_ctl: MessageController,
    dialogue: MyDialogue,
    email: String,
) -> BotResult {
    lazy_static! {
        static ref PASSWORD_REGEX: Regex =
            Regex::new(r"/^(?=.*[A-Za-z])(?=.*\d).{8,72}$/").unwrap();
    }

    match msg_ctl.msg.text() {
        Some(password) if PASSWORD_REGEX.is_match(password) => {
            msg_ctl
                .bot
                .send_message(
                    msg_ctl.chat_id,
                    "Ahora necesito que confirmes las contrasena",
                )
                .await?;

            dialogue
                .update(State::ConfirmPassword {
                    email,
                    password: password.to_string(),
                })
                .await?;
        }
        _ => {
            msg_ctl
                .bot
                .send_message(msg_ctl.chat_id, "Esa contrasena es invalida!")
                .await?;
        }
    }

    Ok(())
}

pub async fn handle_confirm_password(
    msg_ctl: MessageController,
    dialogue: MyDialogue,
    (email, password): (String, String),
) -> BotResult {
    match msg_ctl.msg.text() {
        Some(confirm_pass) if password == confirm_pass => {
            msg_ctl
                .bot
                .send_message(
                    msg_ctl.chat_id,
                    "Ahora necesito que confirmes las contrasena",
                )
                .await?;

            let token = msg_ctl
                .req_client
                .send_user_registration_request(UserOut {
                    email,
                    password,
                    r#type: "USUARIO".to_string(),
                })
                .await?;

            // TODO: Store token somewhere to be used.
        }
        _ => {
            msg_ctl
                .bot
                .send_message(msg_ctl.chat_id, "Esa contrasena es invalida!")
                .await?;
        }
    }

    dialogue.exit().await?;

    Ok(())
}
