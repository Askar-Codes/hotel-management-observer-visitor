package hotel.visitors;

import hotel.testsupport.KnownHotelFactory;
import hotel.testsupport.RoomNumbers;
import hotel.tree.BookableUnit;
import hotel.tree.Hotel;
import hotel.tree.RoomStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BookableUnitsFilteringVisitorTest {

    private Hotel hotel;

    @BeforeEach
    void createHotelWithHandCalculatedNumbers() {
        hotel = KnownHotelFactory.createHotelWithKnownStatuses();
    }

    @Test
    void findsEveryRoomAndSuiteThatNeedsCleaningInTheWholeHotel() {
        List<BookableUnit> found = hotel.accept(
                new BookableUnitsFilteringVisitor(unit -> unit.getStatus() == RoomStatus.NEEDS_CLEANING));

        assertEquals(List.of("202", "203"), RoomNumbers.of(found));
    }

    @Test
    void whenStartedOnOneFloorItLooksOnlyAtThatFloor() {
        BookableUnitsFilteringVisitor occupiedUnits = new BookableUnitsFilteringVisitor(
                unit -> unit.getStatus() == RoomStatus.OCCUPIED);
        List<BookableUnit> onFirstFloor = hotel.getFloors().get(0).accept(occupiedUnits);

        BookableUnitsFilteringVisitor cleaningUnits = new BookableUnitsFilteringVisitor(
                unit -> unit.getStatus() == RoomStatus.NEEDS_CLEANING);
        List<BookableUnit> onFirstFloorNeedingCleaning = hotel.getFloors().get(0).accept(cleaningUnits);

        assertEquals(List.of("101"), RoomNumbers.of(onFirstFloor));
        assertEquals(0, onFirstFloorNeedingCleaning.size());
    }

    @Test
    void hallsAndRestaurantsAreNeverReturnedEvenWhenTheConditionIsAlwaysTrue() {
        List<BookableUnit> everything = hotel.accept(new BookableUnitsFilteringVisitor(unit -> true));

        assertEquals(List.of("101", "102", "201", "202", "203"), RoomNumbers.of(everything));
    }
}
