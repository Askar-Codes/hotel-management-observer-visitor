package hotel.observer;

/**
 * Something that publishes events. Observers choose which event type they want.
 */
public interface Subject<E extends Event> {

    void subscribe(EventType eventType, Observer<E> observer);

    void unsubscribe(EventType eventType, Observer<E> observer);
}
