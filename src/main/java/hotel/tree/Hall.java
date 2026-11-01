package hotel.tree;

/** Leaf 3: a conference or banquet hall that is rented out for a day. */
public final class Hall implements HotelElement {

    private final String name;
    private final int capacity;
    private final int dailyRentalPrice;

    public Hall(String name, int capacity, int dailyRentalPrice) {
        this.name = name;
        this.capacity = capacity;
        this.dailyRentalPrice = dailyRentalPrice;
    }

    public String getName() {
        return name;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getDailyRentalPrice() {
        return dailyRentalPrice;
    }

    @Override
    public <R> R accept(HotelVisitor<R> visitor) {
        return visitor.visitHall(this);
    }
}
