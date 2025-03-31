package org.sergiu.lfa.grammars;

import java.util.*;

public class FiniteAutomaton {
    private final Set<String> states;
    private final Set<String> alphabet;
    private final Map<String, Map<String, String>> transitions;
    private final String startState;
    private final Set<String> finalStates;

    public FiniteAutomaton(Set<String> states, Set<String> alphabet,
                           Map<String, Map<String, String>> transitions,
                           String startState, Set<String> finalStates) {
        this.states = states;
        this.alphabet = alphabet;
        this.transitions = transitions;
        this.startState = startState;
        this.finalStates = finalStates;
    }

    public boolean accepts(String input) {
        String currentState = startState;

        for (char symbol : input.toCharArray()) {
            String sym = String.valueOf(symbol);

            if (!transitions.containsKey(currentState) ||
                    !transitions.get(currentState).containsKey(sym)) {
                return false;
            }

            currentState = transitions.get(currentState).get(sym);
        }

        return finalStates.contains(currentState);
    }


    // debug function
    public void printTransitions() {
        System.out.println("State Transitions:");
        for (String from : transitions.keySet()) {
            Map<String, String> edges = transitions.get(from);
            for (String symbol : edges.keySet()) {
                System.out.println("  " + from + " --" + symbol + "--> " + edges.get(symbol));
            }
        }
    }
}
