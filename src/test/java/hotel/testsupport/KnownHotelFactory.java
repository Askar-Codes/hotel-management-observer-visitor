package hotel.testsupport;

import hotel.tree.Floor;
import hotel.tree.Hall;
import hotel.tree.Hotel;
import hotel.tree.Restaurant;
import hotel.tree.Room;
import hotel.tree.RoomStatus;
import hotel.tree.Suite;

import java.util.List;

/**
 * Hotels with numbers that were calculated BY HAND, so tests can compare visitor results with exact values.
 *
 *   Floor 1:  Room 101 (rate 100, OCCUPIED)   Room 102 (rate 120, VACANT)   Restaurant (revenue 700)
 *   Floor 2:  Suite 201 (rate 300, OCCUPIED)  Suite 202 (rate 350, NEEDS_CLEANING)
 *             Room 203 (rate 110, NEEDS_CLEANING)   Hall (rental 1000)
 *
 * Revenue: floor 1 = 100 + 700 = 800, floor 2 = 300 + 1000 = 1300, hotel = 2100.
 * Occupied rooms and suites: 2 of 5 (floor 1: 1 of 2, floor 2: 1 of 3).
 */
public final class KnownHotelFactory {

    private KnownHotelFactory() {
    }

    public static Hotel createHotelWithKnownStatuses() {
        Room room101 = new Room("101", 100);
        room101.setStatus(RoomStatus.OCCUPIED);
        Room room102 = new Room("102", 120);
        Suite suite201 = new Suite("201", 300, 2);
        suite201.setStatus(RoomStatus.OCCUPIED);
        Suite suite202 = new Suite("202", 350, 3);
        suite202.setStatus(RoomStatus.NEEDS_CLEANING);
        Room room203 = new Room("203", 110);
        room203.setStatus(RoomStatus.NEEDS_CLEANING);

        Floor firstFloor = new Floor("Floor 1", List.of(room101, room102, new Restaurant("Sunrise Restaurant", 40, 700)));
        Floor secondFloor = new Floor("Floor 2", List.of(suite201, suite202, room203, new Hall("Ballroom", 200, 1000)));
        return new Hotel("Known Hotel", List.of(firstFloor, secondFloor));
    }

    public static Hotel createSmallHotelWithOneFloorAndAllFourLeafTypes() {
        Suite suite = new Suite("2", 90, 2);
        suite.setStatus(RoomStatus.OCCUPIED);
        Floor floor = new Floor("Floor A", List.of(
                new Room("1", 50),
                suite,
                new Hall("Hall", 10, 5),
                new Restaurant("Cafe", 8, 20)));
        return new Hotel("Mini Hotel", List.of(floor));
    }
}
