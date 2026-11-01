package hotel.listeners;

import hotel.events.GuestCheckedIn;
import hotel.events.GuestCheckedOut;
import hotel.events.RoomStatusChanged;
import hotel.observer.Event;
import hotel.observer.EventType;
import hotel.observer.Observer;
import hotel.observer.Subject;
import hotel.tree.BookableUnit;
import hotel.tree.Hotel;
import hotel.tree.RoomStatus;
import hotel.visitors.BookableUnitsFilteringVisitor;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Listener 1: the front desk. It knows which guest sleeps in which room
 * and it is the one that keeps the room statuses in the hotel tree up to date.
 * Because of that it must subscribe BEFORE the listeners that read those statuses.
 */
public class FrontDeskPanel implements Observer<Event> {

    private final Hotel hotel;
    private final Map<String, String> guestNamesByRoomNumber = new ConcurrentHashMap<>();

    public FrontDeskPanel(Hotel hotel) {
        this.hotel = hotel;
    }

    public void startListeningTo(Subject<Event> source) {
        source.subscribe(EventType.GUEST_CHECKED_IN, this);
        source.subscribe(EventType.GUEST_CHECKED_OUT, this);
        source.subscribe(EventType.ROOM_STATUS_CHANGED, this);
    }

    @Override
    public void handleEvent(Event event) {
        if (event instanceof GuestCheckedIn checkedIn) {
            registerGuestAndMarkRoomOccupied(checkedIn);
        } else if (event instanceof GuestCheckedOut checkedOut) {
            removeGuestAndMarkRoomNeedsCleaning(checkedOut);
        } else if (event instanceof RoomStatusChanged statusChanged) {
            changeStatusOfRoom(statusChanged.roomNumber(), statusChanged.newStatus());
        }
    }

    public Optional<String> findGuestStayingInRoom(String roomNumber) {
        return Optional.ofNullable(guestNamesByRoomNumber.get(roomNumber));
    }

    public int countGuestsCurrentlyStaying() {
        return guestNamesByRoomNumber.size();
    }

    private void registerGuestAndMarkRoomOccupied(GuestCheckedIn checkedIn) {
        guestNamesByRoomNumber.put(checkedIn.roomNumber(), checkedIn.guestName());
        changeStatusOfRoom(checkedIn.roomNumber(), RoomStatus.OCCUPIED);
    }

    private void removeGuestAndMarkRoomNeedsCleaning(GuestCheckedOut checkedOut) {
        guestNamesByRoomNumber.remove(checkedOut.roomNumber());
        changeStatusOfRoom(checkedOut.roomNumber(), RoomStatus.NEEDS_CLEANING);
    }

    private void changeStatusOfRoom(String roomNumber, RoomStatus newStatus) {
        BookableUnitsFilteringVisitor findRoomByNumber =
                new BookableUnitsFilteringVisitor(unit -> unit.getRoomNumber().equals(roomNumber));
        for (BookableUnit foundUnit : hotel.accept(findRoomByNumber)) {
            foundUnit.setStatus(newStatus);
        }
    }
}
