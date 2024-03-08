/*
 *
 * Copyright (c) 2023 Tony Medhat
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.jsonator;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.Queue;

/*
 * Supported Types:
 * Type Name                    | S | D
 * _____________________________|___|___
 * Primitives                   | T | T
 * Date                         | T | T
 * UUIDs                        | T | T
 * Arrays                       | T | T
 * Enums                        | T | T
 * Outer Classes                | T | T
 * ArrayList                    | T | T
 * LinkedList                   | T | T
 * Stack                        | T | T
 * Queue                        | T | T
 * ArrayDequeue                 | T | T
 * DelayQueue                   | T | T
 * PriorityBlockingQueue        | T | T
 * LinkedTransferQueue          | T | T
 * Vector                       | T | T
 * HashSet                      | T | T
 * LinkedHashSet                | T | T
 * TreeSet                      | T | T
 * Nested Collections           | T | T
 * Maps ( String Key! )         | T | T
 * ConcurrentHashMap            | T | T
 * ConcurrentSkipListMap        | T | T
 * HashMap                      | T | T
 * Hashtable                    | T | T
 * IdentityHashMap              | T | T
 * LinkedHashMap                | T | T
 * TreeMap                      | T | T
 * WeakHashMap                  | T | T
 * Nested Maps                  | T | T
 */

/**
 * High level class for serializing and deserializing JSON.
 *
 * <p>
 * Unsupported Types:
 * Abstract Classes
 * Inner Classes
 * Anonymous Classes
 * Interfaces
 * Multidimensional Arrays
 * <p>
 *
 * NOTE: Some of these require a lot of work and will likely never be used
 * in this project.
 *
 * @author Tony Medhat
 */
public class JsonSerializer {

    private JsonSerializer() {
    }

    /**
     * Returns an instance of {@code T} populated from the given JSON string.
     *
     * @param <T>    type to deserialize
     * @param source valid JSON string
     * @param type   type to deserialize class
     * @return instance of {@code T}
     * @throws JsonException                 if no converter is found
     * @throws UnsupportedOperationException if {@code T} deserialization is
     *                                       supported
     */
    @SuppressWarnings("unchecked")
    public static <T> T deserialize(String source, Type type) {
        return deserialize(source, (TypeToken<T>) TypeToken.get(type), JsonSerializerOptions.DefaultOptions);
    }

    /**
     * Returns an instance of {@code T} populated from the given JSON string.
     *
     * @param <T>    type to deserialize
     * @param source valid JSON string
     * @param type   type to deserialize class
     * @return instance of {@code T}
     * @throws JsonException                 if no converter is found
     * @throws UnsupportedOperationException if {@code T} deserialization is
     *                                       supported
     */
    public static <T> T deserialize(String source, Class<T> type) {
        return deserialize(source, TypeToken.get(type), JsonSerializerOptions.DefaultOptions);
    }

    /**
     * Returns an instance of {@code T} populated from the given JSON string.
     *
     * @param <T>    type to deserialize
     * @param source valid JSON string
     * @param type   type to deserialize class
     * @return instance of {@code T}
     * @throws JsonException                 if no converter is found
     * @throws UnsupportedOperationException if {@code T} deserialization is
     *                                       supported
     */
    public static <T> T deserialize(String source, TypeToken<T> type) {
        return deserialize(source, type, JsonSerializerOptions.DefaultOptions);
    }

    /**
     * Returns an instance of {@code T} populated from the given JSON string.
     * <p>
     * {@link JsonSerializer#deserialize(String, TypeToken, JsonSerializerOptions)}
     */
    @SuppressWarnings("unchecked")
    public static <T> T deserialize(String source, Type type, JsonSerializerOptions option) {
        return deserialize(source, (TypeToken<T>) TypeToken.get(type), option);
    }

    /**
     * Returns an instance of {@code T} populated from the given JSON string.
     * <p>
     * {@link JsonSerializer#deserialize(String, TypeToken, JsonSerializerOptions)}
     */
    public static <T> T deserialize(String source, Class<T> type, JsonSerializerOptions option) {
        return deserialize(source, TypeToken.get(type), option);
    }

    /**
     * Returns an instance of {@code T} populated from the given JSON string.
     *
     * @param <T>     type to deserialize
     * @param source  valid JSON string
     * @param type    type to deserialize class
     * @param options serializer options
     * @return instance of {@code T}
     * @throws JsonException                 if no converter is found
     * @throws UnsupportedOperationException if {@code T} deserialization is
     *                                       supported
     */
    @SuppressWarnings("unchecked")
    public static <T> T deserialize(String source, TypeToken<T> type, JsonSerializerOptions options) {
        JsonLexer lexer = new JsonLexer(source);
        JsonElement jsonElement = JsonParser.parse(lexer);

        if (!options.hasConverter(type)) {
            throw new JsonException("'%s' can not deserialize".formatted(type.getType().getTypeName()));
        }

        JsonConverter<?> converter = options.getConverter(type);
        Object value = converter.deserialize(jsonElement, type, options);
        return (T) value;
    }

    /**
     * Returns a JSON string from the given {@code object}.
     *
     * @param object object to serialize
     * @return JSON string
     */
    public static String serialize(Object object) {
        return serialize(object, JsonSerializerOptions.DefaultOptions);
    }

    /**
     * Returns a JSON string from the given {@code object}.
     *
     * @param object  object to serialize
     * @param options serialization options
     * @return JSON string
     */
    public static String serialize(Object object, JsonSerializerOptions options) {
        Queue<JsonToken> tokens = new LinkedList<>();
        TypeToken<?> type = TypeToken.get(object.getClass());

        if (!options.hasConverter(type)) {
            throw new JsonException("'%s' can not deserialize".formatted(object.getClass().getName()));
        }

        JsonConverter<?> converter = options.getConverter(type);
        converter.serialize(tokens, object, options);

        return JsonFormatter.formatTokens(tokens, options);
    }
}
