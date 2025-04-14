package org.sergiu.lfa.grammars.automaton;

import org.sergiu.lfa.grammars.model.Grammar;
import org.sergiu.lfa.grammars.model.Production;
import org.sergiu.lfa.grammars.model.ProductionSymbol;
import org.sergiu.lfa.grammars.model.SymbolType;

import java.util.*;

/**
 * Represents a finite automaton constructed from a grammar.
 * This class provides functionality to:
 * <ul>
 *   <li>Check if strings are accepted by the automaton</li>
 *   <li>Generate strings accepted by the automaton</li>
 *   <li>Analyze automaton properties</li>
 *   <li>Visualize the automaton structure</li>
 * </ul>
 */
public class FiniteAutomaton {
    private final Set<String> states;
    private final Set<String> alphabet;
    private final Map<String, Map<String, String>> transitions;
    private final String startState;
    private final Set<String> finalStates;

    /**
     * Constructs a finite automaton from the given grammar.
     *
     * @param grammar The grammar to construct the automaton from
     */
    public FiniteAutomaton(Grammar grammar) {
        this.states = new HashSet<>(grammar.nonTerminals());
        this.alphabet = new HashSet<>(grammar.terminals());
        this.transitions = new HashMap<>();
        this.startState = grammar.startSymbol();
        this.finalStates = new HashSet<>();

        // Build transitions and final states
        for (Production production : grammar.rules()) {
            String left = production.left();
            List<ProductionSymbol> right = production.right();

            if (!transitions.containsKey(left)) {
                transitions.put(left, new HashMap<>());
            }

            if (right.isEmpty()) {
                // Epsilon production
                finalStates.add(left);
            } else {
                ProductionSymbol terminal = right.stream()
                        .filter(symbol -> symbol.type() == SymbolType.TERMINAL)
                        .findFirst()
                        .orElse(null);

                ProductionSymbol nonTerminal = right.stream()
                        .filter(symbol -> symbol.type() == SymbolType.NON_TERMINAL)
                        .findFirst()
                        .orElse(null);

                if (terminal != null) {
                    String terminalValue = terminal.value();
                    String nextState = (nonTerminal != null) ? nonTerminal.value() : left;

                    transitions.get(left).put(terminalValue, nextState);

                    if (nonTerminal == null) {
                        finalStates.add(left);
                    }
                }
            }
        }
    }

    /**
     * Checks if the automaton accepts the given input string.
     *
     * @param input The input string to check
     * @return True if the string is accepted, false otherwise
     */
    public boolean accepts(String input) {
        String currentState = startState;

        for (char symbol : input.toCharArray()) {
            String symbolStr = String.valueOf(symbol);

            if (!alphabet.contains(symbolStr)) {
                return false; // Invalid symbol
            }

            Map<String, String> stateTransitions = transitions.get(currentState);
            if (stateTransitions == null || !stateTransitions.containsKey(symbolStr)) {
                return false; // No valid transition
            }

            currentState = stateTransitions.get(symbolStr);
        }

        return finalStates.contains(currentState);
    }

    /**
     * Prints the state transitions of the automaton.
     */
    public void printTransitions() {
        System.out.println("State Transitions:");
        for (String state : transitions.keySet()) {
            Map<String, String> stateTransitions = transitions.get(state);
            for (String symbol : stateTransitions.keySet()) {
                System.out.printf("  %s --%s--> %s%n", state, symbol, stateTransitions.get(symbol));
            }
        }
        System.out.println("Final states: " + finalStates);
    }

    /**
     * Checks if the automaton is useful (has a path from start to a final state).
     *
     * @return True if the automaton is useful, false otherwise
     */
    public boolean isUseful() {
        Set<String> reachableStates = new HashSet<>();
        findReachableStates(startState, reachableStates);

        // Check if any reachable state is a final state
        return reachableStates.stream().anyMatch(finalStates::contains);
    }

    /**
     * Recursively finds all states reachable from the given state.
     *
     * @param state The current state
     * @param reachableStates The set of reachable states to populate
     */
    private void findReachableStates(String state, Set<String> reachableStates) {
        if (reachableStates.contains(state)) {
            return; // Already visited
        }

        reachableStates.add(state);

        Map<String, String> stateTransitions = transitions.getOrDefault(state, Collections.emptyMap());
        for (String nextState : stateTransitions.values()) {
            findReachableStates(nextState, reachableStates);
        }
    }

    /**
     * Generates all strings accepted by the automaton up to a given length.
     *
     * @param maxLength The maximum length of strings to generate
     * @return A set of accepted strings
     */
    public Set<String> getAcceptedStrings(int maxLength) {
        Set<String> results = new HashSet<>();
        generateStrings("", startState, results, maxLength);
        return results;
    }

    /**
     * Recursively generates strings accepted by the automaton.
     *
     * @param currentString The string being built
     * @param state The current state
     * @param results The set of results to populate
     * @param maxLength The maximum length of strings to generate
     */
    private void generateStrings(String currentString, String state, Set<String> results, int maxLength) {
        // If current state is final, add the string to results
        if (finalStates.contains(state)) {
            results.add(currentString);
        }

        // Stop if we've reached the maximum length
        if (currentString.length() >= maxLength) {
            return;
        }

        // Try all possible transitions from the current state
        Map<String, String> stateTransitions = transitions.getOrDefault(state, Collections.emptyMap());
        for (Map.Entry<String, String> transition : stateTransitions.entrySet()) {
            String symbol = transition.getKey();
            String nextState = transition.getValue();
            generateStrings(currentString + symbol, nextState, results, maxLength);
        }
    }

    /**
     * Checks if the automaton is deterministic.
     *
     * @return True if the automaton is deterministic, false otherwise
     */
    public boolean isDeterministic() {
        // Check that each state has at most one transition for each symbol
        for (String state : states) {
            Map<String, String> stateTransitions = transitions.getOrDefault(state, Collections.emptyMap());
            Set<String> symbols = new HashSet<>();

            for (String symbol : stateTransitions.keySet()) {
                if (!symbols.add(symbol)) {
                    return false; // Symbol appears more than once
                }
            }
        }
        return true;
    }

    /**
     * Gets the set of states in the automaton.
     *
     * @return Set of states
     */
    public Set<String> getStates() {
        return Collections.unmodifiableSet(states);
    }

    /**
     * Gets the alphabet of the automaton.
     *
     * @return Set of symbols in the alphabet
     */
    public Set<String> getAlphabet() {
        return Collections.unmodifiableSet(alphabet);
    }

    /**
     * Gets the start state of the automaton.
     *
     * @return The start state
     */
    public String getStartState() {
        return startState;
    }

    /**
     * Gets the set of final states in the automaton.
     *
     * @return Set of final states
     */
    public Set<String> getFinalStates() {
        return Collections.unmodifiableSet(finalStates);
    }

    /**
     * Gets a string representation of the automaton.
     *
     * @return String representation showing states, alphabet, transitions, etc.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Finite Automaton:\n");
        sb.append("States: ").append(states).append("\n");
        sb.append("Alphabet: ").append(alphabet).append("\n");
        sb.append("Start state: ").append(startState).append("\n");
        sb.append("Final states: ").append(finalStates).append("\n");
        sb.append("Transitions:\n");

        for (String state : states) {
            Map<String, String> stateTransitions = transitions.getOrDefault(state, Collections.emptyMap());
            for (Map.Entry<String, String> transition : stateTransitions.entrySet()) {
                sb.append("  ").append(state)
                  .append(" --").append(transition.getKey()).append("--> ")
                  .append(transition.getValue()).append("\n");
            }
        }

        return sb.toString();
    }
}