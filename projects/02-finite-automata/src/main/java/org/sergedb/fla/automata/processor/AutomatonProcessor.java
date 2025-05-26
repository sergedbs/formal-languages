package org.sergedb.fla.automata.processor;

import org.sergedb.fla.automata.model.*;

import java.util.*;

public class AutomatonProcessor {

    /**
     * Converts a finite automaton to a regular grammar.
     *
     * @param automaton The finite automaton to convert.
     * @return The equivalent regular grammar.
     */
    public Grammar convertToRegularGrammar(Automaton automaton) {
        Set<String> nonTerminals = automaton.states();
        Set<String> terminals = automaton.alphabet();
        String startSymbol = automaton.initialState();
        Set<Production> rules = new HashSet<>();

        for (Transition transition : automaton.transitions()) {
            String left = transition.fromState();
            List<ProductionSymbol> right = new ArrayList<>();

            // Add terminal symbol
            right.add(new ProductionSymbol(transition.symbol(), SymbolType.TERMINAL));

            // Add non-terminal symbol if the transition leads to another state
            if (!transition.toState().isEmpty()) {
                right.add(new ProductionSymbol(transition.toState(), SymbolType.NON_TERMINAL));
            }

            rules.add(new Production(left, right));
        }

        // Add epsilon transitions for final states
        for (String finalState : automaton.finalStates()) {
            rules.add(new Production(finalState, Collections.emptyList()));
        }

        return new Grammar(nonTerminals, terminals, startSymbol, rules);
    }

    /**
     * Converts a non-deterministic finite automaton (NDFA) to a deterministic finite automaton (DFA).
     *
     * @param automaton The non-deterministic finite automaton to convert.
     * @return The equivalent deterministic finite automaton.
     */
    public Automaton convertToDFA(Automaton automaton) {
        Set<Set<String>> dfaStates = new HashSet<>();
        Map<Set<String>, Map<String, Set<String>>> dfaTransitions = new HashMap<>();
        Set<String> dfaFinalStates = new HashSet<>();

        // Start with the epsilon-closure of the initial state
        Set<String> initialState = epsilonClosure(Set.of(automaton.initialState()), automaton);
        dfaStates.add(initialState);

        Queue<Set<String>> queue = new LinkedList<>();
        queue.add(initialState);

        while (!queue.isEmpty()) {
            Set<String> currentState = queue.poll();
            Map<String, Set<String>> transitions = new HashMap<>();

            for (String symbol : automaton.alphabet()) {
                if (symbol.equals(Transition.EPSILON)) continue;

                // Compute the epsilon-closure of the move
                Set<String> move = epsilonClosure(move(currentState, symbol, automaton), automaton);
                if (!move.isEmpty()) {
                    transitions.put(symbol, move);
                    if (!dfaStates.contains(move)) {
                        dfaStates.add(move);
                        queue.add(move);
                    }
                }
            }

            dfaTransitions.put(currentState, transitions);

            // Check if the current state contains any final states
            if (currentState.stream().anyMatch(automaton.finalStates()::contains)) {
                dfaFinalStates.add(String.join(",", currentState));
            }
        }

        // Convert DFA states and transitions to the required format
        Set<String> dfaStateNames = new HashSet<>();
        Set<Transition> dfaTransitionSet = new HashSet<>();

        for (Set<String> state : dfaStates) {
            String stateName = String.join(",", state);
            dfaStateNames.add(stateName);

            Map<String, Set<String>> transitions = dfaTransitions.get(state);
            if (transitions != null) {
                for (Map.Entry<String, Set<String>> entry : transitions.entrySet()) {
                    String symbol = entry.getKey();
                    String toState = String.join(",", entry.getValue());
                    dfaTransitionSet.add(new Transition(stateName, toState, symbol));
                }
            }
        }

        return new Automaton(dfaStateNames, automaton.alphabet(), String.join(",", initialState), dfaFinalStates, dfaTransitionSet);
    }

    private Set<String> epsilonClosure(Set<String> states, Automaton automaton) {
        Set<String> closure = new HashSet<>(states);
        Queue<String> queue = new LinkedList<>(states);

        while (!queue.isEmpty()) {
            String state = queue.poll();
            for (Transition transition : automaton.transitions()) {
                if (transition.fromState().equals(state) && transition.symbol().equals(Transition.EPSILON)) {
                    if (closure.add(transition.toState())) {
                        queue.add(transition.toState());
                    }
                }
            }
        }
        return closure;
    }

    private Set<String> move(Set<String> states, String symbol, Automaton automaton) {
        Set<String> result = new HashSet<>();
        for (String state : states) {
            for (Transition transition : automaton.transitions()) {
                if (transition.fromState().equals(state) && transition.symbol().equals(symbol)) {
                    result.add(transition.toState());
                }
            }
        }
        return result;
    }
}
