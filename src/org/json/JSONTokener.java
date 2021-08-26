/*
 * Copyright (c) 2019, HP Development Company, L.P. All rights reserved.
 * This software contains confidential and proprietary information of HP.
 * The user of this software agrees not to disclose, disseminate or copy
 * such Confidential Information and shall use the software only in accordance
 * with the terms of the license agreement the user entered into with HP.
 */

package org.json;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

/**
 * A JSONTokener takes a source string and extracts characters and tokens from
 * it. It is used by the JSONObject and JSONArray constructors to parse
 * JSON source strings.
 * @author JSON.org modified by Edward Guo
 * @since 12/17/2019
 */
public class JSONTokener {

    private long    character;
    private boolean eof;
    private long    index;
    private long    line;
    private char    previous;
    private Reader  reader;
    private boolean usePrevious;

    /**
     * Construct a JSONTokener from a Reader.
     * @param reader A reader.
     */
    public JSONTokener(Reader reader) {
        this.reader = reader.markSupported() ? reader : new BufferedReader(reader);
        this.eof = false;
        this.usePrevious = false;
        this.previous = 0;
        this.index = 0;
        this.character = 1;
        this.line = 1;
    }

    /**
     * Construct a JSONTokener from an InputStream.
     * @param inputStream The source.
     */
    public JSONTokener(InputStream inputStream) throws JSONException {
        this(new InputStreamReader(inputStream));
    }

    /**
     * Construct a JSONTokener from a string.
     * @param token A source string.
     */
    public JSONTokener(String token) {
        this(new StringReader(token));
    }

    /**
     * Back up one character. This provides a sort of lookahead capability,
     * so that you can test for a digit or letter before attempting to parse
     * the next number or identifier.
     */
    public void back() throws JSONException {
        if (this.usePrevious || this.index <= 0) {
            throw new JSONException("Stepping back two steps is not supported");
        }
        this.index -= 1;
        this.character -= 1;
        this.usePrevious = true;
        this.eof = false;
    }

    /**
     * Get the hex value of a character (base16).
     * @param character A character between '0' and '9' or between 'A' and 'F' or
     * between 'a' and 'f'.
     * @return  An int between 0 and 15, or -1 if c was not a hex digit.
     */
    public static int dehexchar(char character) {
        if (character >= '0' && character <= '9') {
            return character - '0';
        }
        if (character >= 'A' && character <= 'F') {
            return character - ('A' - 10);
        }
        if (character >= 'a' && character <= 'f') {
            return character - ('a' - 10);
        }
        return -1;
    }

    public boolean end() {
        return this.eof && !this.usePrevious;
    }


    /**
     * Determine if the source string still contains characters that next()
     * can consume.
     * @return true if not yet at the end of the source.
     */
    public boolean more() throws JSONException {
        this.next();
        if (this.end()) {
            return false;
        }
        this.back();
        return true;
    }


    /**
     * Get the next character in the source string.
     * @return The next character, or 0 if past the end of the source string.
     */
    public char next() throws JSONException {
        int character;
        if (this.usePrevious) {
            this.usePrevious = false;
            character = this.previous;
        } else {
            try {
                character = this.reader.read();
            } catch (IOException exception) {
                throw new JSONException(exception);
            }

            if (character <= 0) { // End of stream
                this.eof = true;
                character = 0;
            }
        }
        this.index += 1;
        if (this.previous == '\r') {
            this.line += 1;
            this.character = character == '\n' ? 0 : 1;
        } else if (character == '\n') {
            this.line += 1;
            this.character = 0;
        } else {
            this.character += 1;
        }
        this.previous = (char) character;
        return this.previous;
    }


    /**
     * Consume the next character, and check that it matches a specified character.
     * @param character The character to match.
     * @return The character.
     * @throws JSONException if the character does not match.
     */
    public char next(char character) throws JSONException {
        char next = this.next();
        if (next != character) {
            throw this.syntaxError("Expected '" + character + "' and instead saw '" + next + "'");
        }
        return next;
    }


    /**
     * Get the next n characters.
     * @param next The number of characters to take.
     * @return A string of n characters.
     * @throws JSONException if JSON cannot be read
     */
    public String next(int next) throws JSONException {
        if (next == 0)
            return "";
        char[] chars = new char[next];
        int position = 0;

        while (position < next) {
            chars[position] = this.next();
            if (this.end()) {
                throw this.syntaxError("Substring bounds error");
            }
            position += 1;
        }
        return new String(chars);
    }


    /**
     * Get the next char in the string, skipping whitespace.
     * @throws JSONException
     * @return  A character, or 0 if there are no more characters.
     */
    public char nextClean() throws JSONException {
        while (true) {
            char character = this.next();
            if (character == 0 || character > ' ') {
                return character;
            }
        }
    }


