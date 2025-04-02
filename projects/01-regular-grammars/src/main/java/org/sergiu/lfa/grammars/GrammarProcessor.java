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
}