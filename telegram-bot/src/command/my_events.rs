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
    // TODO: add an 'open' button if the inscriptions are closed
    let callback_data = Callback::CloseEvent(event.id().to_string()).query();
    // Creates a button for closing the inscriptions for that event
    let button = InlineKeyboardButton::callback("Cerrar inscripciones", callback_data);
    let keyboard = InlineKeyboardMarkup {
        inline_keyboard: vec![vec![button]],
    };

    controller.send_message_with_markup(&text, keyboard).await
}
