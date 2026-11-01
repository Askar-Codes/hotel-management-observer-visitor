package hotel;

import hotel.tree.Floor;
import hotel.tree.Hall;
import hotel.tree.Hotel;
import hotel.tree.Restaurant;
import hotel.tree.Room;
import hotel.tree.Suite;

import java.util.List;

/** Builds the sample hotel used by the demo: 2 floors, 3 rooms, 2 suites, 1 hall and 1 restaurant. */
public final class GrandHotelFactory {

    private GrandHotelFactory() {
    }

    public static Hotel createGrandHotel() {
        Floor firstFloor = new Floor("Floor 1", List.of(
                new Room("101", 100),
                new Room("102", 120),
                new Restaurant("Sunrise Restaurant", 40, 700)));
        Floor secondFloor = new Floor("Floor 2", List.of(
                new Suite("201", 300, 2),
                new Suite("202", 350, 3),
                new Room("203", 110),
                new Hall("Ballroom", 200, 1000)));
        return new Hotel("Grand Hotel", List.of(firstFloor, secondFloor));
    }
}
