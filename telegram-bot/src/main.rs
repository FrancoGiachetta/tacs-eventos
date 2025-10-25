use crate::bot::BotResult;

mod auth;
mod bot;
mod callback;
pub mod command;
mod controller;
pub mod dialogue;
mod error;
mod error_handling;
mod request_client;
mod schemas;

#[tokio::main]
async fn main() -> BotResult<()> {
    // Load .env vars only in debug mode.
    #[cfg(debug_assertions)]
    dotenv::dotenv()?;

    // Configure logging.
    tracing_subscriber::fmt()
        .with_max_level(tracing::Level::INFO)
        .with_target(false)
        .init();

    bot::run().await?;

    Ok(())
}
