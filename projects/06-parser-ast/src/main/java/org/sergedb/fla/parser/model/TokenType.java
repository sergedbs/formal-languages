package org.sergedb.fla.parser.model;

public enum TokenType {
    // Keywords for functions
    SIN, COS, TAN,

    // Operators
    PLUS, MINUS, MULTIPLY, DIVIDE,

    // Parentheses
    LPAREN, RPAREN,

    // Literals
    NUMBER,

    // End of input
    EOF,

    // Unknown token
    UNKNOWN
}
