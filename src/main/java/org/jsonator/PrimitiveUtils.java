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

class PrimitiveUtils {

    private PrimitiveUtils() {
    }

    static boolean isPrimitiveOrWrapper(Class<?> type) {
        return (type.isPrimitive() && type != void.class)
                || type == Double.class || type == Float.class || type == Long.class
                || type == Integer.class || type == Short.class || type == Character.class
                || type == Byte.class || type == Boolean.class;
    }

    static Class<?> wrap(Class<?> primitiveType) {
        if (primitiveType == short.class) return Short.class;
        if (primitiveType == int.class) return Integer.class;
        if (primitiveType == long.class) return Long.class;
        if (primitiveType == float.class) return Float.class;
        if (primitiveType == double.class) return Double.class;
        if (primitiveType == char.class) return Character.class;
        if (primitiveType == boolean.class) return Boolean.class;
        if (primitiveType == byte.class) return Byte.class;

        throw new IllegalArgumentException("'%s' is not a primitive".formatted(primitiveType));
    }

    static Class<?> unwrap(Class<?> wrapperType) {
        if (wrapperType == Short.class) return short.class;
        if (wrapperType == Integer.class) return int.class;
        if (wrapperType == Long.class) return long.class;
        if (wrapperType == Float.class) return float.class;
        if (wrapperType == Double.class) return double.class;
        if (wrapperType == Character.class) return char.class;
        if (wrapperType == Boolean.class) return boolean.class;
        if (wrapperType == Byte.class) return byte.class;

        throw new IllegalArgumentException("'%s' is not a wrapper".formatted(wrapperType));
    }
}
