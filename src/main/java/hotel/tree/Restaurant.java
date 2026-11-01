package hotel.tree;

/** Leaf 4: the hotel restaurant. It earns money but has no guests sleeping in it. */
public final class Restaurant implements HotelElement {

    private final String name;
    private final int seats;
    private final int dailyRevenue;

    public Restaurant(String name, int seats, int dailyRevenue) {
        this.name = name;
        this.seats = seats;
        this.dailyRevenue = dailyRevenue;
    }

    public String getName() {
        return name;
    }

    public int getSeats() {
        return seats;
    }

    public int getDailyRevenue() {
        return dailyRevenue;
    }

    @Override
    public <R> R accept(HotelVisitor<R> visitor) {
        return visitor.visitRestaurant(this);
    }
}
