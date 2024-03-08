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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Queue;

import org.jsonator.JsonArray;
import org.jsonator.JsonConverter;
import org.jsonator.JsonElement;
import org.jsonator.JsonException;
import org.jsonator.JsonSerializerOptions;
import org.jsonator.JsonToken;
import org.jsonator.JsonTokenType;
import org.jsonator.TypeToken;

public abstract class JsonConverterCollection<T> extends JsonConverter<Collection<T>> {

    @SuppressWarnings("unchecked")
    public TypeToken<T> getMyGenericType() {
        ParameterizedType p = (ParameterizedType) getMyType().getType();
        return (TypeToken<T>) TypeToken.get(p.getActualTypeArguments()[0]);
    }


    @Override
    public boolean canConvert(TypeToken<?> typeToConvert) {
        if (typeToConvert == null) {
            return false;
        }

        Class<?> rawType = typeToConvert.getRawType();
        Type type = typeToConvert.getType();

        if (rawType.isInterface() || Modifier.isAbstract(rawType.getModifiers())) {
            return false;
        }

        if (!Collection.class.isAssignableFrom(rawType)) {
            return false;
        }

        if (type instanceof ParameterizedType parameterizedType) {
            Type value = parameterizedType.getActualTypeArguments()[0];

            Class<?> testedValueType = TypeToken.getRawType(value);
            Class<?> valueType = getMyGenericType().getRawType();

            return (valueType.isAssignableFrom(testedValueType));
        }

        if (type instanceof Class<?>) {
            return (Collection.class.isAssignableFrom((Class<?>) type));
        }

        return false;
    }

    @Override
    public void serialize(Queue<JsonToken> tokens, Object value, JsonSerializerOptions options) {
        boolean printComma = false;
        Collection<?> c = (Collection<?>) value;

        tokens.add(new JsonToken("[", JsonTokenType.ARRAY_START));
        for (Object v : c) {
            if (printComma) {
                tokens.add(new JsonToken(",", JsonTokenType.COMMA));
            }

            TypeToken<?> valueType = TypeToken.get(v.getClass());

            if (!options.hasConverter(valueType)) {
                throw new JsonException("'%s' can not serialize".formatted(valueType.getType().getTypeName()));
            }

            JsonConverter<?> converter = options.getConverter(valueType);
            converter.serialize(tokens, v, options);
            printComma = true;
        }
        tokens.add(new JsonToken("]", JsonTokenType.ARRAY_END));
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object deserialize(JsonElement element, TypeToken<?> typeToConvert, JsonSerializerOptions options) {
        if (!canConvert(typeToConvert)) {
            throw new IllegalArgumentException(
                    "'%s' type is not a collection".formatted(typeToConvert.getType().getTypeName()));
        }

        if (element.isJsonNull()) {
            return null;
        }

        if (!element.isJsonArray()) {
            throw new IllegalArgumentException(
                    "'%s' JsonElement is not a JsonArray".formatted(element.getClass().getName()));
        }

        ParameterizedType parameterizedType = (ParameterizedType) typeToConvert.getType();
        Class<?> collectionType = typeToConvert.getRawType();
        JsonArray j = element.getAsJsonArray();
        TypeToken<?> componentType = TypeToken.get(parameterizedType.getActualTypeArguments()[0]);

        try {
            Collection<Object> collection = (Collection<Object>) collectionType.getConstructor().newInstance();

            for (int i = 0; i < j.size(); ++i) {
                JsonElement valueElement = j.get(i);
                Object v = null;

                if (!options.hasConverter(componentType)) {
                    throw new JsonException(
                            "'%s' can not deserialize".formatted(componentType.getType().getTypeName()));
                }

                if (!options.hasConverter(componentType)) {
                    throw new JsonException(
                            "'%s' can not deserialize".formatted(componentType.getType().getTypeName()));
                }

                JsonConverter<?> converter = options.getConverter(componentType);
                v = converter.deserialize(valueElement, componentType, options);

                collection.add(v);
            }

            return collectionType.cast(collection);
        } catch (InvocationTargetException | IllegalAccessException e) {
            System.err.println("deserialization: " + e.getMessage());
            e.printStackTrace();
        } catch (InstantiationException e) {
            throw new UnsupportedOperationException(
                    "User defined constructor instantiation is not supported, " + e.getMessage());
        } catch (NoSuchMethodException e) {
            throw new UnsupportedOperationException("Nested classes serialization is not supported, " + e.getMessage());
        }

        return null;
    }
}
