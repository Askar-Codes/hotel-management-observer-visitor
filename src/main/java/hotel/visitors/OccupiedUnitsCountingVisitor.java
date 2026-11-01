package hotel.visitors;

import hotel.tree.BookableUnit;
import hotel.tree.Floor;
import hotel.tree.Hall;
import hotel.tree.Hotel;
import hotel.tree.HotelVisitor;
import hotel.tree.ParkingLot;
import hotel.tree.Restaurant;
import hotel.tree.Room;
import hotel.tree.RoomStatus;
import hotel.tree.Suite;

/**
 * Visitor 2 (accumulates state across the whole tree): counts occupied and total rooms and suites.
 * The two counters live inside the visitor and grow while it walks, so use ONE new visitor for ONE walk.
 * Every visit method returns the totals collected so far; the answer is what the first call returns at the end.
 */
public class OccupiedUnitsCountingVisitor implements HotelVisitor<OccupancyStatistics> {

    private int occupiedUnits = 0;
    private int bookableUnits = 0;

    @Override
    public OccupancyStatistics visitHotel(Hotel hotel) {
        visitEachChildAndCollectResults(hotel.getFloors());
        return statisticsCollectedSoFar();
    }

    @Override
    public OccupancyStatistics visitFloor(Floor floor) {
        visitEachChildAndCollectResults(floor.getUnits());
        return statisticsCollectedSoFar();
    }

    @Override
    public OccupancyStatistics visitRoom(Room room) {
        countBookableUnit(room);
        return statisticsCollectedSoFar();
    }

    @Override
    public OccupancyStatistics visitSuite(Suite suite) {
        countBookableUnit(suite);
        return statisticsCollectedSoFar();
    }

    @Override
    public OccupancyStatistics visitHall(Hall hall) {
        return statisticsCollectedSoFar();
    }

    @Override
    public OccupancyStatistics visitRestaurant(Restaurant restaurant) {
        return statisticsCollectedSoFar();
    }

    @Override
    public OccupancyStatistics visitParkingLot(ParkingLot parkingLot) {
        return statisticsCollectedSoFar();
    }

    private void countBookableUnit(BookableUnit unit) {
        bookableUnits++;
        if (unit.getStatus() == RoomStatus.OCCUPIED) {
            occupiedUnits++;
        }
    }

    private OccupancyStatistics statisticsCollectedSoFar() {
        return new OccupancyStatistics(occupiedUnits, bookableUnits);
    }
}
