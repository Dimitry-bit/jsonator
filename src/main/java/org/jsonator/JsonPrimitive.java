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

import java.util.Objects;

/**
 * JsonPrimitive is a container to hold JSON primitive values.
 *
 * @author Tony Medhat
 */
public class JsonPrimitive extends JsonElement {

    /**
     * Value.
     */
    public final Object value;

    /**
     * Character JsonPrimitive constructor.
     *
     * @param c character
     */
    public JsonPrimitive(Character c) {
        value = Objects.requireNonNull(c);
    }

    /**
     * String JsonPrimitive constructor.
     *
     * @param s string
     */
    public JsonPrimitive(String s) {
        value = Objects.requireNonNull(s);
    }

    /**
     * Boolean JsonPrimitive constructor.
     *
     * @param b boolean
     */
    public JsonPrimitive(Boolean b) {
        value = Objects.requireNonNull(b);
    }

    /**
     * Number JsonPrimitive constructor.
     *
     * @param n number
     */
    public JsonPrimitive(Number n) {
        value = Objects.requireNonNull(n);
    }

    /**
     * Returns true if value is an instance of {@code Boolean}.
     *
     * @return true if value is an instance of {@code Boolean}
     */
    public boolean isBoolean() {
        return (value instanceof Boolean);
    }

    /**
     * Returns true if value is an instance of {@code Number}.
     *
     * @return true if value is an instance of {@code Number}
     */
    public boolean isNumber() {
        return (value instanceof Number);
    }

    /**
     * Returns true if value is an instance of {@code Character}.
     *
     * @return true if value is an instance of {@code Character}
     */
    public boolean isChar() {
        return (value instanceof Character);
    }

    /**
     * Returns true if value is an instance of {@code String}.
     *
     * @return true if value is an instance of {@code String}
     */
    public boolean isString() {
        return (value instanceof String);
    }

    /**
     * Returns value as a string.
     *
     * @return value as a string
     */
    public String getAsString() {
        return (isString() ? (String) value : value.toString());
    }

    /**
     * Returns value as a {@code Number}.
     *
     * @return value as a {@code Number}
     * @throws JsonException if value is not an instance of {@code Number}
     * @see #isNumber()
     */
    public Number getAsNumber() {
        if (!isNumber()) {
            throw new JsonException("Primitive is not a number");
        }

        return ((Number) value);
    }

    /**
     * Returns value as a {@code Boolean}.
     *
     * @return value as a {@code Boolean}
     * @throws JsonException if value is not an instance of {@code Boolean}
     * @see #isBoolean()
     */
    public Boolean getAsBoolean() {
        return (isBoolean() ? (Boolean) value : Boolean.parseBoolean(getAsString()));
    }

    /**
     * Returns value as a {@code Character}.
     *
     * @return value as a {@code Character}
     * @throws JsonException if value is not an instance of {@code Character}
     * @see #isChar()
     */
    public Character getAsCharacter() {
        if (isChar()) {
            return (Character) (value);
        }

        String s = getAsString();
        if (!s.isEmpty()) {
            return s.charAt(0);
        }

        throw new JsonException("String is empty");
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;

        }

        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }

        return (value == ((JsonPrimitive) obj).value);
    }

    @Override
    public String toString() {
        return getAsString();
    }
}
