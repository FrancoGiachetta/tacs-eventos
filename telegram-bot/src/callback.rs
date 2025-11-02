use crate::controller::query_controller::QueryController;
use crate::error::BotError;

use teloxide::dispatching::UpdateHandler;
use teloxide::dptree;

pub mod event_callback;

/// Handles the callbacks invoked by the buttons in the chatbot
pub fn create_callback_handler() -> UpdateHandler<BotError> {
    dptree::entry()
        .filter_map(|ctl: QueryController| ctl.query().data.clone())
        .branch(
            dptree::filter(|data: String| data.starts_with("event::"))
                .branch(event_callback::schema()),
        )
}
