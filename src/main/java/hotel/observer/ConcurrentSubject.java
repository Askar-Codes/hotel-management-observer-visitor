package hotel.observer;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Thread-safe subject. There is no lock on the subject itself.
 *
 * The registry is a fixed map with one CopyOnWriteArrayList per event type.
 * The map is filled once in the constructor and never changes afterwards, so it is safe to read from any thread.
 * Only the lists change. A CopyOnWriteArrayList iterates over a snapshot, which gives us:
 *  - an observer may subscribe or unsubscribe from any thread, even from inside its own handler;
 *  - a new subscriber does not see the event that is being delivered right now, only later events;
 *  - observers are called in the order in which they subscribed.
 */
public class ConcurrentSubject<E extends Event> implements Subject<E> {

    private final Map<EventType, List<Observer<E>>> observersByEventType = createEmptyObserverListForEveryEventType();

    @Override
    public void subscribe(EventType eventType, Observer<E> observer) {
        observersSubscribedTo(eventType).add(observer);
    }

    @Override
    public void unsubscribe(EventType eventType, Observer<E> observer) {
        observersSubscribedTo(eventType).remove(observer);
    }

    public void publishEventToAllSubscribersOfItsType(E event) {
        for (Observer<E> observer : observersSubscribedTo(event.type())) {
            observer.handleEvent(event);
        }
    }

    private List<Observer<E>> observersSubscribedTo(EventType eventType) {
        return observersByEventType.get(eventType);
    }

    private static <E extends Event> Map<EventType, List<Observer<E>>> createEmptyObserverListForEveryEventType() {
        Map<EventType, List<Observer<E>>> observers = new EnumMap<>(EventType.class);
        for (EventType eventType : EventType.values()) {
            observers.put(eventType, new CopyOnWriteArrayList<>());
        }
        return Collections.unmodifiableMap(observers);
    }
}
