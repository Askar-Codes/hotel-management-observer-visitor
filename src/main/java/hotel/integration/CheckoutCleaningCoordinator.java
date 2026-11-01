package hotel.integration;

import hotel.events.CleaningTaskCreated;
import hotel.events.GuestCheckedOut;
import hotel.observer.ConcurrentSubject;
import hotel.observer.Event;
import hotel.observer.EventType;
import hotel.observer.Observer;
import hotel.observer.Subject;
import hotel.tree.BookableUnit;
import hotel.tree.Floor;
import hotel.tree.Hotel;
import hotel.tree.RoomStatus;
import hotel.visitors.BookableUnitsFilteringVisitor;

import java.util.List;
import java.util.Optional;

/**
 * The link between the two patterns:  check-out event  ->  visitor on ONE floor  ->  cleaning task events.
 *
 * When a guest leaves, the coordinator runs the filter visitor only on the floor of that room (not on the whole hotel)
 * and publishes one CleaningTaskCreated event for every room on that floor that needs cleaning.
 * The result goes back into the system as new events, so it is also a subject.
 */
public class CheckoutCleaningCoordinator extends ConcurrentSubject<Event> implements Observer<Event> {

    private final Hotel hotel;

    public CheckoutCleaningCoordinator(Hotel hotel) {
        this.hotel = hotel;
    }

    public void startListeningTo(Subject<Event> source) {
        source.subscribe(EventType.GUEST_CHECKED_OUT, this);
    }

    @Override
    public void handleEvent(Event event) {
        if (event instanceof GuestCheckedOut checkedOut) {
            findFloorByName(checkedOut.floorName()).ifPresent(this::publishCleaningTasksForRoomsOnFloor);
        }
    }

    private Optional<Floor> findFloorByName(String floorName) {
        return hotel.getFloors().stream().filter(floor -> floor.getName().equals(floorName)).findFirst();
    }

    private void publishCleaningTasksForRoomsOnFloor(Floor floor) {
        BookableUnitsFilteringVisitor findRoomsNeedingCleaning =
                new BookableUnitsFilteringVisitor(unit -> unit.getStatus() == RoomStatus.NEEDS_CLEANING);
        List<BookableUnit> roomsNeedingCleaning = floor.accept(findRoomsNeedingCleaning);
        for (BookableUnit room : roomsNeedingCleaning) {
            publishEventToAllSubscribersOfItsType(new CleaningTaskCreated(room.getRoomNumber(), floor.getName()));
        }
    }
}
