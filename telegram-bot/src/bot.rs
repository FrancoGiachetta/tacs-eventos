use teloxide::{
    Bot,
    dispatching::{DefaultKey, UpdateFilterExt},
    dptree,
    prelude::Dispatcher,
    types::Update,
};

use crate::{
    command::{Command, handle_command},
    error::BotError,
};

pub struct TACSEventBot {
    bot: Bot,
    dispatcher: Dispatcher<Bot, BotError, DefaultKey>,
    // db_con
}

impl TACSEventBot {
    pub fn new() -> Self {
        let bot = Bot::from_env();

        let dispatcher = {
            let handler = dptree::entry().branch(Update::filter_message().endpoint(handle_command));

            Dispatcher::builder(bot.clone(), handler)
                .default_handler(|_| async {})
                .build()
        };

        Self { bot, dispatcher }
    }

    pub async fn run(&mut self) {
        self.dispatcher.dispatch().await;
    }
}
