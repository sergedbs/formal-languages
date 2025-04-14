package org.sergiu.lfa.grammars.interactive;

/**
 * Record class representing the result of a transition.
 *
 * @param isValid Whether the transition is valid
 * @param nextState The next state, or null if invalid
 * @param message Status message
 */
public record TransitionResult(boolean isValid, String nextState, String message) {}
