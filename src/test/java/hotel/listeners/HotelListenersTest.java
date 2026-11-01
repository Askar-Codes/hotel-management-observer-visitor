package hotel.listeners;

import hotel.GrandHotelFactory;
import hotel.HotelApplication;
import hotel.events.OccupancyReportPublished;
import hotel.observer.EventType;
import hotel.testsupport.RecordingObserver;
import hotel.testsupport.RoomNumbers;
import hotel.tree.BookableUnit;
import hotel.tree.RoomStatus;
import hotel.visitors.BookableUnitsFilteringVisitor;
import hotel.visitors.TotalDailyRevenueCalculatingVisitor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HotelListenersTest {

    private static final int HALL_AND_RESTAURANT_REVENUE_WITH_NO_GUESTS = 1000 + 700;

    private HotelApplication application;

    @BeforeEach
    void createApplicationWithAnEmptyGrandHotel() {
        application = new HotelApplication(GrandHotelFactory.createGrandHotel(), 3);
    }

    @Test
    void revenueManagerRepublishesTheStayingGuestCountToItsOwnSubscribers() {
        RecordingObserver reportReader = new RecordingObserver();
        application.getRevenueManager().subscribe(EventType.OCCUPANCY_REPORT_PUBLISHED, reportReader);

        application.getBookingEventSource().publishGuestCheckedIn("Anna", "101", "Floor 1");
        application.getBookingEventSource().publishGuestCheckedIn("Boris", "102", "Floor 1");
        application.getBookingEventSource().publishGuestCheckedIn("Clara", "201", "Floor 2");
        application.getBookingEventSource().publishGuestCheckedOut("Anna", "101", "Floor 1");

        assertEquals(List.of(
                new OccupancyReportPublished(1),
                new OccupancyReportPublished(2),
                new OccupancyReportPublished(3),
                new OccupancyReportPublished(2)), reportReader.getReceivedEvents());
        assertEquals(2, application.getRevenueManager().getStayingGuestsCount());
    }

    @Test
    void frontDeskRemembersTheGuestAndMarksTheRoomOccupied() {
        application.getBookingEventSource().publishGuestCheckedIn("Anna", "101", "Floor 1");

        assertEquals(Optional.of("Anna"), application.getFrontDeskPanel().findGuestStayingInRoom("101"));
        assertEquals(1, application.getFrontDeskPanel().countGuestsCurrentlyStaying());
        int revenue = application.getHotel().accept(new TotalDailyRevenueCalculatingVisitor());
        assertEquals(HALL_AND_RESTAURANT_REVENUE_WITH_NO_GUESTS + 100, revenue);
    }

    @Test
    void frontDeskForgetsTheGuestAfterCheckout() {
        application.getBookingEventSource().publishGuestCheckedIn("Anna", "101", "Floor 1");
        application.getBookingEventSource().publishGuestCheckedOut("Anna", "101", "Floor 1");

        assertEquals(Optional.empty(), application.getFrontDeskPanel().findGuestStayingInRoom("101"));
        assertEquals(0, application.getFrontDeskPanel().countGuestsCurrentlyStaying());
    }

    @Test
    void frontDeskAppliesStatusChangesThatComeFromTheSecondSource() {
        application.getRoomStatusEventSource().publishRoomStatusChanged("102", RoomStatus.UNDER_MAINTENANCE);

        List<BookableUnit> underMaintenance = application.getHotel().accept(
                new BookableUnitsFilteringVisitor(unit -> unit.getStatus() == RoomStatus.UNDER_MAINTENANCE));
        assertEquals(List.of("102"), RoomNumbers.of(underMaintenance));
    }

    @Test
    void guestAppSendsWelcomeAndGoodbyeMessages() {
        application.getBookingEventSource().publishGuestCheckedIn("Anna", "101", "Floor 1");
        application.getBookingEventSource().publishGuestCheckedOut("Anna", "101", "Floor 1");

        assertEquals(List.of(
                "Welcome, Anna! Your room is 101.",
                "Goodbye, Anna! Thank you for staying with us."), application.getGuestApp().getSentMessages());
    }

    @Test
    void journalRecordsEventsFromEverySourceItListensTo() {
        application.getBookingEventSource().publishGuestCheckedIn("Anna", "101", "Floor 1");
        application.getRoomStatusEventSource().publishRoomStatusChanged("102", RoomStatus.UNDER_MAINTENANCE);

        assertEquals(1, application.getJournal().countRecordedEventsOfType(EventType.GUEST_CHECKED_IN));
        assertEquals(1, application.getJournal().countRecordedEventsOfType(EventType.ROOM_STATUS_CHANGED));
        assertEquals(1, application.getJournal().countRecordedEventsOfType(EventType.OCCUPANCY_REPORT_PUBLISHED));
    }
}
