package org.sergiu.lfa.grammars;

import java.util.*;

public class GrammarProcessor {
    private final Grammar grammar;
    private final Random random;

    public GrammarProcessor(Grammar grammar) {
        Objects.requireNonNull(grammar, "The grammar cannot be null");
        this.grammar = grammar;
        this.random = new Random();
    }

    public Set<String> getNonTerminals() {
        return grammar.nonTerminals();
    }

    public Set<String> getTerminals() {
        return grammar.terminals();
    }

    public Set<GrammarRule> getRules() {
        return grammar.rules();
    }

    public String getStartSymbol() {
        return grammar.startSymbol();
    }

    public String generateString() {

        StringBuilder result = new StringBuilder();
        int length = random.nextInt(1, 10);
        List<String> terminalsList = new ArrayList<>(grammar.terminals());

        for (int i = 0; i < length; i++) {
            result.append(terminalsList.get(random.nextInt(terminalsList.size())));
        }

        return result.toString();
    }

/*    public FiniteAutomaton toFiniteAutomaton() {
        Set<String> states = new HashSet<>(nonTerminals);
        Set<String> alphabet = new HashSet<>(terminals);
        Map<String, Map<String, String>> transitions = new HashMap<>();
        Set<String> finalStates = new HashSet<>();

        // Initialize transitions map for all states
        for (String state : states) {
            transitions.put(state, new HashMap<>());
        }

        // Build transitions from grammar rules
        for (GrammarRule rule : rules) {
            String from = rule.from();
            String symbol = rule.terminal();
            String to = rule.to();

            // If there's no "to" state, it means this is a terminal rule
            if (to == null) {
                transitions.get(from).put(symbol, "ε");
                finalStates.add(from);
            } else {
                transitions.get(from).put(symbol, to);
            }
        }

        // Add start state to final states if it can produce ε
        if (canProduceEmptyString(startSymbol, new HashSet<>())) {
            finalStates.add(startSymbol);
        }

        return new FiniteAutomaton(states, alphabet, transitions, startSymbol, finalStates);
    }


    private boolean canProduceEmptyString(String symbol, Set<String> visited) {
        if (visited.contains(symbol)) {
            return false;
        }

        visited.add(symbol);

        List<GrammarRule> applicable = rulesByFrom.getOrDefault(symbol, Collections.emptyList());

        for (GrammarRule rule : applicable) {
            if (rule.to() == null) {
                return true;
            }
        }

        return false;
    } */
}