use std::{fmt::Display, str::FromStr};

use chrono::{NaiveDate, NaiveDateTime};
use serde::{Deserialize, Serialize, Serializer, de};
use serde_json::Value;

use derive_builder::Builder;

#[derive(Debug, Clone, PartialEq, Default)]
pub struct EventFilter {
    pub max_price: Option<f32>,
    pub min_price: Option<f32>,
    pub max_date: Option<NaiveDate>,
    pub min_date: Option<NaiveDate>,
    pub category: Option<String>,
    pub keywords: Option<Vec<String>>,
}

#[derive(Debug, Builder)]
#[builder(derive(Debug))]
pub struct Event {
    pub title: String,
    pub description: String,
    pub start_date_time: NaiveDateTime,
    pub duration_minutes: u32,
    pub location: String,
    pub max_capacity: u32,
    pub price: f32,
    pub category: String,
    pub organizer: String,
}

// Defines how to format an Event struct.
impl Display for Event {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        writeln!(f, "<b>{}</b>", self.title)?;
        writeln!(f)?;
        writeln!(f, "👤 Organizado por: {}", self.organizer)?;
        writeln!(f)?;
        writeln!(f, "📝 <b>Descripción</b>")?;
        writeln!(f, "{}", self.description)?;
        writeln!(f)?;
        writeln!(f, "📅 <b>Fecha y Hora</b>: {}", self.start_date_time)?;
        writeln!(f)?;
        writeln!(f, "⏱ <b>Duración</b>: {} minutos", self.duration_minutes)?;
        writeln!(f)?;
        writeln!(f, "📍 <b>Ubicación</b>: {}", self.location)?;
        writeln!(f)?;
        writeln!(f, "👥 <b>Capacidad</b>: {}", self.max_capacity)?;
        writeln!(f)?;
        writeln!(f, "💰 <b>Precio</b>: ${}", self.price)?;
        writeln!(f)?;
        writeln!(f, "🏷 <b>Categoría</b>: {}", self.category)?;
        Ok(())
    }
}

// Implement serialization for an Event.
impl Serialize for Event {
    fn serialize<S>(&self, serializer: S) -> Result<S::Ok, S::Error>
    where
        S: Serializer,
    {
        let json_value = serialize_event(self).map_err(serde::ser::Error::custom)?;
        json_value.serialize(serializer)
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

fn serialize_event(event: &Event) -> serde_json::Result<Value> {
    Ok(serde_json::json!({
        "titulo": event.title,
        "descripcion": event.description,
        "fechaHoraInicio": event.start_date_time.format("%Y-%m-%dT%H:%M:%S").to_string(),
        "duracionMinutos": event.duration_minutes,
        "ubicacion": event.location,
        "cupoMaximo": event.max_capacity,
        "precio": event.price,
        "categoria": event.category,
    }))
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
    let organizer = json_value["organizador"]["email"].to_string();

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
