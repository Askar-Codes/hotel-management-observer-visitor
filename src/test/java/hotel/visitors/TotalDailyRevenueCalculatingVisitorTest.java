package hotel.visitors;

import hotel.testsupport.KnownHotelFactory;
import hotel.tree.Hotel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TotalDailyRevenueCalculatingVisitorTest {

    private Hotel hotel;

    @BeforeEach
    void createHotelWithHandCalculatedNumbers() {
        hotel = KnownHotelFactory.createHotelWithKnownStatuses();
    }

    @Test
    void wholeHotelEarnsTwoThousandOneHundred() {
        // 101 occupied (100) + restaurant (700) + suite 201 occupied (300) + hall (1000); the rest earns nothing
        int revenue = hotel.accept(new TotalDailyRevenueCalculatingVisitor());

        assertEquals(2100, revenue);
    }

    @Test
    void eachFloorEarnsOnlyItsOwnMoney() {
        int firstFloorRevenue = hotel.getFloors().get(0).accept(new TotalDailyRevenueCalculatingVisitor());
        int secondFloorRevenue = hotel.getFloors().get(1).accept(new TotalDailyRevenueCalculatingVisitor());

        assertEquals(100 + 700, firstFloorRevenue);
        assertEquals(300 + 1000, secondFloorRevenue);
    }
}
