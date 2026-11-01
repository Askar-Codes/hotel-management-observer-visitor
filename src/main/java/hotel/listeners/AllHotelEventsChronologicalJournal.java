package hotel.listeners;

import hotel.observer.Event;
import hotel.observer.EventType;
import hotel.observer.Observer;
import hotel.observer.Subject;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Listener 5: writes down EVERY event of EVERY type, in the order in which the events arrive.
 * It can listen to many sources at once.
 */
public class AllHotelEventsChronologicalJournal implements Observer<Event> {

    private final Queue<Event> recordedEventsInArrivalOrder = new ConcurrentLinkedQueue<>();

    public void startRecordingEventsOf(Subject<Event> source) {
        for (EventType eventType : EventType.values()) {
            source.subscribe(eventType, this);
        }
    }

    @Override
    public void handleEvent(Event event) {
        recordedEventsInArrivalOrder.add(event);
    }

    public List<Event> getRecordedEvents() {
        return List.copyOf(recordedEventsInArrivalOrder);
    }

    public long countRecordedEventsOfType(EventType eventType) {
        return recordedEventsInArrivalOrder.stream().filter(event -> event.type() == eventType).count();
    }
}
