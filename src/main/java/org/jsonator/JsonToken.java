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
 * JSON token containing type and string value of token.
 *
 * @author Tony Medhat
 */
public class JsonToken {

    /**
     * Type of token.
     */
    public final JsonTokenType type;

    /**
     * Value of token.
     */
    public final String value;

    /**
     * Creates {@code JsonToken}.
     *
     * @param value value of token
     * @param type type of token
     */
    public JsonToken(String value, JsonTokenType type) {
        this.value = value;
        this.type = type;
    }
}
