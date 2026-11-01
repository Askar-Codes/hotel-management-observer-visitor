package hotel.visitors;

import hotel.tree.Floor;
import hotel.tree.Hall;
import hotel.tree.Hotel;
import hotel.tree.HotelVisitor;
import hotel.tree.ParkingLot;
import hotel.tree.Restaurant;
import hotel.tree.Room;
import hotel.tree.Suite;

/**
 * Extra visitor (trade-off commit 1): how many people fit into the public areas of the hotel,
 * that is the capacity of all halls plus the seats of all restaurants.
 * Rooms and suites are private, so they add nothing. Use ONE new visitor for ONE walk.
 */
public class PublicAreasTotalCapacityCalculatingVisitor implements HotelVisitor<Integer> {

    private int capacityFoundSoFar = 0;

    @Override
    public Integer visitHotel(Hotel hotel) {
        visitEachChildAndCollectResults(hotel.getFloors());
        return capacityFoundSoFar;
    }

    @Override
    public Integer visitFloor(Floor floor) {
        visitEachChildAndCollectResults(floor.getUnits());
        return capacityFoundSoFar;
    }

    @Override
    public Integer visitRoom(Room room) {
        return capacityFoundSoFar;
    }

    @Override
    public Integer visitSuite(Suite suite) {
        return capacityFoundSoFar;
    }

    @Override
    public Integer visitHall(Hall hall) {
        capacityFoundSoFar += hall.getCapacity();
        return capacityFoundSoFar;
    }

    @Override
    public Integer visitRestaurant(Restaurant restaurant) {
        capacityFoundSoFar += restaurant.getSeats();
        return capacityFoundSoFar;
    }

    @Override
    public Integer visitParkingLot(ParkingLot parkingLot) {
        return capacityFoundSoFar;
    }
}
