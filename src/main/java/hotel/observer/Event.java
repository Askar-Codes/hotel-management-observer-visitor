package hotel.observer;

/**
 * An event is an immutable fact that already happened. It always knows its own type.
 */
public interface Event {

    EventType type();
}
