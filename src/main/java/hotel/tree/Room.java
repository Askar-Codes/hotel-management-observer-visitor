package hotel.tree;

/** Leaf 1: an ordinary hotel room. */
public final class Room extends BookableUnit {

    public Room(String roomNumber, int nightlyRate) {
        super(roomNumber, nightlyRate);
    }

    @Override
    public <R> R accept(HotelVisitor<R> visitor) {
        return visitor.visitRoom(this);
    }
}
