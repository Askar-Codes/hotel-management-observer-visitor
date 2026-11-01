package hotel.testsupport;

import hotel.tree.BookableUnit;

import java.util.List;

public final class RoomNumbers {

    private RoomNumbers() {
    }

    public static List<String> of(List<BookableUnit> units) {
        return units.stream().map(BookableUnit::getRoomNumber).toList();
    }
}
