use chrono::{DateTime, Utc};
use serde::{Deserialize, Serialize};

#[derive(Deserialize, Debug)]
pub struct Token {
    pub token: String,
    #[serde(rename = "expiresAt")]
    pub expires_at: DateTime<Utc>,
}

#[derive(Serialize)]
pub struct UserOut {
    pub email: String,
    pub password: String,
    #[serde(skip_serializing_if = "Option::is_none")]
    #[serde(rename = "tipoUsuario")]
    pub user_type: Option<String>,
}
