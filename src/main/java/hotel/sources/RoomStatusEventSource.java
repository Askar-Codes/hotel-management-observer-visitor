package hotel.sources;

import hotel.events.RoomStatusChanged;
import hotel.observer.ConcurrentSubject;
import hotel.observer.Event;
import hotel.tree.RoomStatus;

/** Source 2: the staff reports that a room changed its status (cleaned, broken, repaired). */
public class RoomStatusEventSource extends ConcurrentSubject<Event> {

    public void publishRoomStatusChanged(String roomNumber, RoomStatus newStatus) {
        publishEventToAllSubscribersOfItsType(new RoomStatusChanged(roomNumber, newStatus));
    }
}
