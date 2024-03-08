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
 * Object representation of JSON null.
 *
 * @author Tony Medhat
 */
public class JsonNull extends JsonElement {
    /**
     * Returns true if other object is equal to this object, otherwise false.
     *
     * @return true if other object is equal to this object, otherwise false
     */
    @Override
    public boolean equals(Object other) {
        return (other instanceof JsonNull);
    }

    /**
     * Returns literal "null"
     *
     * @return literal "null"
     */
    @Override
    public String toString() {
        return "null";
    }
}
