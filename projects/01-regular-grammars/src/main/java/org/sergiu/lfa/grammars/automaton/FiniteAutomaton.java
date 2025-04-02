/*
package org.sergiu.lfa.grammars;

import java.util.*;

public class FiniteAutomaton {
    private final Map<String, Map<String, String>> transitions;
    private final String startState;
    private final Set<String> finalStates;
    private final Set<String> states;
    private final Set<String> alphabet;

    public FiniteAutomaton(Set<String> states, Set<String> alphabet,
                          Map<String, Map<String, String>> transitions,
                          String startState, Set<String> finalStates) {

        Objects.requireNonNull(states, "States cannot be null");
        Objects.requireNonNull(alphabet, "Alphabet cannot be null");
        Objects.requireNonNull(transitions, "Transitions cannot be null");
        Objects.requireNonNull(startState, "Start state cannot be null");
        Objects.requireNonNull(finalStates, "Final states cannot be null");

        if (!states.contains(startState)) {
            throw new IllegalArgumentException("Start state must be in states set");
        }

        for (String finalState : finalStates) {
            if (!states.contains(finalState)) {
                throw new IllegalArgumentException("Final state '" + finalState + "' must be in states set");
            }
        }

        this.states = new HashSet<>(states);
        this.alphabet = new HashSet<>(alphabet);
        this.transitions = new HashMap<>();

        // Deep copy transitions
        for (Map.Entry<String, Map<String, String>> entry : transitions.entrySet()) {
            this.transitions.put(entry.getKey(), new HashMap<>(entry.getValue()));
        }

        this.startState = startState;
        this.finalStates = new HashSet<>(finalStates);
    }

    public boolean accepts(String input) {
        if (input.isEmpty()) {
            // Empty string is accepted only if the start state is final
            return finalStates.contains(startState);
        }

        return acceptsRecursive(input, 0, startState);
    }

    private boolean acceptsRecursive(String input, int position, String currentState) {
        // If we've processed the entire input, check if we're in a final state
        if (position >= input.length()) {
            return finalStates.contains(currentState);
        }

        // Get the current symbol
        String symbol = String.valueOf(input.charAt(position));

        // Check if current state has a transition for this symbol
        if (!transitions.containsKey(currentState) ||
            !transitions.get(currentState).containsKey(symbol)) {
            return false;
        }

        // Make the transition
        String nextState = transitions.get(currentState).get(symbol);

        // Special case for epsilon transitions (terminal rule)
        if ("ε".equals(nextState)) {
            // If this is the last symbol and we're in a final state, accept
            return position == input.length() - 1 && finalStates.contains(currentState);
        }

        // Continue processing with the next state
        return acceptsRecursive(input, position + 1, nextState);
    }

    public void printTransitions() {
        System.out.println("State Transitions:");

        // Use TreeSet for natural ordering of states in output
        List<String> sortedStates = new ArrayList<>(transitions.keySet());
        Collections.sort(sortedStates);

        for (String from : sortedStates) {
            Map<String, String> stateTransitions = transitions.get(from);
            if (stateTransitions.isEmpty()) {
                continue;
            }

            // Sort the symbols for consistent output
            List<String> sortedSymbols = new ArrayList<>(stateTransitions.keySet());
            Collections.sort(sortedSymbols);

            for (String symbol : sortedSymbols) {
                String to = stateTransitions.get(symbol);

                System.out.printf("  %s --%s--> %s%n",
                        from, symbol, to);
            }
        }

        System.out.print("Final states: ");
        System.out.println(String.join(", ", finalStates));
    }

    */
/**
     * Returns true if this automaton accepts any strings
     *//*

    public boolean isUseful() {
        // An automaton is useful if it has at least one path from
        // start state to a final state
        return !finalStates.isEmpty() && isReachable(startState, new HashSet<>());
    }

    */
/**
     * Checks if there's a path from the given state to any final state
     *//*

    private boolean isReachable(String state, Set<String> visited) {
        if (finalStates.contains(state)) {
            return true;
        }

        if (visited.contains(state)) {
            return false;
        }

        visited.add(state);

        Map<String, String> stateTransitions = transitions.getOrDefault(state, Collections.emptyMap());

        for (String nextState : stateTransitions.values()) {
            // Skip epsilon transitions when checking reachability
            if (!"ε".equals(nextState) && isReachable(nextState, visited)) {
                return true;
            }
        }

        return false;
    }

    */
/**
     * Gets all possible strings accepted by this automaton up to the given length
     *//*

    public Set<String> getAcceptedStrings(int maxLength) {
        Set<String> results = new HashSet<>();
        generateAcceptedStrings("", startState, results, maxLength);
        return results;
    }

    private void generateAcceptedStrings(String currentString, String state,
                                        Set<String> results, int maxLength) {
        if (currentString.length() > maxLength) {
            return;
        }

        if (finalStates.contains(state)) {
            results.add(currentString);
        }

        Map<String, String> stateTransitions = transitions.getOrDefault(state, Collections.emptyMap());
        for (Map.Entry<String, String> transition : stateTransitions.entrySet()) {
            String symbol = transition.getKey();
            String nextState = transition.getValue();

            // Skip epsilon transitions when generating strings
            if (!"ε".equals(nextState)) {
                generateAcceptedStrings(currentString + symbol, nextState, results, maxLength);
            } else if (currentString.length() < maxLength) {
                // For epsilon transitions, add the symbol but don't change state
                results.add(currentString + symbol);
            }
        }
    }
}*/
