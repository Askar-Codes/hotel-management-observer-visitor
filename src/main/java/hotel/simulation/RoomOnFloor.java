package hotel.simulation;

/** A room number together with the name of the floor it is on. Producers use it to create valid events. */
public record RoomOnFloor(String roomNumber, String floorName) {
}
