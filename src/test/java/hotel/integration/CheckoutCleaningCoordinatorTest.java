package hotel.integration;

import hotel.HotelApplication;
import hotel.observer.Event;
import hotel.observer.EventType;
import hotel.testsupport.KnownHotelFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** These tests use the real wiring of HotelApplication (the composition root), including the order of subscriptions. */
class CheckoutCleaningCoordinatorTest {

    private HotelApplication application;

    @BeforeEach
    void createApplicationWithTheHandCalculatedHotel() {
        application = new HotelApplication(KnownHotelFactory.createHotelWithKnownStatuses(), 3);
    }

    @Test
    void checkoutOnFirstFloorCreatesTaskOnlyForFirstFloorEvenThoughSecondFloorHasDirtyRooms() {
        application.getBookingEventSource().publishGuestCheckedOut("Anna", "101", "Floor 1");

        assertEquals(Set.of("101"), application.getHousekeepingBoard().getRoomNumbersWaitingForCleaning());
        assertEquals(1, application.getJournal().countRecordedEventsOfType(EventType.CLEANING_TASK_CREATED));
    }

    @Test
    void checkoutOnSecondFloorReportsEveryRoomOfThatFloorThatNeedsCleaning() {
        application.getBookingEventSource().publishGuestCheckedOut("Boris", "201", "Floor 2");

        assertEquals(Set.of("201", "202", "203"), application.getHousekeepingBoard().getRoomNumbersWaitingForCleaning());
        assertEquals(3, application.getJournal().countRecordedEventsOfType(EventType.CLEANING_TASK_CREATED));
    }

    @Test
    void checkoutFromAnUnknownFloorCreatesNoTasksAndDoesNotFail() {
        application.getBookingEventSource().publishGuestCheckedOut("Clara", "999", "Floor 9");

        assertEquals(0, application.getJournal().countRecordedEventsOfType(EventType.CLEANING_TASK_CREATED));
    }

    @Test
    void causeIsRecordedBeforeItsEffectsInTheJournal() {
        application.getBookingEventSource().publishGuestCheckedIn("Anna", "101", "Floor 1");
        application.getBookingEventSource().publishGuestCheckedOut("Anna", "101", "Floor 1");

        List<EventType> recordedTypes = application.getJournal().getRecordedEvents().stream().map(Event::type).toList();

        assertEquals(List.of(
                EventType.GUEST_CHECKED_IN,
                EventType.OCCUPANCY_REPORT_PUBLISHED,
                EventType.GUEST_CHECKED_OUT,
                EventType.CLEANING_TASK_CREATED,
                EventType.OCCUPANCY_REPORT_PUBLISHED), recordedTypes);
    }
}
