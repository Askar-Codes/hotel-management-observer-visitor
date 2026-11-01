package hotel.simulation;

import hotel.sources.BookingEventSource;
import hotel.sources.RoomStatusEventSource;
import hotel.tree.BookableUnit;
import hotel.tree.Floor;
import hotel.tree.Hotel;
import hotel.tree.RoomStatus;
import hotel.visitors.BookableUnitsFilteringVisitor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Starts several producer threads on an ExecutorService. Each producer plays "stay cycles" in its own room:
 * guest checks in, guest checks out, staff marks the room VACANT again (3 events per cycle).
 *
 * Graceful finish: the runner waits for EVERY producer (Future.get), so no event is lost,
 * then shuts the executor down and waits until its threads are really gone, so no thread hangs.
 */
public class HotelSimulationRunner {

    private static final long SECONDS_TO_WAIT_FOR_THREADS_TO_STOP = 30;

    private final ExecutorService executorService;
    private final BookingEventSource bookingEventSource;
    private final RoomStatusEventSource roomStatusEventSource;
    private final List<RoomOnFloor> roomsThatProducersCanUse;

    public HotelSimulationRunner(ExecutorService executorService,
                                 BookingEventSource bookingEventSource,
                                 RoomStatusEventSource roomStatusEventSource,
                                 Hotel hotel) {
        this.executorService = executorService;
        this.bookingEventSource = bookingEventSource;
        this.roomStatusEventSource = roomStatusEventSource;
        this.roomsThatProducersCanUse = listEveryBookableRoomTogetherWithItsFloor(hotel);
    }

    public void publishStayCyclesFromParallelProducers(int producerCount, int stayCyclesPerProducer) {
        List<Future<?>> producers = submitAllProducers(producerCount, stayCyclesPerProducer);
        for (Future<?> producer : producers) {
            waitUntilProducerIsFinished(producer);
        }
        shutDownExecutorAndWaitUntilItsThreadsStop();
    }

    private List<Future<?>> submitAllProducers(int producerCount, int stayCyclesPerProducer) {
        List<Future<?>> producers = new ArrayList<>();
        for (int producerNumber = 1; producerNumber <= producerCount; producerNumber++) {
            RoomOnFloor roomOfThisProducer = roomsThatProducersCanUse.get(producerNumber % roomsThatProducersCanUse.size());
            String guestNamePrefix = "Guest-" + producerNumber + "-";
            producers.add(executorService.submit(
                    () -> publishStayCycles(roomOfThisProducer, guestNamePrefix, stayCyclesPerProducer)));
        }
        return producers;
    }

    private void publishStayCycles(RoomOnFloor room, String guestNamePrefix, int stayCycles) {
        for (int cycleNumber = 1; cycleNumber <= stayCycles; cycleNumber++) {
            publishOneStayCycle(room, guestNamePrefix + cycleNumber);
        }
    }

    private void publishOneStayCycle(RoomOnFloor room, String guestName) {
        bookingEventSource.publishGuestCheckedIn(guestName, room.roomNumber(), room.floorName());
        bookingEventSource.publishGuestCheckedOut(guestName, room.roomNumber(), room.floorName());
        roomStatusEventSource.publishRoomStatusChanged(room.roomNumber(), RoomStatus.VACANT);
    }

    private void waitUntilProducerIsFinished(Future<?> producer) {
        try {
            producer.get();
        } catch (InterruptedException interrupted) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while waiting for a producer", interrupted);
        } catch (ExecutionException producerFailed) {
            throw new IllegalStateException("A producer thread failed", producerFailed.getCause());
        }
    }

    private void shutDownExecutorAndWaitUntilItsThreadsStop() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(SECONDS_TO_WAIT_FOR_THREADS_TO_STOP, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
                throw new IllegalStateException("Producer threads did not stop in time");
            }
        } catch (InterruptedException interrupted) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while waiting for threads to stop", interrupted);
        }
    }

    private static List<RoomOnFloor> listEveryBookableRoomTogetherWithItsFloor(Hotel hotel) {
        List<RoomOnFloor> rooms = new ArrayList<>();
        for (Floor floor : hotel.getFloors()) {
            List<BookableUnit> roomsOnThisFloor = floor.accept(new BookableUnitsFilteringVisitor(unit -> true));
            for (BookableUnit room : roomsOnThisFloor) {
                rooms.add(new RoomOnFloor(room.getRoomNumber(), floor.getName()));
            }
        }
        if (rooms.isEmpty()) {
            throw new IllegalArgumentException("The hotel has no rooms, so producers have nothing to do");
        }
        return rooms;
    }
}
