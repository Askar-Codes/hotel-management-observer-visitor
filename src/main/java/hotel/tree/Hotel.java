package hotel.tree;

import java.util.List;

/** Container, level 1 of the tree. Holds floors. */
public final class Hotel implements HotelElement {

    private final String name;
    private final List<Floor> floors;

    public Hotel(String name, List<Floor> floors) {
        this.name = name;
        this.floors = List.copyOf(floors);
    }

    public String getName() {
        return name;
    }

    public List<Floor> getFloors() {
        return floors;
    }

    @Override
    public <R> R accept(HotelVisitor<R> visitor) {
        return visitor.visitHotel(this);
    }
}
