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

import java.lang.RuntimeException;

/**
 * JSON runtime exception.
 *
 * @author Tony Medhat
 */

public class JsonException extends RuntimeException {

    /**
     * Constructs a new runtime exception with null as its detail message. The cause
     * is not initialized.
     */
    public JsonException() {
        super();
    }

    /**
     * Constructs a new runtime exception with the specified detail message. The
     * cause is not initialized.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the Throwable.getMessage() method.
     */
    public JsonException(String message) {
        super(message);
    }

    /**
     * Constructs a new runtime exception with the specified detail message and
     * cause.
     *
     * @param message the detail message
     * @param cause   the cause
     */
    public JsonException(String message, Throwable cause) {
        super(message, cause);
    }
}
