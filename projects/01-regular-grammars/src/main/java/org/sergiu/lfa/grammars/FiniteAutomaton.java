package org.sergiu.lfa.grammars;

import java.util.*;

public class FiniteAutomaton {
    private final Map<String, Map<String, String>> transitions;
    private final String startState;
    private final Set<String> finalStates;

    public FiniteAutomaton(Set<String> states, Set<String> alphabet,
                           Map<String, Map<String, String>> transitions,
                           String startState, Set<String> finalStates) {

        if (!states.contains(startState)) {
            throw new IllegalArgumentException("Start state must be in states set");
        }

        for (String finalState : finalStates) {
            if (!states.contains(finalState)) {
                throw new IllegalArgumentException("Final state must be in states set");
            }
        }

        this.transitions = new HashMap<>(transitions);
        this.startState = startState;
        this.finalStates = new HashSet<>(finalStates);
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

            if (currentState.equals("ε")) {
                return input.indexOf(symbol) == input.length() - 1;
            }
        }

        return finalStates.contains(currentState);
    }


    public void printTransitions() {
        System.out.println("State Transitions:");
        for (String from : transitions.keySet()) {
            Map<String, String> edges = transitions.get(from);
            for (String symbol : edges.keySet()) {
                System.out.println("  " + from + " --" + symbol + "--> " + edges.get(symbol));
            }
        }
        System.out.println("Final states: " + finalStates);
    }
}
