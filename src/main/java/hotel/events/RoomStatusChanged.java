package hotel.events;

import hotel.observer.Event;
import hotel.observer.EventType;
import hotel.tree.RoomStatus;

public record RoomStatusChanged(String roomNumber, RoomStatus newStatus) implements Event {

    @Override
    public EventType type() {
        return EventType.ROOM_STATUS_CHANGED;
    }
}
