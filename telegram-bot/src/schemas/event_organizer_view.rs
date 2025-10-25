use crate::schemas::event::Event;
use serde::Deserialize;
use std::fmt::{Display, Formatter};

#[derive(Debug)]
/// Holds all the information the Organizer needs to see about an event.
/// Includes the event, its id, and whether the registrations are open.
pub struct EventOrganizerView {
    id: String,
    open: bool,
    event: Event,
}

impl EventOrganizerView {
    pub fn id(&self) -> &String {
        &self.id
    }
}

impl Display for EventOrganizerView {
    fn fmt(&self, f: &mut Formatter<'_>) -> std::fmt::Result {
        // Writes the field in this structure
        writeln!(f, "🏷️ Id de evento: _{}_", self.id)?;
        writeln!(
            f,
            "📋 Inscripciones abiertas: {}",
            if self.open { "si" } else { "no" }
        )?;
        // Writes all the fields in the Event structure
        self.event.fmt(f)
    }
}

// Implement deserialization for an EventOrganizerView.
impl<'de> Deserialize<'de> for EventOrganizerView {
    fn deserialize<D>(deserializer: D) -> Result<Self, D::Error>
    where
        D: serde::Deserializer<'de>,
    {
        let json_value: serde_json::Value = Deserialize::deserialize(deserializer)?; // Json content
        let id = json_value["id"].to_string();
        let open = json_value["inscripcionesAbiertas"].as_bool().unwrap();
        // Deserializes all the fields in the Event structure
        let event = Event::deserialize(json_value).unwrap();
        Ok(EventOrganizerView { id, event, open })
    }
}
