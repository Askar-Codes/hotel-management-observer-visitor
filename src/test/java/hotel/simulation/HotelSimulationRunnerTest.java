package hotel.simulation;

import hotel.GrandHotelFactory;
import hotel.HotelApplication;
import hotel.observer.EventType;
import hotel.sources.BookingEventSource;
import hotel.sources.RoomStatusEventSource;
import hotel.tree.Hotel;
import hotel.visitors.OccupancyStatistics;
import hotel.visitors.OccupiedUnitsCountingVisitor;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HotelSimulationRunnerTest {

    @Test
    void threeProducersLoseNoEvents() {
        assertNoEventIsLostWith(3, 200);
    }

    @Test
    void fourProducersWithOneThousandStayCyclesEachLoseNoEvents() {
        assertNoEventIsLostWith(4, 1000);
    }

    @Test
    void executorThreadsAreStoppedWhenTheRunIsFinished() {
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        HotelSimulationRunner runner = new HotelSimulationRunner(
                executorService, new BookingEventSource(), new RoomStatusEventSource(), GrandHotelFactory.createGrandHotel());

        runner.publishStayCyclesFromParallelProducers(3, 50);

        assertTrue(executorService.isTerminated());
    }

    @Test
    void hotelWithoutRoomsIsRejectedBecauseProducersWouldHaveNothingToDo() {
        Hotel emptyHotel = new Hotel("Empty Hotel", List.of());
        ExecutorService executorService = Executors.newFixedThreadPool(1);

        assertThrows(IllegalArgumentException.class, () -> new HotelSimulationRunner(
                executorService, new BookingEventSource(), new RoomStatusEventSource(), emptyHotel));
        executorService.shutdown();
    }

    /**
     * Every producer plays N cycles of 3 events (check-in, check-out, status change).
     * The revenue manager answers each check-in and each check-out with one report, so reports = 2 * N * producers.
     * Every producer uses its own room and ends with it VACANT, so afterwards nobody is staying and no room is occupied.
     */
    private void assertNoEventIsLostWith(int producerCount, int stayCyclesPerProducer) {
        HotelApplication application = new HotelApplication(GrandHotelFactory.createGrandHotel(), producerCount);

        application.runSimulation(stayCyclesPerProducer);

        int cycles = producerCount * stayCyclesPerProducer;
        assertEquals(cycles, application.getJournal().countRecordedEventsOfType(EventType.GUEST_CHECKED_IN));
        assertEquals(cycles, application.getJournal().countRecordedEventsOfType(EventType.GUEST_CHECKED_OUT));
        assertEquals(cycles, application.getJournal().countRecordedEventsOfType(EventType.ROOM_STATUS_CHANGED));
        assertEquals(2 * cycles, application.getJournal().countRecordedEventsOfType(EventType.OCCUPANCY_REPORT_PUBLISHED));
        assertEquals(2 * cycles, application.getGuestApp().getSentMessages().size());
        assertEquals(0, application.getRevenueManager().getStayingGuestsCount());
        assertEquals(0, application.getFrontDeskPanel().countGuestsCurrentlyStaying());
        assertEquals(new OccupancyStatistics(0, 5), application.getHotel().accept(new OccupiedUnitsCountingVisitor()));
    }
}
