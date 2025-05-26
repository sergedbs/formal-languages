package org.sergedb.fla.regex.model;

import org.sergedb.fla.regex.model.TokenType;

public record Token(TokenType type, String value) {

    @Override
    public String toString() {
        return String.format("Token(%s, %s)", type, value);
    }
}
