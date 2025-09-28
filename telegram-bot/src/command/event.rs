use std::{str::FromStr, sync::Arc};

use chrono::NaiveDate;
use lazy_static::lazy_static;
use regex::Regex;
use teloxide::{Bot, prelude::Requester, types::Message, utils::command::ParseError};
use tracing::{error, info};

use crate::{error::BotError, request_client::RequestClient, schemas::event::EventFilter};

/// List open events.
///
/// Sends a GET request looking for all the still open events. The command also
/// allows to pass arguments to filter events.
pub async fn list_events(
    bot: Arc<Bot>,
    msg: &Message,
    req_client: &RequestClient,
    filters: EventFilter,
) -> Result<(), BotError> {
    info!("Listing list_events!");

    match req_client.send_get_events_list_request(filters).await {
        Ok(events_list) => {
            let events_msg = events_list
                .into_iter()
                .map(|e| format!("{e}"))
                .collect::<Vec<String>>()
                .join("\n");

            bot.send_message(msg.chat.id, events_msg).await?;
        }
        Err(e) => {
            error!("Got an error while performing the request: {}", e);

            bot.send_message(
                msg.chat.id,
                "There was an error while performing the request command!",
            )
            .await?;
        }
    }
    Ok(())
}

/// Parse event filters.
///
/// The use may pass filters to apply during the events fetch. The function
/// provides the parsing for an easier handling.
pub fn parse_event_filters(input: String) -> Result<(EventFilter,), ParseError> {
    lazy_static! {
        static ref MIN_PRICE_PREFIX: Regex = Regex::new(r"\bmin_price=(\d+(\.\d+)?)\b").unwrap();
        static ref MAX_PRICE_PREFIX: Regex = Regex::new(r"\bmax_price=(\d+(\.\d+)?)\b").unwrap();
        static ref MIN_DATE_PREFIX: Regex =
            Regex::new(r"\bmin_date=(\d{1,2}-\d{1,2}-\d{4})\b").unwrap();
        static ref MAX_DATE_PREFIX: Regex =
            Regex::new(r"\bmax_date=(\d{1,2}-\d{1,2}-\d{4})\b").unwrap();
        static ref CATEGORY_PREFIX: Regex = Regex::new(r"\bcategory=(\w+)\b").unwrap();
        static ref KEYWORDS_PREFIX: Regex = Regex::new(r"\bkeywords=(\w+(,\w+)*)\b").unwrap();
    }

    let min_price = MIN_PRICE_PREFIX
        .captures(&input)
        .and_then(|caps| caps.get(1))
        .and_then(|p| p.as_str().parse::<f32>().ok());
    let max_price = MAX_PRICE_PREFIX
        .captures(&input)
        .and_then(|caps| caps.get(1))
        .and_then(|p| p.as_str().parse::<f32>().ok());
    let min_date = MIN_DATE_PREFIX
        .captures(&input)
        .and_then(|caps| caps.get(1))
        .and_then(|date| {
            let mut date = date.as_str().split("-").collect::<Vec<&str>>();

            date.reverse();

            NaiveDate::from_str(&date.join("-")).ok()
        });
    let max_date = MAX_DATE_PREFIX
        .captures(&input)
        .and_then(|caps| caps.get(1))
        .and_then(|date| {
            let mut date = date.as_str().split("-").collect::<Vec<&str>>();

            date.reverse();

            NaiveDate::from_str(&date.join("-")).ok()
        });
    let category = CATEGORY_PREFIX
        .captures(&input)
        .and_then(|c| c.get(1))
        .and_then(|c| Some(c.as_str().to_string()));
    let keywords = KEYWORDS_PREFIX
        .captures(&input)
        .and_then(|ks| ks.get(1))
        .and_then(|ks| {
            Some(
                ks.as_str()
                    .split(",")
                    .map(String::from)
                    .collect::<Vec<String>>(),
            )
        });

    Ok((EventFilter {
        min_price,
        max_price,
        min_date,
        max_date,
        category,
        keywords,
    },))
}

#[cfg(test)]
mod tests {
    use chrono::NaiveDate;

    use super::{EventFilter, parse_event_filters};

    #[test]
    fn empty_filter() {
        let filter = parse_event_filters("".to_string()).unwrap();

        assert_eq!(filter.0, EventFilter::default())
    }

    #[test]
    fn filter_with_price_range() {
        let filter = parse_event_filters("min_price=12 max_price=23".to_string()).unwrap();

        assert_eq!(
            filter.0,
            EventFilter {
                min_price: Some(12.0),
                max_price: Some(23.0),
                min_date: None,
                max_date: None,
                category: None,
                keywords: None
            }
        )
    }

    #[test]
    fn filter_with_price_range_with_decimals() {
        let filter = parse_event_filters("min_price=12.34 max_price=23.54".to_string()).unwrap();

        assert_eq!(
            filter.0,
            EventFilter {
                min_price: Some(12.34),
                max_price: Some(23.54),
                min_date: None,
                max_date: None,
                category: None,
                keywords: None
            }
        )
    }

    #[test]
    fn filter_with_date_range() {
        let filter =
            parse_event_filters("min_date=12-02-2024 max_date=23-02-2024".to_string()).unwrap();

        assert_eq!(
            filter.0,
            EventFilter {
                min_price: None,
                max_price: None,
                min_date: NaiveDate::from_ymd_opt(2024, 2, 12),
                max_date: NaiveDate::from_ymd_opt(2024, 2, 23),
                category: None,
                keywords: None
            }
        )
    }

    #[test]
    fn filter_with_date_range_with_one_digit_month_and_day() {
        let filter =
            parse_event_filters("min_date=2-2-2024 max_date=3-2-2024".to_string()).unwrap();

        assert_eq!(
            filter.0,
            EventFilter {
                min_price: None,
                max_price: None,
                min_date: NaiveDate::from_ymd_opt(2024, 2, 2),
                max_date: NaiveDate::from_ymd_opt(2024, 2, 3),
                category: None,
                keywords: None
            }
        )
    }

    #[test]
    fn filter_with_category() {
        let filter = parse_event_filters("category=Category".to_string()).unwrap();

        assert_eq!(
            filter.0,
            EventFilter {
                min_price: None,
                max_price: None,
                min_date: None,
                max_date: None,
                category: Some("Category".to_string()),
                keywords: None
            }
        )
    }

    #[test]
    fn filter_keywords() {
        let filter = parse_event_filters("keywords=k1,k2,k3,k4".to_string()).unwrap();
        let keyws = vec!["k1", "k2", "k3", "k4"]
            .into_iter()
            .map(String::from)
            .collect();

        assert_eq!(
            filter.0,
            EventFilter {
                min_price: None,
                max_price: None,
                min_date: None,
                max_date: None,
                category: None,
                keywords: Some(keyws)
            }
        )
    }

    #[test]
    fn filter_full() {
        let filter = parse_event_filters(
            "min_price=12 max_price=23 category=Category keywords=k1,k2,k3,k4 min_date=2-2-2024 max_date=3-2-2024"
                .to_string(),
        )
        .unwrap();
        let keyws = vec!["k1", "k2", "k3", "k4"]
            .into_iter()
            .map(String::from)
            .collect();

        assert_eq!(
            filter.0,
            EventFilter {
                min_price: Some(12.0),
                max_price: Some(23.0),
                min_date: NaiveDate::from_ymd_opt(2024, 2, 2),
                max_date: NaiveDate::from_ymd_opt(2024, 2, 3),
                category: Some("Category".to_string()),
                keywords: Some(keyws)
            }
        )
    }
}
