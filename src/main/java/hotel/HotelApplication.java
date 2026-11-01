package hotel;

import hotel.integration.CheckoutCleaningCoordinator;
import hotel.listeners.AllHotelEventsChronologicalJournal;
import hotel.listeners.FrontDeskPanel;
import hotel.listeners.GuestApp;
import hotel.listeners.HousekeepingBoard;
import hotel.listeners.RevenueManager;
import hotel.observer.EventType;
import hotel.simulation.HotelSimulationRunner;
import hotel.sources.BookingEventSource;
import hotel.sources.RoomStatusEventSource;
import hotel.tree.Hotel;
import hotel.visitors.HotelStructureJsonExportVisitor;
import hotel.visitors.OccupiedUnitsCountingVisitor;
import hotel.visitors.TotalDailyRevenueCalculatingVisitor;

import java.util.concurrent.Executors;

/**
 * THE composition root: the only place where objects are created and connected.
 * Every other class gets what it needs through its constructor and never creates its own helpers.
 *
 * Run it with optional arguments:   producerThreadCount (default 4)   stayCyclesPerProducer (default 250)
 */
public final class HotelApplication {

    private static final int DEFAULT_PRODUCER_THREAD_COUNT = 4;
    private static final int DEFAULT_STAY_CYCLES_PER_PRODUCER = 250;

    private final Hotel hotel;
    private final int producerThreadCount;
    private final BookingEventSource bookingEventSource;
    private final RoomStatusEventSource roomStatusEventSource;
    private final FrontDeskPanel frontDeskPanel;
    private final CheckoutCleaningCoordinator cleaningCoordinator;
    private final HousekeepingBoard housekeepingBoard;
    private final RevenueManager revenueManager;
    private final GuestApp guestApp;
    private final AllHotelEventsChronologicalJournal journal;
    private final HotelSimulationRunner simulationRunner;

    public HotelApplication(Hotel hotel, int producerThreadCount) {
        this.hotel = hotel;
        this.producerThreadCount = producerThreadCount;
        this.bookingEventSource = new BookingEventSource();
        this.roomStatusEventSource = new RoomStatusEventSource();
        this.frontDeskPanel = new FrontDeskPanel(hotel);
        this.cleaningCoordinator = new CheckoutCleaningCoordinator(hotel);
        this.housekeepingBoard = new HousekeepingBoard();
        this.revenueManager = new RevenueManager();
        this.guestApp = new GuestApp();
        this.journal = new AllHotelEventsChronologicalJournal();
        this.simulationRunner = new HotelSimulationRunner(
                Executors.newFixedThreadPool(producerThreadCount), bookingEventSource, roomStatusEventSource, hotel);
        connectListenersToSources();
    }

    /**
     * The ORDER of subscriptions matters, because listeners are called in the order they subscribed:
     *  1. the journal is first, so a cause is written down before the events it triggers;
     *  2. the front desk is before the coordinator, so the room status is already NEEDS_CLEANING when the coordinator looks.
     */
    private void connectListenersToSources() {
        journal.startRecordingEventsOf(bookingEventSource);
        journal.startRecordingEventsOf(roomStatusEventSource);
        journal.startRecordingEventsOf(revenueManager);
        journal.startRecordingEventsOf(cleaningCoordinator);

        frontDeskPanel.startListeningTo(bookingEventSource);
        frontDeskPanel.startListeningTo(roomStatusEventSource);
        cleaningCoordinator.startListeningTo(bookingEventSource);
        revenueManager.startListeningTo(bookingEventSource);
        guestApp.startListeningTo(bookingEventSource);
        housekeepingBoard.startListeningTo(cleaningCoordinator);
    }

    public void runSimulation(int stayCyclesPerProducer) {
        simulationRunner.publishStayCyclesFromParallelProducers(producerThreadCount, stayCyclesPerProducer);
    }

    public Hotel getHotel() {
        return hotel;
    }

    public BookingEventSource getBookingEventSource() {
        return bookingEventSource;
    }

    public RoomStatusEventSource getRoomStatusEventSource() {
        return roomStatusEventSource;
    }

    public FrontDeskPanel getFrontDeskPanel() {
        return frontDeskPanel;
    }

    public HousekeepingBoard getHousekeepingBoard() {
        return housekeepingBoard;
    }

    public RevenueManager getRevenueManager() {
        return revenueManager;
    }

    public GuestApp getGuestApp() {
        return guestApp;
    }

    public AllHotelEventsChronologicalJournal getJournal() {
        return journal;
    }

    public static void main(String[] args) {
        int producerThreadCount = readIntegerArgumentOrUseDefault(args, 0, DEFAULT_PRODUCER_THREAD_COUNT);
        int stayCyclesPerProducer = readIntegerArgumentOrUseDefault(args, 1, DEFAULT_STAY_CYCLES_PER_PRODUCER);

        HotelApplication application = new HotelApplication(GrandHotelFactory.createGrandHotel(), producerThreadCount);
        application.runSimulation(stayCyclesPerProducer);
        printSummary(application, producerThreadCount, stayCyclesPerProducer);
    }

    private static int readIntegerArgumentOrUseDefault(String[] arguments, int position, int defaultValue) {
        return arguments.length > position ? Integer.parseInt(arguments[position]) : defaultValue;
    }

    private static void printSummary(HotelApplication application, int producerThreadCount, int stayCyclesPerProducer) {
        AllHotelEventsChronologicalJournal journal = application.getJournal();
        System.out.println("Producers: " + producerThreadCount + ", stay cycles per producer: " + stayCyclesPerProducer);
        for (EventType eventType : EventType.values()) {
            System.out.println("  " + eventType + ": " + journal.countRecordedEventsOfType(eventType));
        }
        System.out.println("Guests staying now: " + application.getRevenueManager().getStayingGuestsCount());
        System.out.println("Rooms waiting for cleaning: " + application.getHousekeepingBoard().getRoomNumbersWaitingForCleaning());
        System.out.println("Daily revenue: " + application.getHotel().accept(new TotalDailyRevenueCalculatingVisitor()));
        System.out.println("Occupancy: " + application.getHotel().accept(new OccupiedUnitsCountingVisitor()));
        System.out.println("Hotel as JSON: " + application.getHotel().accept(new HotelStructureJsonExportVisitor()));
    }
}
