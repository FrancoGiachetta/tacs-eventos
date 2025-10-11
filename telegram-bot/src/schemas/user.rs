use chrono::NaiveDate;
use serde::{Deserialize, Serialize};

#[derive(Deserialize, Debug)]
pub struct Token {
    pub token: String,
    #[serde(rename(deserialize = "expiresAt"))]
    pub expires_at: NaiveDate,
}

#[derive(Serialize)]
pub struct UserOut {
    pub email: String,
    pub password: String,
    #[serde(skip_serializing_if = "Option::is_none")]
    #[serde(rename = "tipoUsuario")]
    pub user_type: Option<String>,
}
