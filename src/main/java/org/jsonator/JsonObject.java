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

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

/**
 * Container to hold JSON object.
 *
 * @author Tony Medhat
 */
public class JsonObject extends JsonElement {
    private HashMap<String, JsonElement> members;

    /** Default constructor. */
    public JsonObject() {
        super();
        members = new HashMap<>();
    }

    /**
     * Creates {@code JsonObject} from the given JSON.
     *
     * @param source - valid JSON object string
     */
    public JsonObject(String source) {
        this();
        fromJson(source);
    }

    /**
     * Returns the number of members in this JSON objects.
     *
     * @return the number of members in this JSON object
     */
    public int size() {
        return members.size();
    }

    /**
     * Removes all the members from this JSON object. The JSON object will be
     * empty after this
     * call returns.
     */
    public void clear() {
        members.clear();
    }

    /**
     * Returns true if this JSON object contains no members.
     *
     * @return true if this JSON object contains no member
     */
    public boolean isEmpty() {
        return members.isEmpty();
    }

    /**
     * Returns a Set view of the keys contained in this JSON object. The set is
     * backed by
     * the JSON object, so changes to the JSON object are reflected in the set, and
     * vice-versa. If
     * the JSON object is modified while an iteration over the set is in progress
     * (except through the iterator's own remove operation), the results of the
     * iteration
     * are undefined.
     *
     * @return a set view of the keys contained in this JSON object
     */
    public Set<String> keySet() {
        return members.keySet();
    }

    /**
     * Returns a Collection view of the values contained in this JSON object. The
     * collection
     * is backed by the JSON object, so changes to the JSON object are reflected in
     * the collection,
     * and vice-versa. If the JSON object is modified while an iteration over the
     * collection
     * is in progress (except through the iterator's own remove operation), the
     * results of the iteration are undefined.
     *
     * @return a collection view of the values contained in this JSON object
     */
    public Collection<JsonElement> values() {
        return members.values();
    }

    /**
     * Tests if the specified object is a key in this JSON object.
     *
     * @param key key - possible key
     * @return true if and only if the specified object is a key in this
     *         JSON object, as determined by the equals method; false otherwise.
     */
    public boolean containsKey(String key) {
        if (members.containsKey(key)) {
            return true;
        }

        char[] str = key.toCharArray();
        str[0] = (Character.isUpperCase(str[0])) ? Character.toLowerCase(str[0]) : Character.toUpperCase(str[0]);
        return members.containsKey(new String(str));
    }

    /**
     * Returns true if this JSON object maps one or more keys to this value.
     *
     * @param element element whose presence in this JSON object is to be tested
     * @return true if this JSON object maps one or more keys to the specified value
     */
    public boolean containsValues(JsonElement element) {
        return members.containsValue(element);
    }

    /**
     * Returns the value to which the specified key is mapped, or null if this JSON
     * array
     * contains no mapping for the key.
     *
     * @param key the key whose associated value is to be returned
     * @return the value to which the specified key is mapped, or null if this JSON
     *         object contains no mapping for the key
     */
    public JsonElement get(String key) {
        if (members.containsKey(key)) {
            return members.get(key);
        }

        char[] str = key.toCharArray();
        str[0] = (Character.isUpperCase(str[0])) ? Character.toLowerCase(str[0]) : Character.toUpperCase(str[0]);
        return members.get(new String(str));
    }

    /**
     * Maps the specified key to the specified element in this JSON object. Neither
     * the key nor the element can be null.
     *
     * @param key     the key
     * @param element the element
     * @return the previous value of the specified key in this hashtable, or null if
     *         it did not have one
     */
    public JsonElement put(String key, JsonElement element) {
        return members.put(key, element);
    }

    /**
     * Removes the key (and its corresponding value) from this JSON object. This
     * method does nothing if the key is not in the JSON object.
     *
     * @param key the key that needs to be removed
     * @return the value to which the key had been mapped in this JSON object, or
     *         null if the key did not have a mapping
     */
    public JsonElement remove(String key) {
        return members.remove(key);
    }

    /**
     * Removes the entry for the specified key only if it is currently mapped to the
     * specified element.
     *
     * @param key     key with which the specified value is associated
     * @param element element expected to be associated with the specified key
     * @return true if this collection changed as a result of the call
     */
    public boolean remove(String key, JsonElement element) {
        return members.remove(key, element);
    }

    /**
     * Parses JSON to JsonObject.
     *
     * @param source valid JSON string
     * @return Parsed JsonObject of the given JSON string
     */
    public JsonObject fromJson(String source) {
        JsonLexer lexer = new JsonLexer(source);
        JsonObject t = (JsonObject) JsonParser.parse(lexer);
        members = t.members;
        return this;
    }

    /**
     * Converts this JSON object to a JSON string.
     *
     * @return JSON string of this JSON object
     */
    public String toJson() {
        StringBuilder sb = new StringBuilder();
        int i = 0;

        sb.append("{ ");
        for (String key : keySet()) {
            if (i != 0) {
                sb.append(", ");
            }

            sb.append("\"%s\" : ".formatted(key));

            JsonElement element = get(key);
            if (element.isJsonArray()) {
                sb.append(element.getAsJsonArray().toJson());
            } else if (element.isJsonObject()) {
                sb.append(element.getAsJsonObject().toJson());
            } else if (element.isJsonPrimitive()) {
                sb.append(element.getAsJsonPrimitive().getAsString());
            } else {
                sb.append(element);
            }

            i++;
        }
        sb.append(" }");

        return sb.toString();
    }
}
