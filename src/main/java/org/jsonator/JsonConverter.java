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

import java.util.Queue;

/**
 * JsonConverter is the abstract base class for all json converters which allow
 * the serialization and deserialization from JSON to POJO (Plain Old Java
 * Objects).
 *
 * @author Tony Medhat
 */
public abstract class JsonConverter<T> {

    /**
     * Returns converter type.
     *
     * @return converter type
     */
    public abstract TypeToken<T> getMyType();

    /**
     * Returns true if this converter can convert this type.
     *
     * @param typeToConvert the type whose conversion is to be tested
     * @return true if this converter can convert this type
     */
    public boolean canConvert(TypeToken<?> typeToConvert) {
        if (typeToConvert == null) {
            return false;
        }

        return (typeToConvert.hashCode() == getMyType().hashCode());
    }

    /**
     * Serializes a value and adds it to the tokens queue.
     *
     * @param tokens  tokens queue
     * @param value   value to serialize
     * @param options serializer options
     */
    public abstract void serialize(Queue<JsonToken> tokens, Object value, JsonSerializerOptions options);

    /**
     * Returns a deserialized instance of {@code typeToConvert} from
     * {@code JsonElement}.
     *
     * @param element       element to deserialize
     * @param typeToConvert type to deserialize to
     * @param options       serializer options
     * @return Returns an instance of {@code typToConvert} populated from
     *         {@code element}
     */
    public abstract Object deserialize(JsonElement element, TypeToken<?> typeToConvert, JsonSerializerOptions options);
}
