package hotel.visitors;

import hotel.tree.BookableUnit;
import hotel.tree.Floor;
import hotel.tree.Hall;
import hotel.tree.Hotel;
import hotel.tree.HotelVisitor;
import hotel.tree.ParkingLot;
import hotel.tree.Restaurant;
import hotel.tree.Room;
import hotel.tree.Suite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

/**
 * Visitor 3 (filter): returns the rooms and suites that satisfy a condition, in tree order.
 * Examples: "needs cleaning", "has room number 101".
 * Halls and restaurants are never returned. Use ONE new visitor for ONE walk.
 */
public class BookableUnitsFilteringVisitor implements HotelVisitor<List<BookableUnit>> {

    private final Predicate<BookableUnit> condition;
    private final List<BookableUnit> matchingUnits = new ArrayList<>();

    public BookableUnitsFilteringVisitor(Predicate<BookableUnit> condition) {
        this.condition = condition;
    }

    @Override
    public List<BookableUnit> visitHotel(Hotel hotel) {
        visitEachChildAndCollectResults(hotel.getFloors());
        return unitsFoundSoFar();
    }

    @Override
    public List<BookableUnit> visitFloor(Floor floor) {
        visitEachChildAndCollectResults(floor.getUnits());
        return unitsFoundSoFar();
    }

    @Override
    public List<BookableUnit> visitRoom(Room room) {
        addIfConditionIsTrue(room);
        return unitsFoundSoFar();
    }

    @Override
    public List<BookableUnit> visitSuite(Suite suite) {
        addIfConditionIsTrue(suite);
        return unitsFoundSoFar();
    }

    @Override
    public List<BookableUnit> visitHall(Hall hall) {
        return unitsFoundSoFar();
    }

    @Override
    public List<BookableUnit> visitRestaurant(Restaurant restaurant) {
        return unitsFoundSoFar();
    }

    @Override
    public List<BookableUnit> visitParkingLot(ParkingLot parkingLot) {
        return unitsFoundSoFar();
    }

    private void addIfConditionIsTrue(BookableUnit unit) {
        if (condition.test(unit)) {
            matchingUnits.add(unit);
        }
    }

    private List<BookableUnit> unitsFoundSoFar() {
        return Collections.unmodifiableList(matchingUnits);
    }
}
