use chrono::NaiveDate;
use serde::{Deserialize, Serialize};

#[derive(Deserialize)]
pub struct Token {
    pub token: String,
    #[serde(rename(deserialize = "expiresAt"))]
    pub expires_at: NaiveDate,
}

#[derive(Serialize)]
pub struct UserOut {
    pub email: String,
    pub password: String,
    #[serde(rename(deserialize = "TipoUsuario"))]
    pub r#type: String,
}
