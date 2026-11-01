package hotel.listeners;

import hotel.events.GuestCheckedIn;
import hotel.events.GuestCheckedOut;
import hotel.events.OccupancyReportPublished;
import hotel.observer.ConcurrentSubject;
import hotel.observer.Event;
import hotel.observer.EventType;
import hotel.observer.Observer;
import hotel.observer.Subject;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Listener 3 AND subject (the chain of observers).
 * It listens to check-ins and check-outs, counts the guests who are staying right now,
 * and republishes that number as a new OccupancyReportPublished event for its own subscribers.
 */
public class RevenueManager extends ConcurrentSubject<Event> implements Observer<Event> {

    private final AtomicInteger stayingGuestsCount = new AtomicInteger();

    public void startListeningTo(Subject<Event> source) {
        source.subscribe(EventType.GUEST_CHECKED_IN, this);
        source.subscribe(EventType.GUEST_CHECKED_OUT, this);
    }

    @Override
    public void handleEvent(Event event) {
        if (event instanceof GuestCheckedIn) {
            publishReportAbout(stayingGuestsCount.incrementAndGet());
        } else if (event instanceof GuestCheckedOut) {
            publishReportAbout(stayingGuestsCount.decrementAndGet());
        }
    }

    public int getStayingGuestsCount() {
        return stayingGuestsCount.get();
    }

    private void publishReportAbout(int currentStayingGuestsCount) {
        publishEventToAllSubscribersOfItsType(new OccupancyReportPublished(currentStayingGuestsCount));
    }
}