    /**
     * Return the characters up to the next close quote character.
     * Backslash processing is done. The formal JSON format does not
     * allow strings in single quotes, but an implementation is allowed to
     * accept them.
     * @param quote The quoting character, either
     *      <code>"</code>&nbsp;<small>(double quote)</small> or
     *      <code>'</code>&nbsp;<small>(single quote)</small>.
     * @return      A String.
     * @throws JSONException Unterminated string.
     */
    public String nextString(char quote) throws JSONException {
        char character;
        StringBuilder builder = new StringBuilder();
        for (;;) {
            character = this.next();
            switch (character) {
                case 0:
                case '\n':
                case '\r':
                    throw this.syntaxError("Unterminated string");
                case '\\':
                    character = this.next();
                    switch (character) {
                        case 'b':
                            builder.append('\b');
                            break;
                        case 't':
                            builder.append('\t');
                            break;
                        case 'n':
                            builder.append('\n');
                            break;
                        case 'f':
                            builder.append('\f');
                            break;
                        case 'r':
                            builder.append('\r');
                            break;
                        case 'u':
                            builder.append((char)Integer.parseInt(this.next(4), 16));
                            break;
                        case '"':
                        case '\'':
                        case '\\':
                        case '/':
                            builder.append(character);
                            break;
                        default:
                            throw this.syntaxError("Illegal escape.");
                    }
                    break;
                default:
                    if (character == quote) {
                        return builder.toString();
                    }
                    builder.append(character);
            }
        }
    }


    /**
     * Get the text up but not including the specified character or the
     * end of line, whichever comes first.
     * @param  delimiter A delimiter character.
     * @return   A string.
     */
    public String nextTo(char delimiter) throws JSONException {
        StringBuilder builder = new StringBuilder();
        for (;;) {
            char character = this.next();
            if (character == delimiter || character == 0 || character == '\n' || character == '\r') {
                if (character != 0) {
                    this.back();
                }
                return builder.toString().trim();
            }
            builder.append(character);
        }
    }


    /**
     * Get the text up but not including one of the specified delimiter
     * characters or the end of line, whichever comes first.
     * @param delimiters A set of delimiter characters.
     * @return A string, trimmed.
     */
    public String nextTo(String delimiters) throws JSONException {
        char character;
        StringBuilder builder = new StringBuilder();
        for (;;) {
            character = this.next();
            if (delimiters.indexOf(character) >= 0 || character == 0 || character == '\n' || character == '\r') {
                if (character != 0) {
                    this.back();
                }
                return builder.toString().trim();
            }
            builder.append(character);
        }
    }


    /**
     * Get the next value. The value can be a Boolean, Double, Integer,
     * JSONArray, JSONObject, Long, or String, or the JSONObject.NULL object.
     * @throws JSONException If syntax error.
     * @return An object.
     */
    public Object nextValue() throws JSONException {
        char character = this.nextClean();
        String string;

        switch (character) {
            case '"':
            case '\'':
                return this.nextString(character);
            case '{':
                this.back();
                return new JSONObject(this);
            case '[':
                this.back();
                return new JSONArray(this);
        }
        /*
         * Handle unquoted text. This could be the values true, false, or
         * null, or it can be a number. An implementation (such as this one)
         * is allowed to also accept non-standard forms.
         * Accumulate characters until we reach the end of the text or a
         * formatting character.
         */
        StringBuilder builder = new StringBuilder();
        while (character >= ' ' && ",:]}/\\\"[{;=#".indexOf(character) < 0) {
            builder.append(character);
            character = this.next();
        }
        this.back();

        string = builder.toString().trim();
        if ("".equals(string)) {
            throw this.syntaxError("Missing value");
        }
        return JSONObject.stringToValue(string);
    }


    /**
     * Skip characters until the next character is the requested character.
     * If the requested character is not found, no characters are skipped.
     * @param targetedChar A character to skip to.
     * @return The requested character, or zero if the requested character
     * is not found.
     */
    public char skipTo(char targetedChar) throws JSONException {
        char character;
        try {
            long startIndex = this.index;
            long startCharacter = this.character;
            long startLine = this.line;
            this.reader.mark(1000000);
            do {
                character = this.next();
                if (character == 0) {
                    this.reader.reset();
                    this.index = startIndex;
                    this.character = startCharacter;
                    this.line = startLine;
                    return character;
                }
            } while (character != targetedChar);
        } catch (IOException exception) {
            throw new JSONException(exception);
        }
        this.back();
        return character;
    }


    /**
     * Make a JSONException to signal a syntax error.
     * @param message The error message.
     * @return  A JSONException object, suitable for throwing
     */
    public JSONException syntaxError(String message) {
        return new JSONException(message + this.toString());
    }


    /**
     * Make a printable string of this JSONTokener.
     * @return " at {index} [character {character} line {line}]"
     */
    public String toString() {
        return " at " + this.index + " [character " + this.character + " line " + this.line + "]";
    }
}
