package hotel.events;

import hotel.observer.Event;
import hotel.observer.EventType;

public record CleaningTaskCreated(String roomNumber, String floorName) implements Event {

    @Override
    public EventType type() {
        return EventType.CLEANING_TASK_CREATED;
    }
}
