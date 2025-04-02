package org.sergiu.lfa.grammars.processor;

import org.sergiu.lfa.grammars.model.Grammar;
import org.sergiu.lfa.grammars.model.Production;
import org.sergiu.lfa.grammars.model.ProductionSymbol;

import java.util.*;

public class GrammarProcessor {
    private final Grammar grammar;
    private final Random random;
    private final Map<String, List<List<ProductionSymbol>>> expansionCache;

    public GrammarProcessor(Grammar grammar) {
        Objects.requireNonNull(grammar, "The grammar cannot be null");
        this.grammar = grammar;
        this.random = new Random();
        this.expansionCache = new HashMap<>();
    }

    public Set<String> getNonTerminals() {
        return grammar.nonTerminals();
    }

    public Set<String> getTerminals() {
        return grammar.terminals();
    }

    public Set<Production> getRules() {
        return grammar.rules();
    }

    public String generateString() {
        String symbol = grammar.startSymbol();
        return generateFrom(symbol);
    }

    private List<List<ProductionSymbol>> expand(String symbol) {
        return expansionCache.computeIfAbsent(symbol, s -> grammar.rules().stream()
                .filter(rule -> rule.left().equals(s))
                .map(Production::right)
                .toList());
    }

    private String generateFrom(String symbol) {
        if (grammar.terminals().contains(symbol)) {
            return symbol;
        }

        List<List<ProductionSymbol>> expansions = expand(symbol);

        if (expansions.isEmpty()) {
            throw new IllegalStateException("No expansions found for symbol: " + symbol);
        }

        List<ProductionSymbol> randomRHS = expansions.get(random.nextInt(expansions.size()));

        StringBuilder result = new StringBuilder();
        for (ProductionSymbol token : randomRHS) {
            result.append(generateFrom(token.value()));
        }

        return result.toString();
    }
}