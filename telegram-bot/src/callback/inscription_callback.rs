use teloxide::{dispatching::UpdateHandler, dptree};

use crate::{
    auth::Authenticator,
    bot::BotResult,
    controller::{general_controller::GeneralController, query_controller::QueryController},
    error::{BotError, request_client_error::RequestClientError},
    schemas::{inscription::InscriptionState, user::Session},
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

    send_request_state_message(general_ctl, &token.token, event_id, &user_id).await?;

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

pub async fn send_request_state_message(
    controller: &GeneralController,
    token: &str,
    event_id: &str,
    user_id: &str,
) -> BotResult<()> {
    let inscription = controller
        .request_client()
        .send_get_inscription_request(token, event_id, user_id)
        .await?;

    let message = match inscription.state {
        InscriptionState::Confirmed => &format!(
            "La inscripcion fue generada exitosomante! Aqui puedes verla:\n\n {inscription}"
        ),
        InscriptionState::Rejected => {
            "La inscripcion fue rechazada. Puede ser porque el cupo de inscriptos al evento llego al limite"
        }
        InscriptionState::Pending => {
            "Tu inscripcion esta pendiente de ser aprobada o rechazada. Estate antento a su actualizacion!"
        }
    };

    controller.send_message(message).await?;

    Ok(())
}
