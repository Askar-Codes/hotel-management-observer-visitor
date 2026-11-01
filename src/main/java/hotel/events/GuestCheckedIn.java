package hotel.events;

import hotel.observer.Event;
import hotel.observer.EventType;

public record GuestCheckedIn(String guestName, String roomNumber, String floorName) implements Event {

    @Override
    public EventType type() {
        return EventType.GUEST_CHECKED_IN;
    }
}
