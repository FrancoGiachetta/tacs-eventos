use teloxide::{dispatching::UpdateHandler, dptree};

use crate::{
    auth::Authenticator,
    bot::BotResult,
    controller::{general_controller::GeneralController, query_controller::QueryController},
    error::BotError,
    schemas::{
        inscription::{Inscription, InscriptionState},
        user::Session,
    },
};

pub const INSCRIPTION_CANCEL_PREFIX: &str = "inscription::cancel::";
pub const INSCRIPTION_ENROL_PREFIX: &str = "inscription::enrol::";
pub const SEE_INSCRIPTIONS_PREFIX: &str = "inscription::manage::";

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

pub async fn handle_see_inscriptions(controller: QueryController, query: String) -> BotResult<()> {
    let event_id = query
        .strip_prefix(SEE_INSCRIPTIONS_PREFIX)
        .ok_or(BotError::CustomError(
            "event id should be there".to_string(),
        ))?;
    let general_ctl = controller.general_ctl();

    controller
        .reply_to_callback(&format!("Listando inscripciones del evento {}", event_id))
        .await?;

    let token = general_ctl
        .auth()
        .get_session_token(&general_ctl.chat_id())?;

    let confirmed_inscriptions = general_ctl
        .request_client()
        .send_get_event_inscriptions_request(&token, event_id)
        .await?;

    let waitlist_inscriptions = general_ctl
        .request_client()
        .send_get_event_waitlist_request(&token, event_id)
        .await?;

    let waitlist_as_inscriptions: Vec<Inscription> = waitlist_inscriptions
        .into_iter()
        .map(|w| Inscription {
            id: w.id,
            state: InscriptionState::Pending,
            email: w.user.email.clone(),
            date: w.entry_date,
            event_id: event_id.to_string(),
        })
        .collect();

    let mut all_inscriptions = confirmed_inscriptions;
    all_inscriptions.extend(waitlist_as_inscriptions);

    if all_inscriptions.is_empty() {
        general_ctl
            .send_message(
                "<b>📋 Sin inscripciones</b>\n\n<i>No hay inscripciones para este evento.</i>",
            )
            .await?;
    } else {
        for inscription in all_inscriptions {
            send_inscription(&general_ctl, &inscription).await?;
        }
    }

    Ok(())
}

async fn send_inscription(ctl: &GeneralController, inscription: &Inscription) -> BotResult<()> {
    ctl.send_message(&format!("{inscription}"))
        .await
        .map_err(BotError::from)?;
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
        .branch(
            dptree::filter(|data: String| data.starts_with(SEE_INSCRIPTIONS_PREFIX))
                .endpoint(handle_see_inscriptions),
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
