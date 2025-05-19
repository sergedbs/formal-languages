package org.sergedb.fla.automata.model;

public record Transition(
        String fromState,
        String toState,
        String symbol
) {
    public static final String EPSILON = ""; // Representing epsilon as an empty string
}
