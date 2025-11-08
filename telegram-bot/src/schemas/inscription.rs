use std::fmt::Display;

use chrono::{DateTime, NaiveDateTime, Utc};
use serde::{Deserialize, Deserializer};

#[derive(Debug, Deserialize)]
pub enum InscriptionState {
    #[serde(rename = "CONFIRMADA")]
    Confirmed,
    #[serde(rename = "CANCELADA")]
    Rejected,
    #[serde(rename = "PENDIENTE")]
    Pending,
}

#[derive(Debug, Deserialize)]
pub struct Inscription {
    pub id: String,
    #[serde(rename = "estado")]
    pub state: InscriptionState,
    pub email: String,
    #[serde(rename = "fechaInscripcion")]
    pub date: NaiveDateTime,
    #[serde(rename = "eventoId")]
    pub event_id: String,
}

impl Display for InscriptionState {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        match self {
            InscriptionState::Confirmed => write!(f, "✅ Confirmada"),
            InscriptionState::Rejected => write!(f, "❌ Rechazada"),
            InscriptionState::Pending => write!(f, "⏳ Pendiente"),
        }
    }
}

impl Display for Inscription {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        writeln!(f, "<b>Inscripción #{}</b>", self.id)?;
        writeln!(f)?;
        writeln!(f, "📧 <b>Email</b>: {}", self.email)?;
        writeln!(f)?;
        writeln!(f, "📊 <b>Estado</b>: {}", self.state)?;
        writeln!(f)?;
        writeln!(f, "📅 <b>Fecha de Inscripción</b>: {}", self.date)?;
        writeln!(f)?;
        writeln!(f, "🎫 <b>ID del Evento</b>: {}", self.event_id)?;
        Ok(())
    }
}
