package hotel.listeners;

import hotel.events.CleaningTaskCreated;
import hotel.observer.Event;
import hotel.observer.EventType;
import hotel.observer.Observer;
import hotel.observer.Subject;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/** Listener 2: the board in the housekeeping office. It shows which rooms are waiting for the cleaners. */
public class HousekeepingBoard implements Observer<Event> {

    private final Set<String> roomNumbersWaitingForCleaning = ConcurrentHashMap.newKeySet();

    public void startListeningTo(Subject<Event> source) {
        source.subscribe(EventType.CLEANING_TASK_CREATED, this);
    }

    @Override
    public void handleEvent(Event event) {
        if (event instanceof CleaningTaskCreated cleaningTask) {
            roomNumbersWaitingForCleaning.add(cleaningTask.roomNumber());
        }
    }

    public Set<String> getRoomNumbersWaitingForCleaning() {
        return Set.copyOf(roomNumbersWaitingForCleaning);
    }
}
