package hotel.visitors;

import hotel.testsupport.KnownHotelFactory;
import hotel.tree.Hotel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PublicAreasTotalCapacityCalculatingVisitorTest {

    private Hotel hotel;

    @BeforeEach
    void createHotelWithHandCalculatedNumbers() {
        hotel = KnownHotelFactory.createHotelWithKnownStatuses();
    }

    @Test
    void wholeHotelHasRoomForTwoHundredFortyPeopleInPublicAreas() {
        // ballroom 200 + restaurant 40 seats
        int capacity = hotel.accept(new PublicAreasTotalCapacityCalculatingVisitor());

        assertEquals(240, capacity);
    }

    @Test
    void eachFloorCountsOnlyItsOwnPublicAreas() {
        int firstFloorCapacity = hotel.getFloors().get(0).accept(new PublicAreasTotalCapacityCalculatingVisitor());
        int secondFloorCapacity = hotel.getFloors().get(1).accept(new PublicAreasTotalCapacityCalculatingVisitor());

        assertEquals(40, firstFloorCapacity);
        assertEquals(200, secondFloorCapacity);
    }
}
