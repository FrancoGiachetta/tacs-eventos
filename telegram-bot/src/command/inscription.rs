use teloxide::types::{InlineKeyboardButton, InlineKeyboardMarkup};
use tracing::info;

use crate::{
    bot::BotResult,
    callback::inscription_callback::INSCRIPTION_CANCEL_PREFIX,
    controller::general_controller::GeneralController,
    error::{BotError, request_client_error::handle_http_request_error},
    schemas::inscription::{Inscription, InscriptionState},
};

pub async fn handle_my_inscriptions(ctl: GeneralController) -> BotResult<()> {
    info!("Listing inscriptions");

    let token = ctl.auth().get_session_token(&ctl.chat_id())?;

    match ctl
        .request_client()
        .send_get_my_inscriptions_request(&token)
        .await
    {
        Ok(inscriptions_list) if inscriptions_list.is_empty() => {
            ctl.send_message(
                "<b>✍️ Sin inscripciones activas</b>\n\n<i>Todavía no estás inscrito en ningún evento. ¡Busca eventos y regístrate!</i>\n\n"
            ).await?;
        }
        Ok(inscriptions_list) => {
            for inscription in inscriptions_list {
                send_inscription_message(&ctl, inscription).await?;
            }
        }
        Err(err) => handle_http_request_error(&ctl, err).await?,
    }

    Ok(())
}

async fn send_inscription_message(
    ctl: &GeneralController,
    inscription: Inscription,
) -> BotResult<()> {
    match inscription.state {
        InscriptionState::Confirmed => {
            let callback = InlineKeyboardButton::callback(
                "Cancelar Inscripcion",
                format!("{}{}", INSCRIPTION_CANCEL_PREFIX, inscription.event_id),
            );
            let keyboard = InlineKeyboardMarkup {
                inline_keyboard: vec![vec![callback]],
            };

            ctl.send_message_with_callback(&format!("{inscription}"), keyboard)
                .await
                .map_err(BotError::from)?
        }
        _ => ctl
            .send_message(&format!("{inscription}"))
            .await
            .map_err(BotError::from)?,
    }

    Ok(())
}
