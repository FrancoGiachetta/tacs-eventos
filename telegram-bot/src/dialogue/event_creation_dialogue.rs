use crate::bot::BotResult;
use crate::controller::Controller;
use crate::dialogue::State;
use crate::dialogue::UseCase;
use crate::error::BotError;
use crate::schemas::event::EventBuilder;
use chrono::NaiveDateTime;
use std::str::FromStr;
use std::string::String;
use teloxide::dispatching::{UpdateFilterExt, UpdateHandler};
use teloxide::dptree::case;
use teloxide::types::Update;

#[derive(Clone, Debug)]
pub enum EventCreationStep {
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
                .branch(case![EventCreationStep::EnterTitle].endpoint(handle_enter_title))
                .branch(
                    case![EventCreationStep::EnterDescription { event_builder }]
                        .endpoint(handle_enter_description),
                )
                .branch(
                    case![EventCreationStep::EnterDate { event_builder }]
                        .endpoint(handle_enter_date),
                )
                .branch(
                    case![EventCreationStep::EnterDuration { event_builder }]
                        .endpoint(handle_enter_duration),
                )
                .branch(
                    case![EventCreationStep::EnterLocation { event_builder }]
                        .endpoint(handle_enter_location),
                )
                .branch(
                    case![EventCreationStep::EnterMaxCapacity { event_builder }]
                        .endpoint(handle_enter_max_capacity),
                )
                .branch(
                    case![EventCreationStep::EnterPrice { event_builder }]
                        .endpoint(handle_enter_price),
                )
                .branch(
                    case![EventCreationStep::EnterCategory { event_builder }]
                        .endpoint(handle_enter_category),
                ),
        ),
    )
}

async fn handle_enter_title(controller: Controller) -> BotResult<()> {
    match get_string_input_data(&controller, 0, 100) {
        Ok(title) => {
            controller
                .send_message("Ahora ingresá la descripción del evento.")
                .await?;
            let mut event_builder = EventBuilder::default();
            event_builder.title(title.to_string());
            set_step(
                &controller,
                EventCreationStep::EnterDescription { event_builder },
            )
            .await?;
            Ok(())
        }
        Err(BotError::CustomError(msg)) => {
            controller.send_error_message(&msg).await?;
            Ok(())
        }
        Err(e) => Err(e),
    }
}

async fn handle_enter_description(
    controller: Controller,
    event_builder: EventBuilder,
) -> BotResult<()> {
    match get_string_input_data(&controller, 10, 1000) {
        Ok(description) => {
            let mut updated_event_data = event_builder.clone();
            updated_event_data.description(description.to_string());
            set_step(
                &controller,
                EventCreationStep::EnterDate {
                    event_builder: updated_event_data,
                },
            )
            .await?;
            controller
                .send_message("Ahora ingresá la fecha de inicio del evento.")
                .await?;
            Ok(())
        }
        Err(BotError::CustomError(msg)) => {
            controller.send_error_message(&msg).await?;
            Ok(())
        }
        Err(e) => Err(e),
    }
}

/// Defines the date format
#[derive(Debug, Clone, PartialEq)]
pub struct Date(pub NaiveDateTime);

impl FromStr for Date {
    type Err = chrono::ParseError;

    fn from_str(s: &str) -> Result<Self, Self::Err> {
        // Format: "DAY/MONTH/YEAR HOUR:MINUTE"
        NaiveDateTime::parse_from_str(s, "%d/%m/%Y %H:%M").map(Date)
    }
}

/// Defines valid event categories
#[derive(Debug, Clone, PartialEq)]
pub struct Category(pub String);

impl Category {
    const VALID_CATEGORIES: [&'static str; 10] = [
        "Deporte",
        "Moda",
        "Educacion",
        "Tecnologia",
        "Musica",
        "Gastronomia",
        "Arte",
        "Negocios",
        "Salud",
        "Entretenimiento",
    ];
}

impl FromStr for Category {
    type Err = ();

    fn from_str(s: &str) -> Result<Self, Self::Err> {
        Self::VALID_CATEGORIES
            .iter()
            .find(|&category| category.eq_ignore_ascii_case(s))
            .map(|&category| Category(category.to_string()))
            .ok_or(())
    }
}

