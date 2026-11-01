package hotel.tree;

/** Leaf 2: a suite, a bigger room with several bedrooms. */
public final class Suite extends BookableUnit {

    private final int bedroomCount;

    public Suite(String roomNumber, int nightlyRate, int bedroomCount) {
        super(roomNumber, nightlyRate);
        this.bedroomCount = bedroomCount;
    }

    public int getBedroomCount() {
        return bedroomCount;
    }

    @Override
    public <R> R accept(HotelVisitor<R> visitor) {
        return visitor.visitSuite(this);
    }
}
