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

package org.jsonator.Converters;

import java.lang.reflect.Array;
import java.util.Queue;

import org.jsonator.JsonArray;
import org.jsonator.JsonConverter;
import org.jsonator.JsonElement;
import org.jsonator.JsonException;
import org.jsonator.JsonSerializerOptions;
import org.jsonator.JsonToken;
import org.jsonator.JsonTokenType;
import org.jsonator.TypeToken;

public class JsonConverterArray extends JsonConverter<Array> {

    @Override
    public TypeToken<Array> getMyType() {
        return TypeToken.get(Array.class);
    }

    @Override
    public boolean canConvert(TypeToken<?> typeToConvert) {
        if (typeToConvert == null) {
            return false;
        }

        return (typeToConvert.getRawType().isArray());
    }

    @Override
    public void serialize(Queue<JsonToken> tokens, Object value, JsonSerializerOptions options) {
        int length = Array.getLength(value);

        tokens.add(new JsonToken("[", JsonTokenType.ARRAY_START));
        for (int i = 0; i < length; ++i) {
            if (i != 0) {
                tokens.add(new JsonToken(",", JsonTokenType.COMMA));
            }

            Object v = Array.get(value, i);
            TypeToken<?> vType = TypeToken.get(v.getClass());

            if (!options.hasConverter(vType)) {
                throw new JsonException("'%s' can not serialize".formatted(v.getClass().getName()));
            }

            JsonConverter<?> converter = options.getConverter(vType);
            converter.serialize(tokens, v, options);
        }
        tokens.add(new JsonToken("]", JsonTokenType.ARRAY_END));
    }

    @Override
    public Object deserialize(JsonElement element, TypeToken<?> typeToConvert, JsonSerializerOptions options) {
        Class<?> typeClass = typeToConvert.getRawType();

        if (!typeClass.isArray()) {
            throw new IllegalArgumentException("'%s' type is not an array".formatted(typeClass.getName()));
        }

        if (!element.isJsonArray()) {
            throw new IllegalArgumentException(
                    "JsonElement is not a '%s'".formatted(getMyType().getRawType().getTypeName()));
        }

        JsonArray jsonArray = element.getAsJsonArray();
        Object array = Array.newInstance(typeClass.getComponentType(), jsonArray.size());
        int length = Array.getLength(array);
        TypeToken<?> componentType = TypeToken.get(typeClass.getComponentType());

        for (int i = 0; i < length; ++i) {
            JsonElement valueElement = jsonArray.get(i);
            Object v = null;

            if (!options.hasConverter(componentType)) {
                throw new JsonException("'%s' can not deserialize".formatted(componentType.getType().getTypeName()));
            }

            JsonConverter<?> converter = options.getConverter(componentType);
            v = converter.deserialize(valueElement, componentType, options);
            Array.set(array, i, v);
        }

        return array;
    }
}
