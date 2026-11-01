package hotel.tree;

import java.util.List;

/** Container, level 2 of the tree. Holds rooms, suites, halls and restaurants. */
public final class Floor implements HotelElement {

    private final String name;
    private final List<HotelElement> units;

    public Floor(String name, List<HotelElement> units) {
        this.name = name;
        this.units = List.copyOf(units);
    }

    public String getName() {
        return name;
    }

    public List<HotelElement> getUnits() {
        return units;
    }

    @Override
    public <R> R accept(HotelVisitor<R> visitor) {
        return visitor.visitFloor(this);
    }
}
