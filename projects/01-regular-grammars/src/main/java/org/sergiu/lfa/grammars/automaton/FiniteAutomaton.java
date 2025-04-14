package org.sergiu.lfa.grammars.automaton;

import org.sergiu.lfa.grammars.model.Grammar;
import org.sergiu.lfa.grammars.model.Production;
import org.sergiu.lfa.grammars.model.ProductionSymbol;
import org.sergiu.lfa.grammars.model.SymbolType;

import java.util.*;

/**
 * Represents a finite automaton constructed from a grammar.
 * This class implements the formal definition of a finite automaton as a 5-tuple:
 * (Q, Σ, δ, q0, F) where:
 * - Q: set of states
 * - Σ: alphabet (set of input symbols)
 * - δ: transition function δ: Q × Σ → Q
 * - q0: initial state
 * - F: set of final/accepting states
 */
public class FiniteAutomaton {
    private final Set<String> Q;
    private final Set<String> Sigma;
    private final Map<String, Map<String, String>> delta;
    private final String q0;
    private final Set<String> F;
    
    private static final String FINAL_STATE_SUFFIX = "_final";

    /**
     * Constructs a finite automaton from the given grammar.
     *
     * @param grammar The grammar to construct the automaton from
     */
    public FiniteAutomaton(Grammar grammar) {
        Objects.requireNonNull(grammar, "Grammar cannot be null");

        this.Q = new HashSet<>(grammar.nonTerminals());
        this.Sigma = new HashSet<>(grammar.terminals());
        this.delta = new HashMap<>();
        this.q0 = grammar.startSymbol();
        this.F = new HashSet<>();

        initializeTransitionMap(grammar.nonTerminals());
        buildAutomaton(grammar.rules());
    }

    /**
     * Initialize the transition map with empty maps for each non-terminal.
     *
     * @param nonTerminals Set of non-terminal symbols
     */
    private void initializeTransitionMap(Set<String> nonTerminals) {
        nonTerminals.forEach(state -> delta.put(state, new HashMap<>()));
    }

    /**
     * Builds the automaton structure from the grammar productions.
     *
     * @param productions Set of productions from the grammar
     */
    private void buildAutomaton(Set<Production> productions) {
        for (Production production : productions) {
            String left = production.left();
            List<ProductionSymbol> right = production.right();

            if (right.isEmpty()) {
                // Handle epsilon production
                F.add(left);
                continue;
            }

            // Find terminal and non-terminal in the production
            Optional<ProductionSymbol> terminalOpt = right.stream()
                    .filter(symbol -> symbol.type() == SymbolType.TERMINAL)
                    .findFirst();

            Optional<ProductionSymbol> nonTerminalOpt = right.stream()
                    .filter(symbol -> symbol.type() == SymbolType.NON_TERMINAL)
                    .findFirst();

            // Process only if we have a terminal symbol
            terminalOpt.ifPresent(terminal ->
                processProduction(left, terminal.value(), nonTerminalOpt.orElse(null)));
        }
    }

    /**
     * Process a single production to create appropriate transitions.
     *
     * @param sourceState The source state (left-hand side)
     * @param terminalValue The terminal symbol value
     * @param nonTerminal The optional non-terminal symbol
     */
    private void processProduction(String sourceState, String terminalValue, ProductionSymbol nonTerminal) {
        if (nonTerminal != null) {
            // Regular transition: A -> aB
            delta.get(sourceState).put(terminalValue, nonTerminal.value());
        } else {
            // Terminal-only production: A -> a
            String finalState = sourceState + FINAL_STATE_SUFFIX;
            Q.add(finalState);
            F.add(finalState);
            delta.get(sourceState).put(terminalValue, finalState);
        }
    }

    /**
     * Checks if the automaton accepts the given input string.
     *
     * @param input The input string to check
     * @return True if the string is accepted, false otherwise
     */
    public boolean accepts(String input) {
        if (input == null) {
            return false;
        }

        String currentState = q0;

        for (char c : input.toCharArray()) {
            String symbol = String.valueOf(c);

            // Reject if symbol not in alphabet
            if (!Sigma.contains(symbol)) {
                return false;
            }

            // Get transitions for current state
            Map<String, String> stateTransitions = delta.get(currentState);

            // No transitions or no transition for this symbol
            if (stateTransitions == null || !stateTransitions.containsKey(symbol)) {
                return false;
            }

            // Move to next state
            currentState = stateTransitions.get(symbol);
        }

        // Accept if we end in a final state
        return F.contains(currentState);
    }

