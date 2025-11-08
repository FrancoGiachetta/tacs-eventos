use teloxide::{dispatching::UpdateHandler, dptree};

use crate::{
    auth::Authenticator, bot::BotResult, controller::query_controller::QueryController,
    error::BotError, schemas::user::Session,
};

pub const INSCRIPTION_CANCEL_PREFIX: &str = "inscription::cancel::";
pub const INSCRIPTION_ENROL_PREFIX: &str = "inscription::enrol::";

pub async fn handle_cancel_inscription(
    controller: QueryController,
    query: String,
) -> BotResult<()> {
    let event_id = query
        .strip_prefix(INSCRIPTION_CANCEL_PREFIX)
        .ok_or(BotError::CustomError(
            "event id should be there".to_string(),
        ))?;
    let general_ctl = controller.general_ctl();

    controller
        .reply_to_callback(&format!("Cancelando incripcion a evento {}", event_id))
        .await?;

    let Session { user_id, token, .. } = general_ctl.auth().get_session(&general_ctl.chat_id())?;

    general_ctl
        .request_client()
        .send_cancel_inscription_request(&token.token, event_id, &user_id)
        .await?;

    controller
        .reply_to_callback(&format!("Inscripcion cancelada"))
        .await?;

    Ok(())
}

pub async fn handle_inscription_enrolment(
    controller: QueryController,
    query: String,
) -> BotResult<()> {
    let event_id = query
        .strip_prefix(INSCRIPTION_ENROL_PREFIX)
        .ok_or(BotError::CustomError(
            "event id should be there".to_string(),
        ))?;
    let general_ctl = controller.general_ctl();

    controller
        .reply_to_callback(&format!("Generando incripcion a evento {}", event_id))
        .await?;

    let Session { user_id, token, .. } = general_ctl.auth().get_session(&general_ctl.chat_id())?;

    general_ctl
        .request_client()
        .send_enrolment_request(&token.token, event_id, &user_id)
        .await?;

    controller
        .reply_to_callback(&format!("Inscripcion generada"))
        .await?;

    Ok(())
}

pub fn schema() -> UpdateHandler<BotError> {
    dptree::entry()
        .branch(
            dptree::filter(|data: String| data.starts_with(INSCRIPTION_CANCEL_PREFIX))
                .endpoint(handle_cancel_inscription),
        )
        .branch(
            dptree::filter(|data: String| data.starts_with(INSCRIPTION_ENROL_PREFIX))
                .endpoint(handle_inscription_enrolment),
        )
}
