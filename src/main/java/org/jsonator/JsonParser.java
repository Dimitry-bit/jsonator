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

class JsonParser {

    static JsonElement parse(JsonLexer lexer) {
        if (!lexer.hasToken()) {
            return new JsonObject();
        }

        JsonToken token = lexer.peek();
        if (token.type == JsonTokenType.OBJECT_START) {
            return parseObject(lexer);
        } else if (token.type == JsonTokenType.ARRAY_START) {
            return parseArray(lexer);
        } else {
            return parseElement(lexer.nextToken());
        }
    }

    static JsonObject parseObject(JsonLexer lexer) {
        JsonObject jsonObject = new JsonObject();
        JsonToken t = null;

        if (!lexer.hasToken()) {
            return jsonObject;
        }

        if (lexer.nextToken().type != JsonTokenType.OBJECT_START) {
            throw new JsonException("Expected start-of-object bracket");
        }

        while (lexer.hasToken() && ((t = lexer.nextToken()).type != JsonTokenType.OBJECT_END)) {
            // Remove quotes
            String key = t.value.substring(1, t.value.length() - 1);

            if (t.type != JsonTokenType.STRING) {
                throw new JsonException("Expected string, got: '%s'".formatted(t.value));
            }

            t = lexer.nextToken();
            if (t.type != JsonTokenType.COLON) {
                throw new JsonException("Expected colon, got: '%s'".formatted(t.value));
            }

            // Extract Value
            t = lexer.nextToken();
            if (t.type == JsonTokenType.OBJECT_START) {
                lexer.ungetToken();
                JsonObject j = parseObject(lexer);
                jsonObject.put(key, j);
            } else if (t.type == JsonTokenType.ARRAY_START) {
                lexer.ungetToken();
                JsonArray j = parseArray(lexer);
                jsonObject.put(key, j);
            } else {
                JsonElement e = parseElement(t);
                jsonObject.put(key, e);
            }

            t = lexer.nextToken();

            if (t.type == JsonTokenType.OBJECT_END) {
                break;
            }

            if (t.type != JsonTokenType.COMMA) {
                throw new JsonException("Expected end-of-object bracket or comma, got: '%s'".formatted(t.value));
            }
        }

        if (Objects.requireNonNull(t).type != JsonTokenType.OBJECT_END) {
            throw new JsonException("Expected end-of-object bracket");
        }

        return jsonObject;
    }

    static JsonArray parseArray(JsonLexer lexer) {
        JsonToken t = null;
        JsonArray jsonArray = new JsonArray();

        if (!lexer.hasToken()) {
            return jsonArray;
        }

        if (lexer.nextToken().type != JsonTokenType.ARRAY_START) {
            throw new JsonException("Expected start-of-array bracket");
        }

        while (lexer.hasToken() && ((t = lexer.nextToken()).type != JsonTokenType.ARRAY_END)) {
            if (t.type == JsonTokenType.OBJECT_START) {
                lexer.ungetToken();
                JsonObject j = parseObject(lexer);
                jsonArray.add(j);
            } else if (t.type == JsonTokenType.ARRAY_START) {
                lexer.ungetToken();
                JsonArray j = parseArray(lexer);
                jsonArray.add(j);
            } else {
                JsonElement e = parseElement(t);
                jsonArray.add(e);
            }

            t = lexer.nextToken();

            if (t.type == JsonTokenType.ARRAY_END) {
                break;
            }

            if (t.type != JsonTokenType.COMMA) {
                throw new JsonException("Expected end-of-array bracket or comma, got: '%s'".formatted(t.value));
            }
        }

        if (Objects.requireNonNull(t).type != JsonTokenType.ARRAY_END) {
            throw new JsonException("Expected end-of-object bracket");
        }

        return jsonArray;
    }

    static JsonElement parseNumber(JsonToken token) {
        boolean isDecimal = false;

        for (int i = 0; i < token.value.length(); ++i) {
            if (token.value.charAt(i) == '.' || token.value.charAt(i) == 'e') {
                isDecimal = true;
                break;
            }
        }

        if (isDecimal) {
            return new JsonPrimitive(Double.parseDouble(token.value));
        }

        long number = Long.parseLong(token.value);
        if ((number <= Short.MAX_VALUE) && (number >= Short.MIN_VALUE)) {
            return new JsonPrimitive((short) (number));
        } else if ((number <= Integer.MAX_VALUE) && (number >= Integer.MIN_VALUE)) {
            return new JsonPrimitive((int) (number));
        } else {
            return new JsonPrimitive(number);
        }
    }

    private static JsonElement parseElement(JsonToken token) {
        JsonElement e = null;

        if (token.type == JsonTokenType.STRING) {
            String s = token.value.substring(1, token.value.length() - 1);
            e = ((s.length() == 1) ? new JsonPrimitive(s.charAt(0)) : new JsonPrimitive(s));
        } else if (token.type == JsonTokenType.BOOLEAN) {
            e = new JsonPrimitive(Boolean.parseBoolean(token.value));
        } else if (token.type == JsonTokenType.NULL) {
            e = new JsonNull();
        } else if (token.type == JsonTokenType.NUMBER) {
            e = parseNumber(token);
        } else {
            throw new JsonException("Expected value, got: '%s'".formatted(token.value));
        }

        return e;
    }
}
