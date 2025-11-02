use reqwest::StatusCode;
use tracing::{error, info};

use crate::{
    bot::BotResult, controller::Controller, error::request_client_error::RequestClientError,
};

pub async fn handle_my_inscriptions(ctl: Controller) -> BotResult<()> {
    info!("Listing inscriptions");

    let token = ctl.auth().get_session_token(&ctl.chat_id())?;

    match ctl
        .request_client()
        .send_get_my_inscriptions_request(&token)
        .await
    {
        Ok(inscriptions_list) if inscriptions_list.is_empty() => {
            ctl.send_message(
                &"<b>✍️ Sin inscripciones activas</b>\n\n<i>Todavía no estás inscrito en ningún evento. ¡Busca eventos y regístrate!</i>\n\n"
            ).await?;
        }
        Ok(inscriptions_list) => {
            ctl.send_message(&"<b>📅 Estos son los eventos disponibles</b>\n\n<i>Según los criterios de búsqueda que ingresaste:</i>\n\n").await?;

            for inscription in inscriptions_list {
                ctl.send_message(&format!("📅 <b>Evento</b>\n\n{}", inscription))
                    .await?;
            }
        }
        Err(err) => {
            error!("Got an error while performing the request: {}", err);

            let error_msg = match err {
                // This command requires the user to be logged in.
                RequestClientError::Reqwest(req_err)
                    if req_err
                        .status()
                        .is_some_and(|e| matches!(e, StatusCode::FORBIDDEN)) =>
                {
                    "<b>Necesitás estar logueado</b>\n\n\
Para usar este comando, primero iniciá sesión"
                }
                _ => {
                    "<b>Error al ejecutar el comando</b>\n\n\
Ocurrió un problema inesperado.\n\
Intentá nuevamente en unos momentos ⏱️"
                }
            };

            ctl.send_error_message(error_msg).await?;
        }
    }

    Ok(())
}
