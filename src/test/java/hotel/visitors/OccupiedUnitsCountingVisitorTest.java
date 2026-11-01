package hotel.visitors;

import hotel.testsupport.KnownHotelFactory;
import hotel.tree.Hotel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OccupiedUnitsCountingVisitorTest {

    private Hotel hotel;

    @BeforeEach
    void createHotelWithHandCalculatedNumbers() {
        hotel = KnownHotelFactory.createHotelWithKnownStatuses();
    }

    @Test
    void twoOfFiveRoomsAndSuitesAreOccupiedInTheWholeHotel() {
        OccupancyStatistics statistics = hotel.accept(new OccupiedUnitsCountingVisitor());

        assertEquals(new OccupancyStatistics(2, 5), statistics);
    }

    @Test
    void countingOneFloorDoesNotCountTheOtherFloor() {
        OccupancyStatistics firstFloor = hotel.getFloors().get(0).accept(new OccupiedUnitsCountingVisitor());
        OccupancyStatistics secondFloor = hotel.getFloors().get(1).accept(new OccupiedUnitsCountingVisitor());

        assertEquals(new OccupancyStatistics(1, 2), firstFloor);
        assertEquals(new OccupancyStatistics(1, 3), secondFloor);
    }
}
