package org.sergedb.fla.regex.model;

public enum TokenType {
    LITERAL,        // a, b, 1, 2
    STAR,           // *
    PLUS,           // +
    QUESTION,       // ?
    OR,             // |
    OPEN_PAREN,     // (
    CLOSE_PAREN,    // )
    OPEN_BRACE,     // {
    CLOSE_BRACE,    // }
    COMMA,          // ,
    NUMBER,         // for repetition counts like {3} or {1,2}
    EOL,            // End of Line/Expression
    INVALID         // Invalid token
}