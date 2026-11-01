package hotel.events;

import hotel.observer.Event;
import hotel.observer.EventType;

public record OccupancyReportPublished(int stayingGuestsCount) implements Event {

    @Override
    public EventType type() {
        return EventType.OCCUPANCY_REPORT_PUBLISHED;
    }
}
