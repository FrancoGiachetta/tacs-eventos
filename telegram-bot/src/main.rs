use crate::error::BotError;

mod bot;
pub mod command;
mod error;
mod request_client;
mod schemas;

#[tokio::main]
async fn main() -> Result<(), BotError> {
    // Load .env vars
    dotenv::dotenv()?;

    // Configure logging.
    tracing_subscriber::fmt()
        .with_max_level(tracing::Level::INFO)
        .with_target(false)
        .init();

    bot::run().await?;

    Ok(())
}
