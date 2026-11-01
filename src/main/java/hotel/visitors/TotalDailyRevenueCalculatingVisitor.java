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

import java.util.List;

/**
 * Visitor 1 (numeric calculation): how much money the hotel earns per day.
 * A room or suite earns its nightly rate only when a guest is in it.
 * A hall earns its rental price and a restaurant earns its daily revenue.
 */
public class TotalDailyRevenueCalculatingVisitor implements HotelVisitor<Integer> {

    @Override
    public Integer visitHotel(Hotel hotel) {
        return sumOf(visitEachChildAndCollectResults(hotel.getFloors()));
    }

    @Override
    public Integer visitFloor(Floor floor) {
        return sumOf(visitEachChildAndCollectResults(floor.getUnits()));
    }

    @Override
    public Integer visitRoom(Room room) {
        return revenueOfBookableUnit(room);
    }

    @Override
    public Integer visitSuite(Suite suite) {
        return revenueOfBookableUnit(suite);
    }

    @Override
    public Integer visitHall(Hall hall) {
        return hall.getDailyRentalPrice();
    }

    @Override
    public Integer visitRestaurant(Restaurant restaurant) {
        return restaurant.getDailyRevenue();
    }

    @Override
    public Integer visitParkingLot(ParkingLot parkingLot) {
        return parkingLot.getDailyRevenue();
    }

    private int revenueOfBookableUnit(BookableUnit unit) {
        return unit.getStatus() == RoomStatus.OCCUPIED ? unit.getNightlyRate() : 0;
    }

    private int sumOf(List<Integer> revenues) {
        return revenues.stream().mapToInt(Integer::intValue).sum();
    }
}
