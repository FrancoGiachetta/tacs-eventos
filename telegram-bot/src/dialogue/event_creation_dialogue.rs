use crate::bot::BotResult;
use crate::controller::Controller;
use crate::dialogue::State;
use crate::dialogue::UseCase;
use crate::error::BotError;
use crate::schemas::event::EventBuilder;
use teloxide::dispatching::{UpdateFilterExt, UpdateHandler};
use teloxide::dptree::case;
use teloxide::types::Update;

#[derive(Clone, Debug)]
pub enum EventCreationSteps {
    EnterTitle,
    EnterDescription { event_builder: EventBuilder },
    EnterDate { event_builder: EventBuilder },
    EnterDuration { event_builder: EventBuilder },
    EnterLocation { event_builder: EventBuilder },
    EnterMaxCapacity { event_builder: EventBuilder },
    EnterPrice { event_builder: EventBuilder },
    EnterCategory { event_builder: EventBuilder },
}

pub fn schema() -> UpdateHandler<BotError> {
    Update::filter_message().branch(
        case![State::Authenticated(usecase)].branch(
            case![UseCase::EventCreation(step)]
                .branch(case![EventCreationSteps::EnterTitle].endpoint(handle_enter_title))
                .branch(
                    case![EventCreationSteps::EnterDescription { event_builder }]
                        .endpoint(handle_enter_description),
                )
                .branch(
                    case![EventCreationSteps::EnterDate { event_builder }]
                        .endpoint(handle_enter_date),
                )
                .branch(
                    case![EventCreationSteps::EnterDuration { event_builder }]
                        .endpoint(handle_enter_duration),
                )
                .branch(
                    case![EventCreationSteps::EnterLocation { event_builder }]
                        .endpoint(handle_enter_location),
                )
                .branch(
                    case![EventCreationSteps::EnterMaxCapacity { event_builder }]
                        .endpoint(handle_enter_max_capacity),
                )
                .branch(
                    case![EventCreationSteps::EnterPrice { event_builder }]
                        .endpoint(handle_enter_price),
                )
                .branch(
                    case![EventCreationSteps::EnterCategory { event_builder }]
                        .endpoint(handle_enter_category),
                ),
        ),
    )
}

pub async fn handle_enter_title(controller: Controller) -> BotResult<()> {
    panic!("Not implemented yet")
}

pub async fn handle_enter_description(
    controller: Controller,
    event_builder: EventBuilder,
) -> BotResult<()> {
    panic!("Not implemented yet")
}

pub async fn handle_enter_date(
    controller: Controller,
    event_builder: EventBuilder,
) -> BotResult<()> {
    panic!("Not implemented yet")
}

pub async fn handle_enter_duration(
    controller: Controller,
    event_builder: EventBuilder,
) -> BotResult<()> {
    panic!("Not implemented yet")
}

pub async fn handle_enter_location(
    controller: Controller,
    event_builder: EventBuilder,
) -> BotResult<()> {
    panic!("Not implemented yet")
}

pub async fn handle_enter_max_capacity(
    controller: Controller,
    event_builder: EventBuilder,
) -> BotResult<()> {
    panic!("Not implemented yet")
}

pub async fn handle_enter_price(
    controller: Controller,
    event_builder: EventBuilder,
) -> BotResult<()> {
    panic!("Not implemented yet")
}

pub async fn handle_enter_category(
    controller: Controller,
    event_builder: EventBuilder,
) -> BotResult<()> {
    panic!("Not implemented yet")
}
