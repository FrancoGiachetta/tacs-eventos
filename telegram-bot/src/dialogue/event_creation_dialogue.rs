#[derive(Clone, Debug)]
pub enum EventCreationState {
    EnterTitle,
    EnterDescription,
    EnterDate,
    EnterDuration,
    EnterLocation,
    EnterMaxParticipants,
    EnterPrice,
    EnterCategory,
}
