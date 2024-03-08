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

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.NoSuchElementException;

class JsonLexer {

    public static final String LITERAL_TRUE = "true";
    public static final String LITERAL_FALSE = "false";
    public static final String LITERAL_NULL = "null";

    private static final Hashtable<Character, JsonTokenType> charToToken = new Hashtable<>();
    static {
        charToToken.put('{', JsonTokenType.OBJECT_START);
        charToToken.put('}', JsonTokenType.OBJECT_END);
        charToToken.put('[', JsonTokenType.ARRAY_START);
        charToToken.put(']', JsonTokenType.ARRAY_END);
        charToToken.put(':', JsonTokenType.COLON);
        charToToken.put(',', JsonTokenType.COMMA);
    }

    private ArrayList<JsonToken> tokens;
    private int index;
    private int lineIndex;

    JsonLexer() {
        tokens = new ArrayList<>();
        lineIndex = 1;
        index = 0;
    }

    JsonLexer(String source) {
        this();
        lex(source);
    }

    boolean hasToken() {
        return (index < tokens.size());
    }

    ArrayList<JsonToken> getTokens() {
        return tokens;
    }

    ArrayList<JsonToken> lex(String source) {
        tokens = new ArrayList<>();
        lineIndex = 1;
        index = 0;

        try (StringReader sr = new StringReader(source)) {
            while (true) {
                JsonToken token = readToken(sr);

                if (token == null) {
                    break;
                }
                tokens.add(token);
            }
        } catch (IOException io) {
            System.err.println("lexer: " + io.getMessage());
            io.printStackTrace();
        }

        return tokens;
    }

    JsonToken nextToken() {
        if (!hasToken()) {
            throw new NoSuchElementException("Token list is empty");
        }

        return tokens.get(index++);
    }

    public JsonToken previousToken() {
        if (index - 2 < 0) {
            throw new NoSuchElementException("Token list contains one or none tokens");
        }

        return tokens.get(index - 2);
    }

    void ungetToken() {
        if (index <= 0) {
            throw new NoSuchElementException("Token index is zero");
        }

        index--;
    }

    JsonToken peek() {
        if (!hasToken()) {
            throw new NoSuchElementException("Token list is empty");
        }

        return tokens.get(index);
    }

    private JsonToken readToken(StringReader sr) throws IOException, JsonException {
        JsonToken token = null;
        int c = '\0';

        skipWhiteSpace(sr);
        c = sr.read();

        // Handle EOF
        if (c == -1) {
            return null;
        }

        // Extract Token
        {
            if (charToToken.containsKey((char) c)) {
                token = new JsonToken(Character.toString(c), charToToken.get((char) c));
            } else {
                boolean isJsonLiteral = false;

                sr.reset();

                isJsonLiteral = ((token = lexString(sr, lineIndex)) != null)
                        || ((token = lexBool(sr)) != null)
                        || ((token = lexNull(sr)) != null)
                        || ((token = lexNumber(sr, lineIndex)) != null);

                if (!isJsonLiteral) {
                    throw new JsonException("Unexpected token '%c':line %d".formatted(c, lineIndex));
                }
            }

            sr.mark(0);
        }

        return token;
    }

    private void skipWhiteSpace(StringReader sr) throws IOException {
        int c = '\0';

        do {
            c = sr.read();

            if (!Character.isWhitespace(c)) {
                sr.reset();
                break;
            }
            sr.mark(0);

            if (c == '\n') {
                lineIndex++;
            }
        } while (c != -1);

    }

    private JsonToken lexString(StringReader sr, int lineIndex) throws IOException, JsonException {
        CharArrayWriter cw = new CharArrayWriter();
        int prevChar = '\0';
        int c = '\0';

        sr.mark(0);
        if ((c = sr.read()) != '"') {
            sr.reset();
            return null;
        }
        cw.append((char) c);

        while ((c = sr.read()) != -1) {
            cw.append((char) c);

            if (c == '"' && prevChar != '\\') {
                break;
            }

            prevChar = c;
        }

        if (c == '"') {
            return new JsonToken(cw.toString(), JsonTokenType.STRING);
        }

        throw new JsonException("Expected end-of-string quote, got: '%s':line %d".formatted(cw.toString(), lineIndex));
    }

    private JsonToken lexBool(StringReader sr) throws IOException {
        int c = 0;
        CharArrayWriter cw = new CharArrayWriter(5);

        sr.mark(0);
        while ((c = sr.read()) != -1) {
            cw.append((char) c);

            if (cw.size() == LITERAL_TRUE.length()) {
                break;
            }
        }

        if (LITERAL_TRUE.contentEquals(cw.toString())) {
            return new JsonToken(LITERAL_TRUE, JsonTokenType.BOOLEAN);
        }

        if ((c != -1) && ((c = sr.read()) != -1)) {
            cw.append((char) c);

            if (LITERAL_FALSE.contentEquals(cw.toString())) {
                return new JsonToken(LITERAL_FALSE, JsonTokenType.BOOLEAN);
            }
        }

        sr.reset();
        return null;
    }

    private JsonToken lexNull(StringReader sr) throws IOException {
        char[] charBuffer = new char[4];

        sr.mark(0);
        if (sr.read(charBuffer, 0, charBuffer.length) == -1) {
            sr.reset();
            return null;
        }

        if (LITERAL_NULL.equals(String.valueOf(charBuffer))) {
            return new JsonToken(LITERAL_NULL, JsonTokenType.NULL);
        }

        sr.reset();
        return null;
    }

    private JsonToken lexNumber(StringReader sr, int lineIndex) throws IOException, JsonException {
        CharArrayWriter cw = new CharArrayWriter();
        boolean isError = false;

        {
            boolean hasSign = false;
            boolean isFraction = false;
            boolean isExponent = false;
            int prevChar = '\0';
            int c = '\0';

            sr.mark(0);
            while ((c = sr.read()) != -1) {
                if (cw.size() == 0 && (c == '-')) {
                    cw.append((char) c);
                    hasSign = true;
                    continue;
                }

                if (Character.isDigit(c)) {
                    cw.append((char) c);
                    sr.mark(0);
                    prevChar = c;
                    continue;
                }

                if (c == '.') {
                    isError = (isFraction || isExponent);
                    isFraction = true;
                } else if (Character.toLowerCase(c) == 'e') {
                    isError = (isExponent || (prevChar == '.'));
                    isExponent = true;
                } else if ((c == '-') || (c == '+')) {
                    isError = (Character.toLowerCase(prevChar) != 'e');
                } else {
                    break;
                }

                cw.append((char) c);
                prevChar = c;

                if (isError) {
                    break;
                }
            }

            if (Character.isDigit(c)) {
                cw.append((char) c);
            } else {
                switch (Character.toLowerCase(c)) {
                    case '.':
                    case 'e':
                    case '+':
                    case '-':
                        isError = true;
                        break;
                    default:
                        sr.reset();
                        break;
                }
            }

            if (hasSign && (cw.size() < 2)) {
                isError = true;
            }
        }

        if (isError) {
            throw new JsonException("Expected a number, got '%s':line %d".formatted(cw.toString(), lineIndex));
        }

        if (cw.size() >= 1) {
            return new JsonToken(cw.toString(), JsonTokenType.NUMBER);
        }

        sr.reset();
        return null;
    }

}
