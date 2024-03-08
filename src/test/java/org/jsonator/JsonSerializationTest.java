package org.jsonator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class JsonSerializationTest {
    @Test
    public void serialize_primitives_shouldReturnJson() {
        Assertions.assertAll(
                "Grouped Assertions of Primitives",
                () -> assertEquals("0", JsonSerializer.serialize(0)),
                () -> assertEquals("1", JsonSerializer.serialize((short) 1)),
                () -> assertEquals("2", JsonSerializer.serialize(2L)),
                () -> assertEquals("3.0", JsonSerializer.serialize(3.0f)),
                () -> assertEquals("4.0", JsonSerializer.serialize(4.0D)),
                () -> assertEquals("-1", JsonSerializer.serialize(-1)),
                () -> assertEquals("-2", JsonSerializer.serialize((short) -2)),
                () -> assertEquals("-3", JsonSerializer.serialize(-3L)),
                () -> assertEquals("-4.0", JsonSerializer.serialize(-4.0f)),
                () -> assertEquals("-5.0", JsonSerializer.serialize(-5.0D)),
                () -> assertEquals("true", JsonSerializer.serialize(true)),
                () -> assertEquals("false", JsonSerializer.serialize(false)),
                () -> assertEquals("\"a\"", JsonSerializer.serialize('a')),
                () -> assertEquals("\"'\"", JsonSerializer.serialize('\'')),
                () -> assertEquals("8", JsonSerializer.serialize((byte) 8)));
    }

    @Test
    public void serialize_String_shouldReturnJson() {
        Assertions.assertAll(
                "Grouped Assertions of Strings",
                () -> assertEquals("\"json\"", JsonSerializer.serialize("json")),
                () -> assertEquals("\"String with spaces\"", JsonSerializer.serialize("String with spaces")),
                () -> assertEquals("\"String with '\"", JsonSerializer.serialize("String with '")));
    }

    @Test
    public void serialize_LocalTime_shouldReturnJson() {
        LocalTime time = LocalTime.now();
        Assertions.assertEquals('"' + time.toString() + '"', JsonSerializer.serialize(time));
    }

    @Test
    public void serialize_LocalDate_shouldReturnJson() {
        LocalDate date = LocalDate.now();
        Assertions.assertEquals('"' + date.toString() + '"', JsonSerializer.serialize(date));
    }

    @Test
    public void serialize_LocalDateTime_shouldReturnJson() {
        LocalDateTime dateTime = LocalDateTime.now();
        Assertions.assertEquals('"' + dateTime.toString() + '"', JsonSerializer.serialize(dateTime));
    }

    @Test
    public void serialize_UUID_shouldReturnJson() {
        UUID uuid = UUID.randomUUID();
        Assertions.assertEquals('"' + uuid.toString() + '"', JsonSerializer.serialize(uuid));
    }

    @Test
    public void serialize_Array_shouldReturnJson() {
        Integer[] ints = { 1, 2, 3 };

        String expected = "[1,2,3]";
        String actual = JsonSerializer.serialize(ints);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void serialize_Collection_shouldReturnJson() {
        List<Integer> ints = new ArrayList<>();
        Collections.addAll(ints, 1, 2, 3);

        String expected = "[1,2,3]";
        String actual = JsonSerializer.serialize(ints);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void serialize_map_shouldReturnJson() {
        Map<String, Integer> map = new HashMap<>() {
            {
                put("one", 1);
                put("two", 2);
                put("three", 3);
            }
        };

        String expected = "{\"one\":1,\"two\":2,\"three\":3}";
        String actual = JsonSerializer.serialize(map);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void serialize_Enum_shouldReturnJson() {
        enum TestEnum {
            OPTION_1,
            OPTION_2,
            OPTION_3,
        }

        TestEnum src = TestEnum.OPTION_1;
        Assertions.assertEquals(((Integer) src.ordinal()).toString(), JsonSerializer.serialize(src));
    }

    @Test
    public void serialize_classInstanceWithDefaultCtor_shouldReturnJson() {
        TestClassDefaultCtor obj = new TestClassDefaultCtor();
        obj.number = 1;

        String expected = "{\"number\":1}";
        String actual = JsonSerializer.serialize(obj);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void serialize_nestedClassInstance_shouldReturnJson() {
        TestNestedClass obj = new TestNestedClass();
        obj.nestedClass.number = 1;

        String expected = "{\"nestedClass\":{\"number\":1}}";
        String actual = JsonSerializer.serialize(obj);

        Assertions.assertEquals(expected, actual);
    }
}
