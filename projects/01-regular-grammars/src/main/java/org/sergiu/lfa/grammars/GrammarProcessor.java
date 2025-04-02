package org.sergiu.lfa.grammars;

import java.util.*;
import java.util.stream.Collectors;

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

    public String generateString() {
        String symbol = grammar.startSymbol();
        return generateFrom(symbol);
    }

    private List<List<TokenRHS>> expand(String symbol) {
        return grammar.rules().stream()
                .filter(rule -> rule.left().equals(symbol))
                .map(GrammarRule::right)
                .toList();
    }

    private String generateFrom(String symbol) {
        if (grammar.terminals().contains(symbol)) {
            return symbol;
        }

        List<List<TokenRHS>> expansions = expand(symbol);

        if (expansions.isEmpty()) {
            throw new IllegalStateException("No expansions found for symbol: " + symbol);
        }

        List<TokenRHS> randomRHS = expansions.get(random.nextInt(expansions.size()));

        StringBuilder result = new StringBuilder();
        for (TokenRHS s : randomRHS) {
            result.append(generateFrom(String.valueOf(s)));
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