package hotel.tree;

/** Leaf 5 (trade-off commit 2): the hotel parking lot. It earns money but has no guests sleeping in it. */
public final class ParkingLot implements HotelElement {

    private final String name;
    private final int parkingSpots;
    private final int dailyRevenue;

    public ParkingLot(String name, int parkingSpots, int dailyRevenue) {
        this.name = name;
        this.parkingSpots = parkingSpots;
        this.dailyRevenue = dailyRevenue;
    }

    public String getName() {
        return name;
    }

    public int getParkingSpots() {
        return parkingSpots;
    }

    public int getDailyRevenue() {
        return dailyRevenue;
    }

    @Override
    public <R> R accept(HotelVisitor<R> visitor) {
        return visitor.visitParkingLot(this);
    }
}
