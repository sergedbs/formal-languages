package org.sergedb.fla.parser.model;

public record Token(TokenType type, String lexeme, Object literal, int line) {
    // Constructor for EOF or other simple tokens where literal and line might not be relevant or can be defaulted.
    public Token(TokenType type, String lexeme) {
        this(type, lexeme, null, 0); // Assuming default line 0 for simplicity here
    }
}
