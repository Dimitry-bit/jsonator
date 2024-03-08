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
import java.util.Map;
import java.util.Map.Entry;

import org.jsonator.JsonConverter;
import org.jsonator.JsonElement;
import org.jsonator.JsonException;
import org.jsonator.JsonObject;
import org.jsonator.JsonPrimitive;
import org.jsonator.JsonSerializerOptions;
import org.jsonator.JsonToken;
import org.jsonator.JsonTokenType;
import org.jsonator.TypeToken;

import java.util.Queue;

public abstract class JsonConverterMap<K, V> extends JsonConverter<Map<K, V>> {

    @SuppressWarnings("unchecked")
    public TypeToken<K> getMyKeyType() {
        ParameterizedType p = (ParameterizedType) getMyType().getType();
        return (TypeToken<K>) TypeToken.get(p.getActualTypeArguments()[0]);
    }

    @SuppressWarnings("unchecked")
    public TypeToken<V> getMyValueType() {
        ParameterizedType p = (ParameterizedType) getMyType().getType();
        return (TypeToken<V>) TypeToken.get(p.getActualTypeArguments()[1]);
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

        if (!Map.class.isAssignableFrom(rawType)) {
            return false;
        }

        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) typeToConvert.getType();

            if (parameterizedType.getActualTypeArguments().length == 2) {
                Type key = parameterizedType.getActualTypeArguments()[0];
                Type value = parameterizedType.getActualTypeArguments()[1];

                Class<?> testedKeyTypeRaw = TypeToken.getRawType(key);
                Class<?> testedValueTypeRaw = TypeToken.getRawType(value);
                Class<?> keyRawType = getMyKeyType().getRawType();
                Class<?> valueRawType = getMyValueType().getRawType();

                return (keyRawType.isAssignableFrom(testedKeyTypeRaw)
                        && valueRawType.isAssignableFrom(testedValueTypeRaw));
            }
        } else if (type instanceof Class<?>) {
            return (Map.class.isAssignableFrom((Class<?>) type));
        }

        return false;
    }

    @Override
    public void serialize(Queue<JsonToken> tokens, Object value, JsonSerializerOptions options) {
        boolean printComma = false;
        Map<?, ?> m = (Map<?, ?>) value;

        tokens.add(new JsonToken("{", JsonTokenType.OBJECT_START));
        for (Entry<?, ?> e : m.entrySet()) {
            if (printComma) {
                tokens.add(new JsonToken(",", JsonTokenType.COMMA));
            }

            TypeToken<?> valueType = TypeToken.get(e.getValue().getClass());

            tokens.add(new JsonToken('"' + e.getKey().toString() + '"', JsonTokenType.STRING));
            tokens.add(new JsonToken(":", JsonTokenType.COLON));

            if (!options.hasConverter(valueType)) {
                throw new JsonException("'%s' can not serialize".formatted(e.getValue().getClass().getName()));
            }

            JsonConverter<?> converter = options.getConverter(valueType);
            converter.serialize(tokens, e.getValue(), options);

            printComma = true;
        }
        tokens.add(new JsonToken("}", JsonTokenType.OBJECT_END));
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

        if (!element.isJsonObject()) {
            throw new IllegalArgumentException(
                    "'%s' JsonElement is not a JsonObject".formatted(element.getClass().getName()));
        }

        ParameterizedType parameterizedType = (ParameterizedType) typeToConvert.getType();
        Class<?> mapType = (Class<?>) parameterizedType.getRawType();
        JsonObject j = element.getAsJsonObject();
        TypeToken<?> keyType = TypeToken.get(parameterizedType.getActualTypeArguments()[0]);
        TypeToken<?> valueType = TypeToken.get(parameterizedType.getActualTypeArguments()[1]);

        try {
            Map<Object, Object> map = (Map<Object, Object>) mapType.getConstructor().newInstance();

            for (String key : j.keySet()) {
                JsonElement valueElement = j.get(key);
                Object k = key;
                Object v = null;

                if (!options.hasConverter(valueType)) {
                    throw new JsonException("'%s' can not deserialize".formatted(valueType.getType().getTypeName()));
                }

                if (options.hasConverter(keyType)) {
                    JsonElement keyElement = new JsonPrimitive(key);
                    JsonConverter<?> converter = options.getConverter(keyType);
                    k = converter.deserialize(keyElement, keyType, options);
                }

                if (!options.hasConverter(valueType)) {
                    throw new JsonException("'%s' can not deserialize".formatted(valueType.getType().getTypeName()));
                }

                JsonConverter<?> converter = options.getConverter(valueType);
                v = converter.deserialize(valueElement, valueType, options);

                map.put(k, v);
            }

            return mapType.cast(map);
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
