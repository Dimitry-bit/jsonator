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

/**
 * JsonElement is the abstract base class for all Json elements.
 *
 * @author Tony Medhat
 */
public abstract class JsonElement {

    /**
     * Returns true if this {@code JsonElement} is an instance of {@code JsonArray}.
     *
     * @return true if this is an instance of {@code JsonArray}
     */
    public boolean isJsonArray() {
        return (this instanceof JsonArray);
    }

    /**
     * Returns true if this {@code JsonElement} is an instance of
     * {@code JsonObject}.
     *
     * @return true if this is an instance of {@code JsonObject}
     */
    public boolean isJsonObject() {
        return (this instanceof JsonObject);
    }

    /**
     * Returns true if this {@code JsonElement} is an instance of
     * {@code JsonPrimitive}.
     *
     * @return true if this is an instance of {@code JsonPrimitive}
     */
    public boolean isJsonPrimitive() {
        return (this instanceof JsonPrimitive);
    }

    /**
     * Returns true if this {@code JsonElement} is an instance of
     * {@code JsonNull}.
     *
     * @return true if this is an instance of {@code JsonNull}
     */
    public boolean isJsonNull() {
        return (this instanceof JsonNull);
    }

    /**
     * Returns this object as {@code JsonObject}.
     *
     * @return this object as {@code JsonObject}
     * @throws JsonException if this is not an instance of {@code JsonObject}
     * @see #isJsonObject()
     */
    public JsonObject getAsJsonObject() {
        if (isJsonObject()) {
            return (JsonObject) this;
        }

        throw new JsonException("Not a Json Object");
    }

    /**
     * Returns this object as {@code JsonArray}.
     *
     * @return this object as {@code JsonArray}
     * @throws JsonException if this is not an instance of {@code JsonArray}
     * @see #isJsonArray()
     */
    public JsonArray getAsJsonArray() {
        if (isJsonArray()) {
            return (JsonArray) this;
        }

        throw new JsonException("Not a Json Array");
    }

    /**
     * Returns this object as {@code JsonPrimitive}.
     *
     * @return this object as {@code JsonPrimitive}
     * @throws JsonException if this is not an instance of {@code JsonPrimitive}
     * @see #isJsonPrimitive()
     */
    public JsonPrimitive getAsJsonPrimitive() {
        if (isJsonPrimitive()) {
            return (JsonPrimitive) this;
        }

        throw new JsonException("Not a Json Primitive");
    }
}
