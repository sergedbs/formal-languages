package org.sergedb.fla.parser.model;

public enum NodeType {
    NUMBER,          // For numeric literals
    BINARY_OPERATION, // For operations like +, -, *, /
    UNARY_OPERATION,  // For operations like unary minus
    FUNCTION_CALL,   // For functions like sin, cos, tan
    VARIABLE           // If you plan to support variables in the future
}
