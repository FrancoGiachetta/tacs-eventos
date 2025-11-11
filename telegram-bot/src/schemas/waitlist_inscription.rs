use chrono::NaiveDateTime;
use serde::Deserialize;

#[derive(Debug, Deserialize)]
pub struct WaitlistInscription {
    pub id: String,
    #[serde(rename = "usuario")]
    pub user: WaitlistUser,
    #[serde(rename = "fechaIngreso")]
    pub entry_date: NaiveDateTime,
}

#[derive(Debug, Deserialize)]
pub struct WaitlistUser {
    pub id: String,
    pub email: String,
}
