package org.jsonator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class JsonDeserializationTest {
    @Test
    public void deserialize_primitives_shouldReturnJson() {
        Assertions.assertAll(
                "Grouped Assertions of Primitives",
                () -> assertEquals(0, JsonSerializer.deserialize("0", int.class)),
                () -> assertEquals((short) 1, JsonSerializer.deserialize("1", short.class)),
                () -> assertEquals(2, JsonSerializer.deserialize("2", long.class)),
                () -> assertEquals(3.0f, JsonSerializer.deserialize("3.0", float.class)),
                () -> assertEquals(4.0f, JsonSerializer.deserialize("4.0", double.class)),
                () -> assertEquals(-1, JsonSerializer.deserialize("-1", int.class)),
                () -> assertEquals((short) -2, JsonSerializer.deserialize("-2", short.class)),
                () -> assertEquals(-3, JsonSerializer.deserialize("-3", long.class)),
                () -> assertEquals(-4.0f, JsonSerializer.deserialize("-4.0", float.class)),
                () -> assertEquals(-5.0D, JsonSerializer.deserialize("-5.0", double.class)),
                () -> assertEquals(true, JsonSerializer.deserialize("true", boolean.class)),
                () -> assertEquals(false, JsonSerializer.deserialize("false", boolean.class)),
                () -> assertEquals('a', JsonSerializer.deserialize("\"a\"", char.class)),
                () -> assertEquals('\'', JsonSerializer.deserialize("\"'\"", char.class)),
                () -> assertEquals((byte) 8, JsonSerializer.deserialize("8", byte.class)));
    }

    @Test
    public void deserialize_String_shouldReturnJson() {
        Assertions.assertAll(
                "Grouped Assertions of Strings",
                () -> assertEquals("json", JsonSerializer.deserialize("\"json\"", String.class)),
                () -> assertEquals("String with spaces",
                        JsonSerializer.deserialize("\"String with spaces\"", String.class)),
                () -> assertEquals("String with '", JsonSerializer.deserialize("\"String with '\"", String.class)));
    }

    @Test
    public void deserialize_LocalTime_shouldReturnJson() {
        LocalTime time = LocalTime.now();
        Assertions.assertEquals(time, JsonSerializer.deserialize('"' + time.toString() + '"', LocalTime.class));
    }

    @Test
    public void deserialize_LocalDate_shouldReturnJson() {
        LocalDate date = LocalDate.now();
        Assertions.assertEquals(date, JsonSerializer.deserialize('"' + date.toString() + '"', LocalDate.class));
    }

    @Test
    public void deserialize_LocalDateTime_shouldReturnJson() {
        LocalDateTime dateTime = LocalDateTime.now();
        Assertions.assertEquals(dateTime,
                JsonSerializer.deserialize('"' + dateTime.toString() + '"', LocalDateTime.class));
    }

    @Test
    public void deserialize_UUID_shouldReturnJson() {
        UUID uuid = UUID.randomUUID();
        Assertions.assertEquals(uuid, JsonSerializer.deserialize('"' + uuid.toString() + '"', UUID.class));
    }

    @Test
    public void deserialize_Array_shouldReturnJson() {
        Integer[] expected = { 1, 2, 3 };
        Integer[] actual = JsonSerializer.deserialize("[1,2,3]", Integer[].class);

        Assertions.assertArrayEquals(expected, actual);
    }

    @Test
    public void deserialize_Collection_shouldReturnJson() {
        List<Integer> expected = new ArrayList<>();
        Collections.addAll(expected, 1, 2, 3);

        List<Integer> actual = JsonSerializer.deserialize("[1,2,3]", new TypeToken<ArrayList<Integer>>() {
        });

        Assertions.assertArrayEquals(expected.toArray(), actual.toArray());
    }

    @Test
    public void deserialize_map_shouldReturnJson() {
        Map<String, Integer> expected = new HashMap<>() {
            {
                put("one", 1);
                put("two", 2);
                put("three", 3);
            }
        };

        Map<String, Integer> actual = JsonSerializer.deserialize("{\"one\":1,\"two\":2,\"three\":3}",
                new TypeToken<Hashtable<String, Integer>>() {
                });

        assertEquals(expected, actual);
    }

    @Test
    public void deserialize_Enum_shouldReturnJson() {
        enum TestEnum {
            OPTION_1,
            OPTION_2,
            OPTION_3,
        }

        TestEnum expected = TestEnum.OPTION_1;
        TestEnum actual = JsonSerializer.deserialize(((Integer) expected.ordinal()).toString(), TestEnum.class);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void deserialize_classInstanceWithDefaultCtor_shouldReturnJson() {
        TestClassDefaultCtor expected = new TestClassDefaultCtor();
        expected.number = 1;

        TestClassDefaultCtor actual = JsonSerializer.deserialize("{\"number\":1}", TestClassDefaultCtor.class);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void deserialize_classInstanceWithParameterizedCtor_shouldReturnJson() {
        TestClassParameterizedCtor expected = new TestClassParameterizedCtor(1);
        TestClassParameterizedCtor actual = JsonSerializer.deserialize("{\"number\":1}",
                TestClassParameterizedCtor.class);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void deserialize_nestedClassInstance_shouldReturnJson() {
        TestNestedClass expected = new TestNestedClass();
        expected.nestedClass.number = 1;

        TestNestedClass actual = JsonSerializer.deserialize("{\"nestedClass\":{\"number\":1}}", TestNestedClass.class);

        Assertions.assertEquals(expected, actual);
    }
}
