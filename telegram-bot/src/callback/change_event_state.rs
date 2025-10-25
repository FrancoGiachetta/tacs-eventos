use crate::bot::BotResult;
use crate::controller::Controller;

pub async fn close_event(controller: Controller, event_id: String) -> BotResult<()> {
    controller
        .request_client()
        .send_close_event_request(
            controller.auth().get_session_token(&controller.chat_id())?,
            &event_id,
        )
        .await?;
    Ok(())
}

pub async fn open_event(controller: Controller, event_id: String) -> BotResult<()> {
    controller
        .request_client()
        .send_open_event_request(
            controller.auth().get_session_token(&controller.chat_id())?,
            &event_id,
        )
        .await?;
    Ok(())
}
