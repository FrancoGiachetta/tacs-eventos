use teloxide::dispatching::UpdateHandler;
use teloxide::dptree;

use crate::bot::BotResult;
use crate::controller::query_controller::QueryController;
use crate::error::BotError;

pub const CLOSE_EVENT_PREFIX: &str = "event::close::";
pub const OPEN_EVENT_PREFIX: &str = "event::open::";

pub async fn close_event(controller: QueryController, query: String) -> BotResult<()> {
    let event_id = query
        .strip_prefix(CLOSE_EVENT_PREFIX)
        .ok_or(BotError::CustomError(
            "envet id should be there".to_string(),
        ))?;
    let general_ctl = controller.general_ctl();

    controller
        .reply_to_callback(&format!("Cerrando inscripciones a evento {}", event_id))
        .await?;

    general_ctl
        .request_client()
        .send_close_event_request(
            event_id,
            &general_ctl
                .auth()
                .get_session_token(&general_ctl.chat_id())?,
        )
        .await?;

    controller
        .reply_to_callback(&format!(
            "Se cerraron las inscripciones al evento {}",
            event_id
        ))
        .await?;

    Ok(())
}

pub async fn open_event(controller: QueryController, query: String) -> BotResult<()> {
    let event_id = query
        .strip_prefix(OPEN_EVENT_PREFIX)
        .ok_or(BotError::CustomError(
            "envet id should be there".to_string(),
        ))?;
    let general_ctl = controller.general_ctl();

    controller
        .reply_to_callback(&format!("Abriendo inscripciones a evento {}", event_id))
        .await?;

    general_ctl
        .request_client()
        .send_open_event_request(
            event_id,
            &general_ctl
                .auth()
                .get_session_token(&general_ctl.chat_id())?,
        )
        .await?;

    controller
        .reply_to_callback(&format!(
            "Se abrieron las inscripciones al evento {}",
            event_id
        ))
        .await?;

    Ok(())
}

pub fn schema() -> UpdateHandler<BotError> {
    dptree::entry()
        .branch(
            dptree::filter(|data: String| data.starts_with(OPEN_EVENT_PREFIX)).endpoint(open_event),
        )
        .branch(
            dptree::filter(|data: String| data.starts_with(CLOSE_EVENT_PREFIX))
                .endpoint(close_event),
        )
}
