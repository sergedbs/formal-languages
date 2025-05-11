package org.sergedb.fla.grammars.processor;

import org.sergedb.fla.grammars.model.Grammar;
import org.sergedb.fla.grammars.model.Production;
import org.sergedb.fla.grammars.model.ProductionSymbol;

import java.util.*;

/**
 * Processes a formal grammar to perform operations like string generation.
 * <p>
 * This processor takes a {@link Grammar} and provides functionality to:
 * <ul>
 *   <li>Access grammar components (terminals, non-terminals, rules)</li>
 *   <li>Generate random strings that conform to the grammar</li>
 * </ul>
 * <p>
 * The string generation uses a recursive derivation approach, randomly selecting
 * productions when expanding non-terminal symbols. A caching mechanism is employed
 * to improve performance by avoiding redundant lookups of production rules.
 */
public class GrammarProcessor {
    private final Grammar grammar;
    private final Random random;
    private final Map<String, List<List<ProductionSymbol>>> expansionCache;

    /**
     * Creates a new processor for the specified grammar.
     *
     * @param grammar The grammar to process
     * @throws NullPointerException if grammar is null
     */
    public GrammarProcessor(Grammar grammar) {
        Objects.requireNonNull(grammar, "The grammar cannot be null");
        this.grammar = grammar;
        this.random = new Random();
        this.expansionCache = new HashMap<>();
    }

    /**
     * Gets the set of non-terminal symbols from the grammar.
     *
     * @return Set of non-terminal symbols
     */
    public Set<String> getNonTerminals() {
        return grammar.nonTerminals();
    }

    /**
     * Gets the set of terminal symbols from the grammar.
     *
     * @return Set of terminal symbols
     */
    public Set<String> getTerminals() {
        return grammar.terminals();
    }

    /**
     * Gets the set of production rules from the grammar.
     *
     * @return Set of production rules
     */
    public Set<Production> getRules() {
        return grammar.rules();
    }

    /**
     * Generates a random string that conforms to the grammar.
     * <p>
     * Starting from the grammar's start symbol, this method applies
     * random production rules until all symbols are terminals.
     *
     * @return A randomly generated string that follows the grammar rules
     * @throws IllegalStateException if no expansions are found for a non-terminal
     */
    public String generateString() {
        String symbol = grammar.startSymbol();
        return generateFrom(symbol);
    }

    /**
     * Retrieves or computes all possible expansions for a given symbol.
     * <p>
     * This method uses caching to improve performance by avoiding
     * repeated searches through the grammar rules.
     *
     * @param symbol The symbol to expand
     * @return List of possible right-hand sides for the given symbol
     */
    private List<List<ProductionSymbol>> expand(String symbol) {
        return expansionCache.computeIfAbsent(symbol, s -> grammar.rules().stream()
                .filter(rule -> rule.left().equals(s))
                .map(Production::right)
                .toList());
    }

    /**
     * Recursively generates a string from a given symbol.
     * <p>
     * If the symbol is a terminal, it is returned as is.
     * If it's a non-terminal, a random production is chosen and
     * each symbol in the right-hand side is recursively processed.
     *
     * @param symbol The symbol to generate from
     * @return The generated string
     * @throws IllegalStateException if no expansions are found for a non-terminal
     */
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