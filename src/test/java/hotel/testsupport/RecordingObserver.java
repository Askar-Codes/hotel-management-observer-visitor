package hotel.testsupport;

import hotel.observer.Event;
import hotel.observer.Observer;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/** A listener for tests. It just remembers every event it receives. Safe to use from many threads. */
public class RecordingObserver implements Observer<Event> {

    private final Queue<Event> receivedEvents = new ConcurrentLinkedQueue<>();

    @Override
    public void handleEvent(Event event) {
        receivedEvents.add(event);
    }

    public List<Event> getReceivedEvents() {
        return List.copyOf(receivedEvents);
    }

    public int countReceivedEvents() {
        return receivedEvents.size();
    }
}
