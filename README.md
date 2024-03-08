# JSONator

JSONator is a simple JSON serialization/deserialization library written in Java.

## Requirements

- Minimum Java version: Java 17

## Usage

### Serialization

- Serialize concrete types:

```java
import org.jsonator;

public class Main {

    public static void main(String[] args) {
        int number = 123;

        // Serialize an int to JSON
        String json = JsonSerializer.serialize(number);

        // Print the JSON string
        System.out.println(json); // 123
    }
}
```

- Serialize generic types:

```java
import org.jsonator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        List<Integer> ints = new ArrayList<>();
        Collections.addAll(ints, 1, 2, 3);

        // Serialize an int to JSON
        String json = JsonSerializer.serialize(ints);

        // Print the JSON string
        System.out.println(json); // [1,2,3]
    }
}
```

## Deserialization

- Deserialize concrete types:

```java
import org.jsonator;

public class Main {

    public static void main(String[] args) {
        // JSON string representing an integer
        String json = "123";

        // Deserialize the integer from JSON
        int number = JsonSerializer.deserialize(json, int.class);

        // Print the integer string
        System.out.println(number); // 123
    }
}
```

- Deserialize generic types:

```java
import org.jsonator;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        // JSON string representing a list
        String json = "[1,2,3]";

        // Deserialize the list from JSON
        List<Integer> ints = JsonSerializer.deserialize(json, new TypeToken<ArrayList<Integer>>() {});

        // Print the list string
        System.out.println(ints); // [1, 2, 3]
    }
}
```

## Supported Types

- Type Matrix:

| Type Name                    | Serialization | Deserialization |
|------------------------------|:-------------:|:---------------:|
| Primitives                    | ✅ | ✅ |
| LocalTime                     | ✅ | ✅ |
| LocalDate                     | ✅ | ✅ |
| LocalDateTime                 | ✅ | ✅ |
| UUID                          | ✅ | ✅ |
| Arrays                        | ✅ | ✅ |
| Enums                         | ✅ | ✅ |
| Interfaces                    | ✅ | ❌ |
| Outer Classes                 | ✅ | ✅ |
| Inner Classes                 | ❌ | ❌ |
| ArrayList                     | ✅ | ✅ |
| LinkedList                    | ✅ | ✅ |
| Stack                         | ✅ | ✅ |
| Queue                         | ✅ | ✅ |
| ArrayDequeue                  | ✅ | ✅ |
| DelayQueue                    | ✅ | ✅ |
| PriorityBlockingQueue         | ✅ | ✅ |
| LinkedTransferQueue           | ✅ | ✅ |
| Vector                        | ✅ | ✅ |
| HashSet                       | ✅ | ✅ |
| LinkedHashSet                 | ✅ | ✅ |
| TreeSet                       | ✅ | ✅ |
| Nested Collections            | ✅ | ✅ |
| ConcurrentHashMap (**String keys only !**)            | ✅ | ✅ |
| ConcurrentSkipListMap (**String keys only !**)        | ✅ | ✅ |
| HashMap (**String keys only !**)                      | ✅ | ✅ |
| Hashtable (**String keys only !**)                    | ✅ | ✅ |
| IdentityHashMap (**String keys only !**)              | ✅ | ✅ |
| LinkedHashMap (**String keys only !**)                | ✅ | ✅ |
| TreeMap (**String keys only !**)                      | ✅ | ✅ |
| WeakHashMap (**String keys only !**)                  | ✅ | ✅ |
| Nested Maps (**String keys only !**)                  | ✅ | ✅ |
