package hotel.events;

import hotel.observer.EventType;
import hotel.tree.RoomStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HotelEventsTest {

    @Test
    void everyEventKnowsItsOwnType() {
        assertEquals(EventType.GUEST_CHECKED_IN, new GuestCheckedIn("Anna", "101", "Floor 1").type());
        assertEquals(EventType.GUEST_CHECKED_OUT, new GuestCheckedOut("Anna", "101", "Floor 1").type());
        assertEquals(EventType.ROOM_STATUS_CHANGED, new RoomStatusChanged("101", RoomStatus.VACANT).type());
        assertEquals(EventType.CLEANING_TASK_CREATED, new CleaningTaskCreated("101", "Floor 1").type());
        assertEquals(EventType.OCCUPANCY_REPORT_PUBLISHED, new OccupancyReportPublished(3).type());
    }

    @Test
    void eventsWithTheSameDataAreEqualBecauseTheyAreImmutableValues() {
        assertEquals(new GuestCheckedIn("Anna", "101", "Floor 1"), new GuestCheckedIn("Anna", "101", "Floor 1"));
        assertEquals(new OccupancyReportPublished(3), new OccupancyReportPublished(3));
    }
}
