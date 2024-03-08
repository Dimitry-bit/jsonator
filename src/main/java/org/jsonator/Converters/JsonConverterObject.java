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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.*;

import org.jsonator.*;
import org.jsonator.Annotations.JsonConstructor;
import org.jsonator.Annotations.JsonIgnore;

public class JsonConverterObject extends JsonConverter<Object> {

    @Override
    public TypeToken<Object> getMyType() {
        return TypeToken.get(Object.class);
    }

    @Override
    public boolean canConvert(TypeToken<?> typeToConvert) {
        if (typeToConvert == null) {
            return false;
        }

        if (typeToConvert.getType() instanceof Class<?> typeClass) {
            return !typeClass.isMemberClass() && !typeClass.isAnonymousClass()
                    && !typeClass.isLocalClass() && !typeClass.isInterface();
        }

        return false;
    }

    @Override
    public void serialize(Queue<JsonToken> tokens, Object value, JsonSerializerOptions options) {
        Set<Field> fields = new LinkedHashSet<>();
        Class<?> objectClass = value.getClass();

        Collections.addAll(fields, objectClass.getFields());
        Collections.addAll(fields, objectClass.getDeclaredFields());

        try {
            boolean isPrintComma = false;
            tokens.add(new JsonToken("{", JsonTokenType.OBJECT_START));
            for (Field f : fields) {
                if (f.isAnnotationPresent(JsonIgnore.class)) {
                    continue;
                }

                if (isPrintComma) {
                    tokens.add(new JsonToken(",", JsonTokenType.COMMA));
                }

                tokens.add(new JsonToken('"' + f.getName() + '"', JsonTokenType.STRING));
                tokens.add(new JsonToken(":", JsonTokenType.COLON));

                f.setAccessible(true);
                Object v = f.get(value);

                if (v == null) {
                    tokens.add(new JsonToken("null", JsonTokenType.NULL));
                } else {
                    TypeToken<?> fieldType = TypeToken.get(f.getGenericType());

                    if (!options.hasConverter(fieldType)) {
                        throw new JsonException("'%s' Can not serialize".formatted(v.getClass().getName()));
                    }

                    JsonConverter<?> converter = options.getConverter(fieldType);
                    converter.serialize(tokens, v, options);
                }

                isPrintComma = true;
            }
            tokens.add(new JsonToken("}", JsonTokenType.OBJECT_END));
        } catch (IllegalAccessException e) {
            System.err.println("serialization: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public Object deserialize(JsonElement element, TypeToken<?> typeToConvert, JsonSerializerOptions options) {
        if (element.isJsonNull()) {
            return null;
        }

        if (!element.isJsonObject()) {
            throw new IllegalArgumentException(
                    "'%s' JsonElement is not a JsonObject".formatted(element.getClass().getName()));
        }

        try {
            Class<?> type = (Class<?>) typeToConvert.getType();
            JsonObject jsonObject = element.getAsJsonObject();
            Set<Field> fields = new LinkedHashSet<>();

            Collections.addAll(fields, type.getFields());
            Collections.addAll(fields, type.getDeclaredFields());

            Object o = createInstance(jsonObject, typeToConvert.getType(), options);

            // Note: Override fields set by object's constructor

            for (Field field : fields) {
                String key = field.getName();
                TypeToken<?> fieldType = TypeToken.get(field.getGenericType());
                Object value = null;
                JsonElement valueElement = null;

                if (!jsonObject.containsKey(key) || field.isAnnotationPresent(JsonIgnore.class)) {
                    continue;
                }

                if (!options.hasConverter(fieldType)) {
                    throw new JsonException("'%s' can not deserialize".formatted(fieldType.getType().getTypeName()));
                }

                valueElement = jsonObject.get(key);
                JsonConverter<?> valueConverter = options.getConverter(fieldType);
                value = valueConverter.deserialize(valueElement, fieldType, options);

                field.setAccessible(true);
                field.set(o, value);
            }

            return o;
        } catch (IllegalAccessException e) {
            System.err.println("deserialization: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    private Constructor<?>[] getCandidateConstructors(Type typeToConstruct) {
        Class<?> typeClass = (Class<?>) typeToConstruct;

        if (typeClass.isInterface() || Modifier.isAbstract(typeClass.getModifiers())) {
            return null;
        }

        List<Constructor<?>> ctors = new ArrayList<>();
        ctors.addAll(Arrays.asList(typeClass.getConstructors()));
        ctors.addAll(Arrays.asList(typeClass.getDeclaredConstructors()));

        Constructor<?>[] ctorsArray = ctors.toArray(Constructor<?>[]::new);
        Arrays.sort(ctorsArray, Comparator.comparingInt(Constructor::getParameterCount));

        return ctorsArray;
    }

    // Note: Generic parameters are not supported
    private Object createInstance(JsonObject jsonObject, Type typeToCreate, JsonSerializerOptions options) {
        Constructor<?>[] ctors = getCandidateConstructors(typeToCreate);

        if (ctors == null) {
            throw new JsonException("'%s' Class is either an Interface or Abstract class");
        }

        try {
            boolean isDefaultConstructor = (ctors[0].getParameterCount() == 0);

            if (isDefaultConstructor) {
                ctors[0].setAccessible(true);
                return ctors[0].newInstance();
            }

            for (Constructor<?> ctor : ctors) {
                if (!ctor.isAnnotationPresent(JsonConstructor.class)) {
                    continue;
                }

                Queue<Object> q = new LinkedList<>();
                JsonConstructor ctorAnnotation = ctor.getAnnotation(JsonConstructor.class);
                Parameter[] parameters = ctor.getParameters();

                if (ctorAnnotation.parameters().length != ctor.getParameterCount()) {
                    throw new JsonException("'%s' Parameter names annotation length does ".formatted(ctor.getName()) +
                            "not match constructor parameters length");
                }

                for (int i = 0; i < parameters.length; ++i) {
                    Parameter param = parameters[i];
                    TypeToken<?> paramType = TypeToken.get(param.getType());
                    String paramName = ctorAnnotation.parameters()[i];

                    if (!jsonObject.containsKey(paramName) || !options.hasConverter(paramType)) {
                        break;
                    }

                    JsonConverter<?> converter = options.getConverter(paramType);
                    q.add(converter.deserialize(jsonObject.get(paramName), paramType, options));
                }

                if (q.size() == parameters.length) {
                    Object[] args = q.toArray();
                    return ctor.newInstance(args);
                }
            }

            throw new JsonException("'%s': no suitable constructor is found, likely cause: missing data or annotation"
                    .formatted(typeToCreate.getTypeName()));
        } catch (InvocationTargetException | IllegalAccessException e) {
            System.err.println("deserialization: " + e.getMessage());
            e.printStackTrace();
            throw new AssertionError(e);
        } catch (InstantiationException e) {
            throw new UnsupportedOperationException(
                    "User defined constructor instantiation is not supported, " + e.getMessage());
        }
    }
}
