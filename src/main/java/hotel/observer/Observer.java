package hotel.observer;

/**
 * Something that wants to be told about events (a listener).
 */
public interface Observer<E extends Event> {

    void handleEvent(E event);
}
