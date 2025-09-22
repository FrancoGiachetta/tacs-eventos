use std::str::FromStr;

use chrono::NaiveDateTime;
use serde::Deserialize;

pub struct Event {
    title: String,
    description: String,
    start_date_time: NaiveDateTime,
    duration_minutes: u32,
    location: String,
    max_capacity: u32,
    price: f32,
    category: String,
    is_open: bool,
    organizer: String,
}

impl<'de> Deserialize<'de> for Event {
    fn deserialize<D>(deserializer: D) -> Result<Self, D::Error>
    where
        D: serde::Deserializer<'de>,
    {
        // Unwrapig is safe here because we are guaranteed these values are set. If
        // there whever any error, that would be due a bad parsing.
        let json_value: serde_json::Value = Deserialize::deserialize(deserializer)?;

        let title = json_value["titulo"].to_string();
        let description = json_value["descripcion"].to_string();
        let start_date_time = {
            let date_str = json_value["fechaHoraInicio"].to_string();

            NaiveDateTime::from_str(&date_str)
                .map_err(|_| serde::de::Error::custom("Couldn't parse date"))?
        };
        let duration_minutes = json_value["duracionMinutos"]
            .to_string()
            .parse::<u32>()
            .unwrap();
        let location = json_value["ubicacion"].to_string();
        let max_capacity = json_value["cupoMaximo"].to_string().parse::<u32>().unwrap();
        let price = json_value["precio"].to_string().parse::<f32>().unwrap();
        let category = json_value["categoria"].to_string();
        let is_open = json_value[""].to_string().parse::<bool>().unwrap();
        let organizer = json_value["organizador"]["nombre"].to_string();

        Ok(Event {
            title,
            description,
            start_date_time,
            duration_minutes,
            location,
            max_capacity,
            price,
            category,
            is_open,
            organizer,
        })
    }
}