async fn handle_enter_date(controller: Controller, event_builder: EventBuilder) -> BotResult<()> {
    match get_input_data::<Date>(
        &controller,
        Some("Ingrese una fecha en el siguiente formato: DIA/MES/AÑO HORAS:MINUTOS".to_string()),
    ) {
        Ok(Date(date)) => {
            let current_date = chrono::Local::now().naive_local();
            if date.lt(&current_date) {
                controller
                    .send_error_message(
                        "La fecha ingresada ya pasó. Por favor ingrese una fecha futura.",
                    )
                    .await?;
            } else {
                let mut updated_event_data = event_builder.clone();
                updated_event_data.start_date_time(date);
                set_step(
                    &controller,
                    EventCreationStep::EnterDuration {
                        event_builder: updated_event_data,
                    },
                )
                .await?;
                controller
                    .send_message("Ahora ingresá la duración del evento en minutos.")
                    .await?;
            }
            Ok(())
        }
        Err(BotError::CustomError(msg)) => {
            controller.send_error_message(&msg).await?;
            Ok(())
        }
        Err(e) => Err(e),
    }
}

async fn handle_enter_duration(
    controller: Controller,
    event_builder: EventBuilder,
) -> BotResult<()> {
    match get_integer_input_data(&controller, Some(1), None) {
        Ok(duration_minutes) => {
            let mut updated_event_data = event_builder.clone();
            updated_event_data.duration_minutes(duration_minutes);
            set_step(
                &controller,
                EventCreationStep::EnterLocation {
                    event_builder: updated_event_data,
                },
            )
            .await?;
            controller
                .send_message("Ahora ingresá la ubicación del evento.")
                .await?;
            Ok(())
        }
        Err(BotError::CustomError(msg)) => {
            controller.send_error_message(&msg).await?;
            Ok(())
        }
        Err(e) => Err(e),
    }
}

async fn handle_enter_location(
    controller: Controller,
    event_builder: EventBuilder,
) -> BotResult<()> {
    match get_string_input_data(&controller, 3, 300) {
        Ok(location) => {
            let mut updated_event_data = event_builder.clone();
            updated_event_data.location(location.to_string());
            set_step(
                &controller,
                EventCreationStep::EnterMaxCapacity {
                    event_builder: updated_event_data,
                },
            )
            .await?;
            controller
                .send_message("Ahora ingresá la capacidad máxima del evento.")
                .await?;
            Ok(())
        }
        Err(BotError::CustomError(msg)) => {
            controller.send_error_message(&msg).await?;
            Ok(())
        }
        Err(e) => Err(e),
    }
}

async fn handle_enter_max_capacity(
    controller: Controller,
    event_builder: EventBuilder,
) -> BotResult<()> {
    match get_integer_input_data(&controller, Some(0), None) {
        Ok(max_capacity) => {
            let mut updated_event_data = event_builder.clone();
            updated_event_data.max_capacity(max_capacity);
            set_step(
                &controller,
                EventCreationStep::EnterPrice {
                    event_builder: updated_event_data,
                },
            )
            .await?;
            controller
                .send_message("Ahora ingresá el precio del evento.")
                .await?;
            Ok(())
        }
        Err(BotError::CustomError(msg)) => {
            controller.send_error_message(&msg).await?;
            Ok(())
        }
        Err(e) => Err(e),
    }
}

async fn handle_enter_price(controller: Controller, event_builder: EventBuilder) -> BotResult<()> {
    match get_double_input_data(&controller, Some(0.00), None, Some(2)) {
        Ok(price) => {
            let mut updated_event_data = event_builder.clone();
            updated_event_data.price(price);
            set_step(
                &controller,
                EventCreationStep::EnterCategory {
                    event_builder: updated_event_data,
                },
            )
            .await?;
            controller
                .send_message(&format!(
                    "Ahora ingresá la categoría del evento. Las categorías válidas son: {}",
                    Category::VALID_CATEGORIES.join(", ")
                ))
                .await?;
            Ok(())
        }
        Err(BotError::CustomError(msg)) => {
            controller.send_error_message(&msg).await?;
            Ok(())
        }
        Err(e) => Err(e),
    }
}

