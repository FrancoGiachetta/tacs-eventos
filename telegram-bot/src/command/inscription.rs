use tracing::info;

use crate::{
    bot::BotResult, controller::general_controller::GeneralController,
    error::request_client_error::handle_http_request_error,
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
            ctl.send_message("<b>📅 Estos son los eventos disponibles</b>\n\n<i>Según los criterios de búsqueda que ingresaste:</i>\n\n").await?;

            for inscription in inscriptions_list {
                ctl.send_message(&format!("📅 <b>Evento</b>\n\n{}", inscription))
                    .await?;
            }
        }
        Err(err) => handle_http_request_error(&ctl, err).await?,
    }

    Ok(())
}
