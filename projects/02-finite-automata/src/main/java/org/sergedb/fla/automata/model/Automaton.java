package org.sergedb.fla.automata.model;

import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

public record Automaton(
        Set<String> states,
        Set<String> alphabet,
        String initialState,
        Set<String> finalStates,
        Set<Transition> transitions
) {

    /**
     * Gets all target states for a given state and symbol.
     * The Transition record is (fromState, toState, symbol).
     *
     * @param fromState The state from which the transition originates.
     * @param symbol    The symbol for the transition.
     * @return A set of target states. Returns an empty set if no such transitions exist.
     */
    public Set<String> getTransitions(String fromState, String symbol) {
        return transitions.stream()
                .filter(t -> t.fromState().equals(fromState) && t.symbol().equals(symbol))
                .map(Transition::toState)
                .collect(Collectors.toSet());
    }

    /**
     * Determines if the automaton is deterministic (DFA).
     * An automaton is deterministic if:
     * 1. There are no epsilon transitions.
     * 2. For every state and for every input symbol, there is exactly one transition.
     *
     * @return true if the automaton is a DFA, false otherwise (i.e., it's an NDFA).
     */
    public boolean isDeterministic() {
        // Check for epsilon transitions
        boolean hasEpsilonTransitions = transitions.stream()
                .anyMatch(t -> t.symbol().equals(Transition.EPSILON));
        if (hasEpsilonTransitions) {
            return false; // Condition 1 violated
        }

        // Check for unique transitions for each state and symbol
        for (String state : states) {
            for (String symbol : alphabet) {
                long count = transitions.stream()
                        .filter(t -> t.fromState().equals(state) && t.symbol().equals(symbol))
                        .count();
                if (count != 1) {
                    return false; // Condition 2 violated (either 0 or more than 1 transition)
                }
            }
        }

        return true; // All conditions for DFA met
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Automaton {\n");
        sb.append("  States (Q): ").append(states).append(",\n");
        sb.append("  Alphabet (∑): ").append(alphabet).append(",\n");
        sb.append("  Initial State: '").append(initialState).append("',\n");
        sb.append("  Final States (F): ").append(finalStates).append(",\n");
        sb.append("  Transitions (δ): {\n");
        // For Transition(fromState, toState, symbol)
        transitions.stream()
                .sorted(Comparator.comparing(Transition::fromState)
                        .thenComparing(Transition::symbol) // Sort by symbol before toState for conventional display
                        .thenComparing(Transition::toState))
                .forEach(t -> sb.append("    δ(").append(t.fromState()).append(", ").append(t.symbol()).append(") = ").append(t.toState()).append("\n"));
        sb.append("  }\n");
        sb.append("}");
        return sb.toString();
    }
}
