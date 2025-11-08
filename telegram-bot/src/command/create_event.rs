use crate::bot::BotResult;
use crate::controller::general_controller::GeneralController;
use crate::dialogue::{State, UseCase};
use crate::dialogue::event_creation_dialogue::EventCreationStep;

pub async fn handle_create_event(controller: GeneralController) -> BotResult<()> {
    controller.send_message("Vamos a pedirte la información de tu evento. Para comenzar, \
    ingresá el título.").await?;
    controller.update_dialogue_state(State::Authenticated(
        UseCase::EventCreation(EventCreationStep::EnterTitle))).await?;
    Ok(())
}
