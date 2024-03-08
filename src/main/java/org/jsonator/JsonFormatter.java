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

class JsonFormatter {

    private JsonFormatter() {
    }

    static String formatTokens(Queue<JsonToken> tokens, JsonSerializerOptions options) {
        StringBuilder sb = new StringBuilder();

        if (!options.WriteIndented) {
            while (!tokens.isEmpty()) {
                sb.append((tokens.poll()).value);
            }

            return sb.toString();
        }

        // WriteIndented
        {
            int nTabs = 0;
            int tabWidth = options.TabWidth;
            boolean indent = false;

            while (!tokens.isEmpty()) {
                JsonToken token = tokens.poll();

                boolean isTokenStart = (token.type == JsonTokenType.ARRAY_START)
                        || (token.type == JsonTokenType.OBJECT_START);

                boolean isNextTokenEnd = !tokens.isEmpty() && ((tokens.peek().type == JsonTokenType.ARRAY_END)
                        || (tokens.peek().type == JsonTokenType.OBJECT_END));

                boolean isTokenStartOrComma = isTokenStart || (token.type == JsonTokenType.COMMA);

                if (indent) {
                    sb.append(" ".repeat(tabWidth).repeat(nTabs));
                    indent = false;
                }

                sb.append(token.value);

                if (isTokenStartOrComma || isNextTokenEnd) {
                    if (!(isTokenStart && isNextTokenEnd)) {
                        sb.append("\n");
                        indent = true;
                    }

                    if (isTokenStart) {
                        nTabs++;
                    }

                    if (isNextTokenEnd) {
                        nTabs--;
                    }
                } else if (token.type == JsonTokenType.COLON) {
                    sb.append(" ");
                }
            }
        }

        return sb.toString();
    }
}
