package org.sergiu.lfa.grammars.automaton;

import org.sergiu.lfa.grammars.model.Grammar;
import org.sergiu.lfa.grammars.model.Production;
import org.sergiu.lfa.grammars.model.ProductionSymbol;
import org.sergiu.lfa.grammars.model.SymbolType;

import java.util.*;

/**
 * Represents a finite automaton constructed from a regular grammar.
 * <p>
 * This class implements the formal definition of a finite automaton as a 5-tuple:
 * (Q, Σ, δ, q0, F) where:
 * <ul>
 *   <li>Q: set of states</li>
 *   <li>Σ: alphabet (set of input symbols)</li>
 *   <li>δ: transition function δ: Q × Σ → Q</li>
 *   <li>q0: initial state</li>
 *   <li>F: set of final/accepting states</li>
 * </ul>
 * <p>
 * The automaton is constructed from a regular grammar following the rules:
 * <ul>
 *   <li>For A → aB: Create a transition from state A to state B on input a</li>
 *   <li>For A → a: Create a transition from state A to a new final state on input a</li>
 *   <li>For A → ε: Make state A a final state</li>
 * </ul>
 */
public class FiniteAutomaton {
    private final Set<String> Q; // States
    private final Set<String> Sigma; // Alphabet
    private final Map<String, Map<String, String>> delta; // Transition function
    private final String q0; // Start state
    private final Set<String> F; // Final states
    
    private static final String FINAL_STATE_SUFFIX = "_final";

    /**
     * Creates a finite automaton from the given grammar.
     *
     * @param grammar The grammar to convert into an automaton
     * @throws NullPointerException If the grammar is null
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
     * Factory method to create a finite automaton from a grammar.
     * 
     * @param grammar The grammar to convert
     * @return A new FiniteAutomaton instance
     */
    public FiniteAutomaton fromGrammar(Grammar grammar) {
        return new FiniteAutomaton(grammar);
    }

    /**
     * Initializes the transition map with empty maps for each state.
     *
     * @param nonTerminals Set of non-terminal symbols that become states
     */
    private void initializeTransitionMap(Set<String> nonTerminals) {
        nonTerminals.forEach(state -> delta.put(state, new HashMap<>()));
    }

    /**
     * Gets the transition map for a specific state.
     * <p>
     * This method is primarily used for debugging and visualization purposes.
     *
     * @param state The state to get transitions for
     * @return Map of transitions from the state or an empty map if none exist
     */
    public Map<String, String> getTransitionMapForState(String state) {
        return Collections.unmodifiableMap(delta.getOrDefault(state, Collections.emptyMap()));
    }

    /**
     * Constructs the automaton by processing grammar production rules.
     *
     * @param productions Set of productions from the grammar
     */
    private void buildAutomaton(Set<Production> productions) {
        for (Production production : productions) {
            String left = production.left();
            List<ProductionSymbol> right = production.right();

            if (right.isEmpty()) {
                // Handle epsilon production: A → ε
                F.add(left);
                continue;
            }

            // Extract terminal and non-terminal components from the production
            Optional<ProductionSymbol> terminal = right.stream()
                    .filter(symbol -> symbol.type() == SymbolType.TERMINAL)
                    .findFirst();
                    
            Optional<ProductionSymbol> nonTerminal = right.stream()
                    .filter(symbol -> symbol.type() == SymbolType.NON_TERMINAL)
                    .findFirst();

            // Process production if it contains a terminal symbol
            terminal.ifPresent(t -> 
                processProduction(left, t.value(), nonTerminal.orElse(null)));
        }
    }

    /**
     * Processes a single production rule to create appropriate transitions.
     *
     * @param sourceState The source state (left-hand side non-terminal)
     * @param inputSymbol The input symbol (terminal)
     * @param nonTerminal The optional non-terminal for the target state, or null
     */
    private void processProduction(String sourceState, String inputSymbol, ProductionSymbol nonTerminal) {
        if (nonTerminal != null) {
            // Production of the form A → aB
            delta.get(sourceState).put(inputSymbol, nonTerminal.value());
        } else {
            // Production of the form A → a
            String finalState = sourceState + FINAL_STATE_SUFFIX;
            Q.add(finalState);
            F.add(finalState);
            delta.get(sourceState).put(inputSymbol, finalState);
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
            if (stateTransitions == null || !stateTransitions.containsKey(symbol)) {
                return false;
            }

            // Follow the transition
            currentState = stateTransitions.get(symbol);
        }

        // Accept if final state
        return F.contains(currentState);
    }

    /**
     * Prints a formatted visualization of the automaton's transition function.
     */
    public void printTransitions() {
        System.out.println("State Transitions:");

        // Sort for consistent output
        List<String> sortedStates = new ArrayList<>(delta.keySet());
        Collections.sort(sortedStates);

        for (String state : sortedStates) {
            Map<String, String> stateTransitions = delta.get(state);
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
     * @throws IllegalArgumentException if maxLength is negative
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
     * @param prefix Current string prefix
     * @param state Current state
     * @param results Collection to store accepted strings
     * @param maxLength Maximum string length
     */
    private void generateStrings(String prefix, String state, Set<String> results, int maxLength) {
        // Add current string if we're in a final state
        if (F.contains(state)) {
            results.add(prefix);
        }

        // Stop recursion if maximum length reached
        if (prefix.length() >= maxLength) {
            return;
        }

        // Follow all possible transitions from current state
        Map<String, String> stateTransitions = delta.getOrDefault(state, Collections.emptyMap());
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

        // Sort for consistent output
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