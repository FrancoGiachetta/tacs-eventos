use crate::bot::BotResult;
use crate::controller::Controller;
use reqwest::StatusCode;
use thiserror::Error;
use tracing::error;

#[derive(Debug, Error)]
pub enum RequestClientError {
    #[error(transparent)]
    Reqwest(#[from] reqwest::Error),
    #[error("request has failed with due to timeout")]
    TimeOut,
    #[error(transparent)]
    JsonParse(#[from] serde_json::Error),
}

/// Handles an api error
pub async fn handle_http_request_error(ctl: Controller, err: RequestClientError) -> BotResult<()> {
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
