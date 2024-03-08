package org.jsonator;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class TypeToken<T> {
    private final Class<? super T> rawType;
    private final Type type;

    @SuppressWarnings("unchecked")
    public TypeToken() {
        this.type = getTypeTokenTypeArgument();
        this.rawType = (Class<? super T>) getRawType(type);
    }

    @SuppressWarnings("unchecked")
    public TypeToken(Type type) {
        if (!isValidType(type)) {
            throw new UnsupportedOperationException("'%s' type is not supported".formatted(type.getTypeName()));
        }

        this.type = type;
        this.rawType = (Class<? super T>) getRawType(this.type);
    }

    public Type getType() {
        return type;
    }

    public Class<? super T> getRawType() {
        return rawType;
    }

    public static TypeToken<?> get(Type type) {
        return new TypeToken<>(type);
    }

    public static <T> TypeToken<T> get(Class<T> type) {
        return new TypeToken<>(type);
    }

    private Type getTypeTokenTypeArgument() {
        Type superclass = getClass().getGenericSuperclass();
        if (superclass instanceof ParameterizedType p) {
            if (p.getRawType() == TypeToken.class) {
                Type typeArg = p.getActualTypeArguments()[0];
                if (isValidType(typeArg)) {
                    return typeArg;
                }
            }
        }

        throw new IllegalStateException("'%s' TypeToken<?> typeToken = new TypeToken<?>() {}");
    }

    public static Class<?> getRawType(Type type) {
        if (type instanceof Class<?>) {
            return (Class<?>) type;
        } else if (type instanceof ParameterizedType p) {
            return (Class<?>) p.getRawType();
        }

        throw new UnsupportedOperationException("'%s' type is not supported".formatted(type.getTypeName()));
    }

    private boolean isValidType(Type type) {
        if (type instanceof Class) {
            return true;
        }

        return type instanceof ParameterizedType;
    }

    @Override
    public int hashCode() {
        return type.hashCode();
    }
}
