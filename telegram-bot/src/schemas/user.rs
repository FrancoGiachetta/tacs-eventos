use chrono::{DateTime, Utc};
use serde::{Deserialize, Serialize};

#[derive(Serialize)]
pub struct UserOut {
    pub email: String,
    pub password: String,
    #[serde(skip_serializing_if = "Option::is_none")]
    #[serde(rename = "tipoUsuario")]
    pub user_type: Option<String>,
}

#[derive(Deserialize)]
pub struct UserIn {
    pub id: String,
    pub email: String,
    #[serde(skip_deserializing)]
    #[serde(rename = "tipoUsuario")]
    user_type: Option<String>,
}

// Sessions

#[derive(Deserialize, Debug, Clone)]
pub struct Token {
    pub token: String,
    #[serde(rename = "expiresAt")]
    pub expires_at: DateTime<Utc>,
}

#[derive(Debug, Clone)]
pub struct Session {
    pub user_id: String,
    pub email: String,
    pub password: String,
    pub token: Token,
    pub is_active: bool,
}
