package hotel.tree;

import java.util.ArrayList;
import java.util.List;

/**
 * An operation over the hotel tree. R is the type of the result.
 * There is one visit method for every element class (this is what makes double dispatch work).
 * The visitor itself walks down the tree, because it decides what to do before and after the children.
 */
public interface HotelVisitor<R> {

    R visitHotel(Hotel hotel);

    R visitFloor(Floor floor);

    R visitRoom(Room room);

    R visitSuite(Suite suite);

    R visitHall(Hall hall);

    R visitRestaurant(Restaurant restaurant);

    R visitParkingLot(ParkingLot parkingLot);

    default List<R> visitEachChildAndCollectResults(List<? extends HotelElement> children) {
        List<R> resultsOfChildren = new ArrayList<>();
        for (HotelElement child : children) {
            resultsOfChildren.add(child.accept(this));
        }
        return resultsOfChildren;
    }
}
