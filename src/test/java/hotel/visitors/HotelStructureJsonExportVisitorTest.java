package hotel.visitors;

import hotel.testsupport.KnownHotelFactory;
import hotel.tree.Hotel;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HotelStructureJsonExportVisitorTest {

    @Test
    void smallHotelIsExportedAsExactlyTheExpectedJson() {
        Hotel hotel = KnownHotelFactory.createSmallHotelWithOneFloorAndAllFourLeafTypes();

        String json = hotel.accept(new HotelStructureJsonExportVisitor());

        String expectedJson = """
                {"type":"hotel","name":"Mini Hotel","floors":[\
                {"type":"floor","name":"Floor A","units":[\
                {"type":"room","roomNumber":"1","nightlyRate":50,"status":"VACANT"},\
                {"type":"suite","roomNumber":"2","nightlyRate":90,"status":"OCCUPIED","bedroomCount":2},\
                {"type":"hall","name":"Hall","capacity":10,"dailyRentalPrice":5},\
                {"type":"restaurant","name":"Cafe","seats":8,"dailyRevenue":20}\
                ]}]}""";
        assertEquals(expectedJson, json);
    }

    @Test
    void quotesInsideNamesAreEscapedSoTheJsonStaysValid() {
        Hotel hotel = new Hotel("Grand \"Best\" Hotel", List.of());

        String json = hotel.accept(new HotelStructureJsonExportVisitor());

        assertEquals("""
                {"type":"hotel","name":"Grand \\"Best\\" Hotel","floors":[]}""", json);
    }

    @Test
    void bracesAndBracketsAreBalancedForTheBiggerHotel() {
        Hotel hotel = KnownHotelFactory.createHotelWithKnownStatuses();

        String json = hotel.accept(new HotelStructureJsonExportVisitor());

        assertEquals(countOf(json, '{'), countOf(json, '}'));
        assertEquals(countOf(json, '['), countOf(json, ']'));
        assertEquals(1 + 2 + 7, countOf(json, '{')); // 1 hotel + 2 floors + 7 leaves
        assertTrue(json.startsWith("{\"type\":\"hotel\"") && json.endsWith("]}"));
    }

    private int countOf(String text, char symbol) {
        return (int) text.chars().filter(character -> character == symbol).count();
    }
}
