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

import java.util.List;

/**
 * Visitor 4 (export): writes the whole tree as JSON by hand, without any library.
 * Every container builds its text from the texts of its children, so the nesting is always correct:
 * {"type":"hotel", ..., "floors":[ {"type":"floor", ..., "units":[ {"type":"room", ...}, ... ]} ]}
 */
public class HotelStructureJsonExportVisitor implements HotelVisitor<String> {

    @Override
    public String visitHotel(Hotel hotel) {
        List<String> floorsAsJson = visitEachChildAndCollectResults(hotel.getFloors());
        return "{\"type\":\"hotel\",\"name\":" + quote(hotel.getName()) + ",\"floors\":" + asJsonArray(floorsAsJson) + "}";
    }

    @Override
    public String visitFloor(Floor floor) {
        List<String> unitsAsJson = visitEachChildAndCollectResults(floor.getUnits());
        return "{\"type\":\"floor\",\"name\":" + quote(floor.getName()) + ",\"units\":" + asJsonArray(unitsAsJson) + "}";
    }

    @Override
    public String visitRoom(Room room) {
        return "{\"type\":\"room\"," + bookableUnitFields(room) + "}";
    }

    @Override
    public String visitSuite(Suite suite) {
        return "{\"type\":\"suite\"," + bookableUnitFields(suite) + ",\"bedroomCount\":" + suite.getBedroomCount() + "}";
    }

    @Override
    public String visitHall(Hall hall) {
        return "{\"type\":\"hall\",\"name\":" + quote(hall.getName())
                + ",\"capacity\":" + hall.getCapacity()
                + ",\"dailyRentalPrice\":" + hall.getDailyRentalPrice() + "}";
    }

    @Override
    public String visitRestaurant(Restaurant restaurant) {
        return "{\"type\":\"restaurant\",\"name\":" + quote(restaurant.getName())
                + ",\"seats\":" + restaurant.getSeats()
                + ",\"dailyRevenue\":" + restaurant.getDailyRevenue() + "}";
    }

    @Override
    public String visitParkingLot(ParkingLot parkingLot) {
        return "{\"type\":\"parkingLot\",\"name\":" + quote(parkingLot.getName())
                + ",\"parkingSpots\":" + parkingLot.getParkingSpots()
                + ",\"dailyRevenue\":" + parkingLot.getDailyRevenue() + "}";
    }

    private String bookableUnitFields(BookableUnit unit) {
        return "\"roomNumber\":" + quote(unit.getRoomNumber())
                + ",\"nightlyRate\":" + unit.getNightlyRate()
                + ",\"status\":" + quote(unit.getStatus().name());
    }

    private String asJsonArray(List<String> elementsAsJson) {
        return "[" + String.join(",", elementsAsJson) + "]";
    }

    private String quote(String text) {
        String withEscapedBackslashes = text.replace("\\", "\\\\");
        String withEscapedQuotes = withEscapedBackslashes.replace("\"", "\\\"");
        return "\"" + withEscapedQuotes + "\"";
    }
}
