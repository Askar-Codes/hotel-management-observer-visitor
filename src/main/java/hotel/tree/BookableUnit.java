package hotel.tree;

/**
 * Data shared by everything a guest can sleep in (Room and Suite).
 * The status is the only changing value, so it is volatile: a change made by one thread is seen by all others.
 */
public abstract class BookableUnit implements HotelElement {

    private final String roomNumber;
    private final int nightlyRate;
    private volatile RoomStatus status = RoomStatus.VACANT;

    protected BookableUnit(String roomNumber, int nightlyRate) {
        this.roomNumber = roomNumber;
        this.nightlyRate = nightlyRate;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public int getNightlyRate() {
        return nightlyRate;
    }

    public RoomStatus getStatus() {
        return status;
    }

    public void setStatus(RoomStatus status) {
        this.status = status;
    }
}
