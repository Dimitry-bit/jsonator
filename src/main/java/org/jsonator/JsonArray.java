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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Predicate;

/**
 * Container to hold JSON array.
 *
 * @author Tony Medhat
 */
public class JsonArray extends JsonElement implements Iterable<JsonElement> {

    private ArrayList<JsonElement> elements;

    /** Default constructor. */
    public JsonArray() {
        super();
        elements = new ArrayList<>();
    }

    /**
     * Creates {@code JsonArray} from the given JSON.
     *
     * @param source - valid JSON array string
     */
    public JsonArray(String source) {
        this();
        fromJson(source);
    }

    /**
     * Returns the number of elements in this JSON array.
     *
     * @return the number of elements in this JSON array
     */
    public int size() {
        return elements.size();
    }

    /**
     * Returns true if this JSON array contains no elements.
     *
     * @return true if this JSON array contains no elements
     */
    public boolean isEmpty() {
        return elements.isEmpty();
    }

    /**
     * Removes all the elements from this JSON array. The JSON array will be
     * empty after this
     * call returns.
     */
    public void clear() {
        elements.clear();
    }

    /**
     * Appends the specified element to the end of this JSON array.
     *
     * @param element element to be appended to this list
     * @return true if collection changed as a result of the call
     */
    public boolean add(JsonElement element) {
        return elements.add(element);
    }

    /**
     * Appends all the elements in the specified collection to the end of this
     * JSON array, in the order that they are returned by the specified collection's
     * Iterator.
     *
     * @param elements collection containing elements to be added to this list
     * @return true if this JSON array changed as a result of the call
     */
    public boolean addAll(Collection<? extends JsonElement> elements) {
        return this.elements.addAll(elements);
    }

    /**
     * Returns true if this JSON array contains the specified element.
     *
     * @param element element whose presence in this JSON array is to be tested
     * @return true if this JSON array contains the specified element
     */
    public boolean contains(JsonElement element) {
        return elements.contains(element);
    }

    /**
     * Returns true if this JSON array contains all the elements in the specified
     * collection.
     *
     * @param elements elements to be checked for containment in this collection
     * @return true if this collection contains all the elements in this JSON array
     */
    public boolean containsAll(Collection<JsonElement> elements) {
        return this.elements.containsAll(elements);
    }

    /**
     * Returns the element at the specified position in this JSON array.
     *
     * @param index index of the element to return
     * @return the element at the specified position in this JSON array
     * @throws IndexOutOfBoundsException if the index is out of range (index &lt; 0
     *                                   || index >= size())
     */
    public JsonElement get(int index) {
        return elements.get(index);
    }

    /**
     * Returns all elements in this JSON array.
     *
     * @return all elements in this JSON array.
     */
    public Collection<JsonElement> getElements() {
        return elements;
    }

    /**
     * Removes the element at the specified position in this JSON array.
     *
     * @param index the index of the element to be removed
     * @return the element that was removed from the list
     */
    public JsonElement remove(int index) {
        return elements.remove(index);
    }

    /**
     * Removes from this JSON array all of its elements that are contained in the
     * specified collection.
     *
     * @param elements - collection containing elements to be removed from this list
     * @return true if this JSON array changed as a result of the call
     */
    public boolean removeAll(Collection<JsonElement> elements) {
        return this.elements.removeAll(elements);
    }

    /**
     * Removes all the elements of this JSON array that satisfy the given
     * predicate.
     *
     * @param filter a predicate which returns true for elements to be removed
     * @return true if any elements were removed
     */
    public boolean removeIf(Predicate<? super JsonElement> filter) {
        return this.elements.removeIf(filter);
    }

    /**
     * Parses JSON to JsonArray.
     *
     * @param source valid JSON string
     * @return Parsed JsonArray of the given JSON string
     */
    public JsonArray fromJson(String source) {
        JsonLexer lexer = new JsonLexer(source);
        JsonArray t = JsonParser.parseArray(lexer);
        t.elements = elements;
        return this;
    }

    /**
     * Converts this JSON array to a JSON string.
     *
     * @return JSON string of this JSON array
     */
    public String toJson() {
        StringBuilder sb = new StringBuilder();

        sb.append("[ ");
        for (int i = 0; i < size(); ++i) {
            JsonElement element = get(i);
            if (i != 0) {
                sb.append(", ");
            }

            if (element.isJsonArray()) {
                sb.append(element.getAsJsonArray().toJson());
            } else if (element.isJsonObject()) {
                sb.append(element.getAsJsonObject().toJson());
            } else if (element.isJsonPrimitive()) {
                sb.append(element.getAsJsonPrimitive().getAsString());
            } else {
                sb.append(element);
            }
        }
        sb.append(" ]");

        return sb.toString();

    }

    /**
     * Returns an iterator over the elements in this JSON array in proper sequence.
     *
     * @return an iterator over the elements in this JSON array in proper sequence
     */
    @Override
    public Iterator<JsonElement> iterator() {
        return elements.iterator();
    }
}