    /**
     * Prints the state transitions of the automaton in a readable format.
     */
    public void printTransitions() {
        System.out.println("State Transitions:");

        // Sort states for consistent output
        List<String> sortedStates = new ArrayList<>(delta.keySet());
        Collections.sort(sortedStates);

        for (String state : sortedStates) {
            Map<String, String> stateTransitions = delta.get(state);

            // Sort symbols for consistent output
            List<String> symbols = new ArrayList<>(stateTransitions.keySet());
            Collections.sort(symbols);

            for (String symbol : symbols) {
                System.out.printf("  δ(%s,%s) = %s%n", state, symbol, stateTransitions.get(symbol));
            }
        }

        System.out.println("Final states (F): " + F);
    }

    /**
     * Generates all strings accepted by the automaton up to a given length.
     *
     * @param maxLength The maximum length of strings to generate
     * @return A set of accepted strings
     */
    public Set<String> getAcceptedStrings(int maxLength) {
        if (maxLength < 0) {
            throw new IllegalArgumentException("Maximum length cannot be negative");
        }

        Set<String> results = new HashSet<>();
        generateStrings("", q0, results, maxLength);
        return results;
    }

    /**
     * Recursively generates strings accepted by the automaton.
     *
     * @param prefix The string built so far
     * @param state The current state
     * @param results The set to collect results
     * @param maxLength The maximum string length
     */
    private void generateStrings(String prefix, String state, Set<String> results, int maxLength) {
        // If we're in a final state, add the current string to results
        if (F.contains(state)) {
            results.add(prefix);
        }

        // Stop if we've reached the maximum length
        if (prefix.length() >= maxLength) {
            return;
        }

        // Get transitions from current state
        Map<String, String> stateTransitions = delta.getOrDefault(state, Collections.emptyMap());

        // Try all possible transitions
        for (Map.Entry<String, String> transition : stateTransitions.entrySet()) {
            String symbol = transition.getKey();
            String nextState = transition.getValue();
            generateStrings(prefix + symbol, nextState, results, maxLength);
        }
    }

    /**
     * Gets the set of states in the automaton.
     *
     * @return Unmodifiable set of states (Q)
     */
    public Set<String> getStates() {
        return Collections.unmodifiableSet(Q);
    }

    /**
     * Gets the alphabet of the automaton.
     *
     * @return Unmodifiable set of symbols in the alphabet (Σ)
     */
    public Set<String> getAlphabet() {
        return Collections.unmodifiableSet(Sigma);
    }

    /**
     * Gets the start state of the automaton.
     *
     * @return The start state (q0)
     */
    public String getStartState() {
        return q0;
    }

    /**
     * Gets the set of final states in the automaton.
     *
     * @return Unmodifiable set of final states (F)
     */
    public Set<String> getFinalStates() {
        return Collections.unmodifiableSet(F);
    }

    /**
     * Gets a string representation of the automaton.
     *
     * @return String representation using formal definition notation
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Finite Automaton (Q, Σ, δ, q0, F):\n");
        sb.append("Q = ").append(Q).append("\n");
        sb.append("Σ = ").append(Sigma).append("\n");
        sb.append("q0 = ").append(q0).append("\n");
        sb.append("F = ").append(F).append("\n");
        sb.append("Transition function δ:\n");

        List<String> sortedStates = new ArrayList<>(Q);
        Collections.sort(sortedStates);

        for (String state : sortedStates) {
            Map<String, String> stateTransitions = delta.getOrDefault(state, Collections.emptyMap());
            List<String> symbols = new ArrayList<>(stateTransitions.keySet());
            Collections.sort(symbols);

            for (String symbol : symbols) {
                sb.append("  δ(").append(state).append(",").append(symbol).append(") = ")
                  .append(stateTransitions.get(symbol)).append("\n");
            }
        }

        return sb.toString();
    }
}