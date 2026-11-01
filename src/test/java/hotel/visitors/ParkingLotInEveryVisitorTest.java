package hotel.visitors;

import hotel.tree.BookableUnit;
import hotel.tree.Floor;
import hotel.tree.Hotel;
import hotel.tree.ParkingLot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** A hotel that consists only of one parking lot: 50 spots, 400 revenue per day. */
class ParkingLotInEveryVisitorTest {

    private Hotel hotel;

    @BeforeEach
    void createHotelWithOnlyAParkingLot() {
        Floor basement = new Floor("Basement", List.of(new ParkingLot("Garage", 50, 400)));
        hotel = new Hotel("Parking Hotel", List.of(basement));
    }

    @Test
    void parkingLotEarnsItsDailyRevenue() {
        int revenue = hotel.accept(new TotalDailyRevenueCalculatingVisitor());
        assertEquals(400, revenue);
    }

    @Test
    void parkingLotIsNotARoomSoItIsNeitherCountedNorFound() {
        assertEquals(new OccupancyStatistics(0, 0), hotel.accept(new OccupiedUnitsCountingVisitor()));
        List<BookableUnit> found = hotel.accept(new BookableUnitsFilteringVisitor(unit -> true));
        assertEquals(0, found.size());
    }

    @Test
    void parkingLotIsNotAPublicAreaForPeople() {
        int capacity = hotel.accept(new PublicAreasTotalCapacityCalculatingVisitor());
        assertEquals(0, capacity);
    }

    @Test
    void parkingLotIsExportedToJson() {
        String expectedJson = """
                {"type":"hotel","name":"Parking Hotel","floors":[\
                {"type":"floor","name":"Basement","units":[\
                {"type":"parkingLot","name":"Garage","parkingSpots":50,"dailyRevenue":400}\
                ]}]}""";

        assertEquals(expectedJson, hotel.accept(new HotelStructureJsonExportVisitor()));
    }
}
