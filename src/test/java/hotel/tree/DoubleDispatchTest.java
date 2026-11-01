package hotel.tree;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Double dispatch: the caller only knows "a HotelElement" and "a HotelVisitor".
 *  1st dispatch: element.accept(visitor) picks the element's own accept method (virtual call on the element).
 *  2nd dispatch: inside accept the element calls visitor.visitXxx(this) with its own exact type.
 * So the right visit method is chosen by the types of BOTH objects.
 */
class DoubleDispatchTest {

    @Test
    void everyElementCallsTheVisitMethodMadeForItsOwnClass() {
        HotelVisitor<String> visitor = new VisitMethodNameReportingVisitor();
        List<HotelElement> elements = List.of(
                new Hotel("H", List.of()),
                new Floor("F", List.of()),
                new Room("1", 10),
                new Suite("2", 20, 2),
                new Hall("Hall", 5, 5),
                new Restaurant("Cafe", 5, 5),
                new ParkingLot("Garage", 5, 5));

        List<String> calledMethods = elements.stream().map(element -> element.accept(visitor)).toList();

        assertEquals(List.of("visitHotel", "visitFloor", "visitRoom", "visitSuite", "visitHall", "visitRestaurant", "visitParkingLot"),
                calledMethods);
    }

    private static class VisitMethodNameReportingVisitor implements HotelVisitor<String> {

        @Override
        public String visitHotel(Hotel hotel) {
            return "visitHotel";
        }

        @Override
        public String visitFloor(Floor floor) {
            return "visitFloor";
        }

        @Override
        public String visitRoom(Room room) {
            return "visitRoom";
        }

        @Override
        public String visitSuite(Suite suite) {
            return "visitSuite";
        }

        @Override
        public String visitHall(Hall hall) {
            return "visitHall";
        }

        @Override
        public String visitRestaurant(Restaurant restaurant) {
            return "visitRestaurant";
        }

        @Override
        public String visitParkingLot(ParkingLot parkingLot) {
            return "visitParkingLot";
        }
    }
}
