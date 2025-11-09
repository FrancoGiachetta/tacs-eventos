use tracing::info;

use crate::callback::event_callback::CLOSE_EVENT_PREFIX;
use crate::controller::general_controller::GeneralController;
use crate::error::request_client_error::handle_http_request_error;
use crate::error::BotError;
use crate::schemas::event_organizer_view::EventOrganizerView;
use crate::{bot::BotResult, callback::event_callback::OPEN_EVENT_PREFIX};
use teloxide::types::{InlineKeyboardButton, InlineKeyboardMarkup};

/// Lists the events organized by the user
pub async fn handle_my_events(controller: GeneralController) -> BotResult<()> {
    info!("Listing my_events!");

    let token = controller.auth().get_session_token(&controller.chat_id())?;

    match controller
        .request_client()
        .send_get_my_events_list_request(&token)
        .await
    {
        Ok(events_list) => {
            // Sends a message swowing each event
            for event in events_list {
                let pending_count = controller
                    .request_client()
                    .send_get_pending_inscriptions_count_request(&token, &event.id)
                    .await?;
                let confirmed_count = controller
                    .request_client()
                    .send_get_confirmed_inscriptions_count_request(&token, &event.id)
                    .await?;
                send_event_message_with_callback(
                    &controller,
                    &event,
                    pending_count,
                    confirmed_count,
                )
                .await?;
            }
        }
        Err(err) => {
            handle_http_request_error(&controller, err).await?;
        }
    }

    Ok(())
}

async fn send_event_message_with_callback(
    controller: &GeneralController,
    event: &EventOrganizerView,
    pending_count: u64,
    confirmed_count: u64,
) -> BotResult<()> {
    let mut text = format!("{event}");
    text.push_str(&format!(
        "\n\n<b>✍️ Inscripciones pendientes:</b> {}\n<b>✅ Inscripciones confirmadas:</b> {}",
        pending_count, confirmed_count
    ));
    let callback = create_event_callback(event);
    let keyboard = InlineKeyboardMarkup {
        inline_keyboard: vec![vec![callback]],
    };
    controller
        .send_message_with_callback(&text, keyboard)
        .await
        .map_err(BotError::from)
}

/// Creates a button for opening or closing the event
fn create_event_callback(event: &EventOrganizerView) -> InlineKeyboardButton {
    let event_id = event.id().to_string();

    if event.open() {
        // if the event is open
        // Creates a button for closing the inscriptions for that event
        let callback_data = format!("{}{}", CLOSE_EVENT_PREFIX, event_id);
        InlineKeyboardButton::callback("Cerrar inscripciones", callback_data)
    } else {
        // if the event is closed
        // Creates a button for opening the inscriptions for that event
        let callback_data = format!("{}{}", OPEN_EVENT_PREFIX, event_id);
        InlineKeyboardButton::callback("Abrir inscripciones", callback_data)
    }
}
