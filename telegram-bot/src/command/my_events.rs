use tracing::{error, info};

use crate::bot::BotResult;
use crate::callback::Callback;
use crate::controller::Controller;
use crate::error_handling::handle_error;
use crate::schemas::event_organizer_view::EventOrganizerView;
use teloxide::types::{InlineKeyboardButton, InlineKeyboardMarkup};

/// Lists the events organized by the user
pub async fn handle_my_events(controller: Controller) -> BotResult<()> {
    info!("Listing my_events!");

    match controller
        .request_client()
        .send_get_my_events_list_request(
            &controller.auth().get_session_token(&controller.chat_id())?,
        )
        .await
    {
        Ok(events_list) => {
            // Sends a message swowing each event
            for event in events_list {
                send_event_message(&controller, &event).await?;
            }
        }
        Err(err) => {
            handle_error(controller, err).await?;
        }
    }

    Ok(())
}

async fn send_event_message(controller: &Controller, event: &EventOrganizerView) -> BotResult<()> {
    let text = format!("{event}");
    let change_state_button = change_state_button(event);
    let keyboard = InlineKeyboardMarkup {
        inline_keyboard: vec![vec![change_state_button]],
    };
    controller.send_message_with_markup(&text, keyboard).await
}

/// Creates a button for opening or closing the event
fn change_state_button(event: &EventOrganizerView) -> InlineKeyboardButton {
    if event.open() {
        // if the event is open
        // Creates a button for closing the inscriptions for that event
        let callback_data = Callback::CloseEvent(event.id().to_string()).query();
        InlineKeyboardButton::callback("Cerrar inscripciones", callback_data)
    } else {
        // if the event is closed
        // Creates a button for opening the inscriptions for that event
        let callback_data = Callback::OpenEvent(event.id().to_string()).query();
        InlineKeyboardButton::callback("Abrir inscripciones", callback_data)
    }
}
