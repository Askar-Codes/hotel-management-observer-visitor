package hotel.tree;

/**
 * One node of the hotel tree. The only method is the "door" through which a visitor enters (double dispatch).
 */
public interface HotelElement {

    <R> R accept(HotelVisitor<R> visitor);
}
