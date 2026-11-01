package hotel.observer;

/**
 * Every kind of event that can travel through the system.
 * Listeners subscribe to one or more of these types.
 */
public enum EventType {
    GUEST_CHECKED_IN,
    GUEST_CHECKED_OUT,
    ROOM_STATUS_CHANGED,
    CLEANING_TASK_CREATED,
    OCCUPANCY_REPORT_PUBLISHED
}
