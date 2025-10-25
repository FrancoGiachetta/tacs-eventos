use crate::bot::BotResult;
use crate::controller::Controller;
use crate::error::request_client_error::RequestClientError;
use reqwest::StatusCode;
use tracing::error;

/// Handles an api error
pub async fn handle_error(ctl: Controller, err: RequestClientError) -> BotResult<()> {
    error!("Got an error while performing the request: {}", err);

    let error_msg = match err {
        // This command requires the user to be logged in.
        RequestClientError::Reqwest(req_err)
            if req_err
                .status()
                .is_some_and(|e| matches!(e, StatusCode::FORBIDDEN)) =>
        {
            "🔒 <b>Necesitás estar logueado</b>\n\n\
Para usar este comando, primero iniciá sesión"
        }
        _ => {
            "⚠️ <b>Error al ejecutar el comando</b>\n\n\
Ocurrió un problema inesperado.\n\
Intentá nuevamente en unos momentos ⏱️"
        }
    };

    ctl.send_message(error_msg).await?;

    Ok(())
}
