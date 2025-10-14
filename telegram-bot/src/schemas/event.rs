use std::{fmt::Display, str::FromStr};

use chrono::{NaiveDate, NaiveDateTime};
use serde::{Deserialize, de};
use serde_json::Value;

#[derive(Debug, Clone, PartialEq, Default)]
pub struct EventFilter {
    pub max_price: Option<f32>,
    pub min_price: Option<f32>,
    pub max_date: Option<NaiveDate>,
    pub min_date: Option<NaiveDate>,
    pub category: Option<String>,
    pub keywords: Option<Vec<String>>,
}

#[derive(Debug)]
pub struct Event {
    title: String,
    description: String,
    start_date_time: NaiveDateTime,
    duration_minutes: u32,
    location: String,
    max_capacity: u32,
    price: f32,
    category: String,
    organizer: String,
}

// Defines how to format an Event struct.
impl Display for Event {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        writeln!(f, "🎉 *{}*", self.title)?;
        writeln!(f, "👤 Organizado por: _{}_", self.organizer)?;
        writeln!(f)?;
        writeln!(f, "📝 *Descripción*")?;
        writeln!(f, "{}", self.description)?;
        writeln!(f)?;
        writeln!(f, "📅 *Fecha y Hora*")?;
        writeln!(f, "{}", self.start_date_time)?;
        writeln!(f, "⏱ Duración: {} minutos", self.duration_minutes)?;
        writeln!(f)?;
        writeln!(f, "📍 *Ubicación*")?;
        writeln!(f, "{}", self.location)?;
        writeln!(f)?;
        writeln!(f, "👥 Capacidad: {}", self.max_capacity)?;
        writeln!(f, "💰 Precio: {}", self.price)?;
        writeln!(f, "🏷 Categoría: {}", self.category)?;
        Ok(())
    }
}

// Implement deserialization for a Event.
impl<'de> Deserialize<'de> for Event {
    fn deserialize<D>(deserializer: D) -> Result<Self, D::Error>
    where
        D: serde::Deserializer<'de>,
    {
        let json_value: serde_json::Value = Deserialize::deserialize(deserializer)?;
        derialize_event(json_value).map_err(|err| serde::de::Error::custom(err.to_string()))
    }
}

fn derialize_event(json_value: Value) -> serde_json::Result<Event> {
    // Unwrapig is safe here because we are guaranteed these values are set. If
    // there whever any error, that would be due a bad parsing.
    let title = json_value["titulo"].to_string();
    let description = json_value["descripcion"].to_string();
    let start_date_time = {
        let date_str = json_value["fechaHoraInicio"]
            .as_str()
            .ok_or(de::Error::custom("fechaHoraInicio is not a string"))?;

        NaiveDateTime::from_str(date_str)
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
        organizer,
    })
}
