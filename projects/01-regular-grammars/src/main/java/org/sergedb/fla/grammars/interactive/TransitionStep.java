package org.sergedb.fla.grammars.interactive;

/**
 * Record class representing a transition step during string processing.
 *
 * @param position Position in the input string
 * @param state Current state
 * @param symbol Input symbol
 * @param result Result of the transition
 */
public record TransitionStep(int position, String state, String symbol, TransitionResult result) {}