async fn handle_enter_category(
    controller: Controller,
    event_builder: EventBuilder,
) -> BotResult<()> {
    match get_input_data::<Category>(
        &controller,
        Some(format!(
            "Categoría inválida. Las categorías válidas son: {}",
            Category::VALID_CATEGORIES.join(", ")
        )),
    ) {
        Ok(Category(category)) => {
            let mut updated_event_data = event_builder.clone();
            updated_event_data.category(category);

            create_and_send_event(&controller, updated_event_data).await?;
            Ok(())
        }
        Err(BotError::CustomError(msg)) => {
            controller.send_error_message(&msg).await?;
            Ok(())
        }
        Err(e) => Err(e),
    }
}

async fn create_and_send_event(
    controller: &Controller,
    mut event_builder: EventBuilder,
) -> BotResult<()> {
    let chat_id = controller.chat_id();
    let token = controller.auth().get_session_token(&chat_id)?;
    // This field is set in the backend based on the token
    event_builder.organizer("placeholder".to_string());
    let event = event_builder
        .build()
        .map_err(|e| BotError::CustomError(e.to_string()))?;
    let request_client = controller.request_client();
    request_client
        .send_create_event_request(event, &token)
        .await?;
    Ok(())
}

async fn set_step(
    controller: &Controller,
    event_creation_step: EventCreationStep,
) -> BotResult<()> {
    controller
        .update_dialogue_state(State::Authenticated(UseCase::EventCreation(
            event_creation_step,
        )))
        .await?;
    Ok(())
}

fn get_input_data<T>(controller: &Controller, parse_error_message: Option<String>) -> BotResult<T>
where
    T: FromStr,
{
    let text = raw_input(controller)?;

    text.parse::<T>().map_err(|_| {
        BotError::CustomError(
            parse_error_message.unwrap_or("Por favor ingrese un valor válido.".to_string()),
        )
    })
}

fn raw_input(controller: &Controller) -> Result<String, BotError> {
    controller
        .message()
        .text()
        .map(|text| text.to_string())
        .ok_or(BotError::CustomError(
            "Por favor ingrese un valor.".to_string(),
        ))
}

fn get_integer_input_data(
    controller: &Controller,
    min_value: Option<u32>,
    max_value: Option<u32>,
) -> BotResult<u32> {
    get_input_data::<u32>(
        controller,
        Some("Por favor ingrese un número entero válido.".to_string()),
    )
    .and_then(|value| validate_range(value, min_value, max_value))
}

fn get_string_input_data(
    controller: &Controller,
    min_length: u32,
    max_length: u32,
) -> BotResult<String> {
    get_input_data::<String>(controller, None).and_then(|text| {
        if text.len() > max_length as usize {
            Err(BotError::CustomError(format!(
                "Por favor ingrese un valor con menos de {} caracteres.",
                max_length
            )))
        } else if text.len() < min_length as usize {
            Err(BotError::CustomError(format!(
                "Por favor ingrese un valor con más de {} caracteres.",
                min_length
            )))
        } else {
            Ok(text)
        }
    })
}

fn get_double_input_data(
    controller: &Controller,
    min_value: Option<f32>,
    max_value: Option<f32>,
    max_digits: Option<u32>,
) -> BotResult<f32> {
    let raw_input = raw_input(controller)?;
    let decimals = raw_input.split('.').nth(1).unwrap_or("").chars().count();
    if let Some(max_digits) = max_digits {
        if decimals > max_digits as usize {
            return Err(BotError::CustomError(format!(
                "Por favor ingrese un número que tenga como máximo {} dígitos decimales.",
                max_digits
            )));
        }
    }
    raw_input.parse::<f32>()
        .map_err(|_| {
            BotError::CustomError(
                "Por favor ingrese un número. Si quiere usar decimales, debe usar el punto como dígito separador.".to_string(),
            )
        })
        .and_then(|value| validate_range(value, min_value, max_value))
}

fn validate_range<T>(value: T, min_value: Option<T>, max_value: Option<T>) -> BotResult<T>
where
    T: PartialOrd + std::fmt::Display,
{
    if let Some(min) = min_value {
        if value < min {
            return Err(BotError::CustomError(format!(
                "Por favor ingrese un valor mayor o igual a {}.",
                min
            )));
        }
    }
    if let Some(max) = max_value {
        if value > max {
            return Err(BotError::CustomError(format!(
                "Por favor ingrese un valor menor o igual a {}.",
                max
            )));
        }
    }
    Ok(value)
}
