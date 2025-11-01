use crate::bot::BotResult;
use crate::controller::Controller;
use crate::dialogue::State;
use crate::dialogue::UseCase;
use crate::error::BotError;
use teloxide::dispatching::{UpdateFilterExt, UpdateHandler};
use teloxide::dptree::case;
use teloxide::types::Update;

#[derive(Clone, Debug)]
pub enum EventCreationSteps {
    EnterTitle,
    EnterDescription,
    EnterDate,
    EnterDuration,
    EnterLocation,
    EnterMaxParticipants,
    EnterPrice,
    EnterCategory,
}

pub fn schema() -> UpdateHandler<BotError> {
    Update::filter_message().branch(
        case![State::Authenticated(usecase)].branch(
            case![UseCase::EventCreation(step)]
                .branch(case![EventCreationSteps::EnterTitle].endpoint(handle_enter_title))
                .branch(
                    case![EventCreationSteps::EnterDescription].endpoint(handle_enter_description),
                )
                .branch(case![EventCreationSteps::EnterDate].endpoint(handle_enter_date))
                .branch(case![EventCreationSteps::EnterDuration].endpoint(handle_enter_duration))
                .branch(case![EventCreationSteps::EnterLocation].endpoint(handle_enter_location))
                .branch(
                    case![EventCreationSteps::EnterMaxParticipants]
                        .endpoint(handle_enter_max_participants),
                )
                .branch(case![EventCreationSteps::EnterPrice].endpoint(handle_enter_price))
                .branch(case![EventCreationSteps::EnterCategory].endpoint(handle_enter_category)),
        ),
    )
}

pub async fn handle_enter_title(controller: Controller) -> BotResult<()> {
    panic!("Not implemented yet")
}

pub async fn handle_enter_description(controller: Controller) -> BotResult<()> {
    panic!("Not implemented yet")
}

pub async fn handle_enter_date(controller: Controller) -> BotResult<()> {
    panic!("Not implemented yet")
}

pub async fn handle_enter_duration(controller: Controller) -> BotResult<()> {
    panic!("Not implemented yet")
}

pub async fn handle_enter_location(controller: Controller) -> BotResult<()> {
    panic!("Not implemented yet")
}

pub async fn handle_enter_max_participants(controller: Controller) -> BotResult<()> {
    panic!("Not implemented yet")
}

pub async fn handle_enter_price(controller: Controller) -> BotResult<()> {
    panic!("Not implemented yet")
}

pub async fn handle_enter_category(controller: Controller) -> BotResult<()> {
    panic!("Not implemented yet")
}
