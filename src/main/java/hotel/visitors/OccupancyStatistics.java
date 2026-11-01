package hotel.visitors;

/** Result of the occupancy counting: how many rooms and suites have a guest, out of all rooms and suites. */
public record OccupancyStatistics(int occupiedUnits, int bookableUnits) {
}
