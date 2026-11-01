package hotel.sources;

import hotel.events.GuestCheckedIn;
import hotel.events.GuestCheckedOut;
import hotel.observer.ConcurrentSubject;
import hotel.observer.Event;

/** Source 1: the booking system. It tells everybody when a guest arrives or leaves. */
public class BookingEventSource extends ConcurrentSubject<Event> {

    public void publishGuestCheckedIn(String guestName, String roomNumber, String floorName) {
        publishEventToAllSubscribersOfItsType(new GuestCheckedIn(guestName, roomNumber, floorName));
    }

    public void publishGuestCheckedOut(String guestName, String roomNumber, String floorName) {
        publishEventToAllSubscribersOfItsType(new GuestCheckedOut(guestName, roomNumber, floorName));
    }
}
