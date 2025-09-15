use teloxide::Bot;

use crate::error::BotError;

mod bot;
pub mod command;
mod db;
mod error;
mod schemas;

#[tokio::main]
async fn main() -> Result<(), BotError> {
    // Load .env vars
    dotenv::dotenv()?;

    // This will panic if TELOXIDE_TOKEN is not found.
    let bot = Bot::from_env();

    teloxide::repl(bot);

    Ok(())
}
