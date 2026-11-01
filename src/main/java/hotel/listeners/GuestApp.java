package hotel.listeners;

import hotel.events.GuestCheckedIn;
import hotel.events.GuestCheckedOut;
import hotel.observer.Event;
import hotel.observer.EventType;
import hotel.observer.Observer;
import hotel.observer.Subject;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/** Listener 4: the mobile app of the guests. It sends a welcome message and a goodbye message. */
public class GuestApp implements Observer<Event> {

    private final Queue<String> sentMessages = new ConcurrentLinkedQueue<>();

    public void startListeningTo(Subject<Event> source) {
        source.subscribe(EventType.GUEST_CHECKED_IN, this);
        source.subscribe(EventType.GUEST_CHECKED_OUT, this);
    }

    @Override
    public void handleEvent(Event event) {
        if (event instanceof GuestCheckedIn checkedIn) {
            sentMessages.add("Welcome, " + checkedIn.guestName() + "! Your room is " + checkedIn.roomNumber() + ".");
        } else if (event instanceof GuestCheckedOut checkedOut) {
            sentMessages.add("Goodbye, " + checkedOut.guestName() + "! Thank you for staying with us.");
        }
    }

    public List<String> getSentMessages() {
        return List.copyOf(sentMessages);
    }
}
