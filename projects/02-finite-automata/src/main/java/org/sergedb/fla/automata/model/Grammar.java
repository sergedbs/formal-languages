package org.sergedb.fla.automata.model;

import java.util.Set;

/**
 * Represents a formal grammar with its components.
 * <p>
 * A grammar is defined as a 4-tuple G = (V_N, V_T, S, P) where:
 * <ul>
 *   <li>V_N: Set of non-terminal symbols</li>
 *   <li>V_T: Set of terminal symbols</li>
 *   <li>S: Start symbol (a special non-terminal)</li>
 *   <li>P: Set of production rules</li>
 * </ul>
 */
public record Grammar(
        Set<String> nonTerminals,
        Set<String> terminals,
        String startSymbol,
        Set<Production> rules
) {
    @Override
    public String toString() {
        return "Non-terminals: " + nonTerminals +
                "\nTerminals: " + terminals +
                "\nStart symbol: " + startSymbol +
                "\nRules: " + rules;
    }

    /**
     * Classifies the grammar based on the Chomsky hierarchy.
     *
     * @return A string representing the type of the grammar (Type 0, Type 1, Type 2, or Type 3).
     */
    public String classifyChomskyHierarchy() {
        boolean isType3 = rules.stream().allMatch(rule -> {
            // Type 3: Regular Grammar
            // Each production is of the form A -> aB or A -> a (A is non-terminal, a is terminal, B is non-terminal or empty)
            return rule.left().length() == 1 &&
                   nonTerminals.contains(rule.left()) &&
                   rule.right().stream().allMatch(symbol -> terminals.contains(symbol.value()) || nonTerminals.contains(symbol.value()));
        });

        if (isType3) {
            return "Type 3 (Regular Grammar)";
        }

        boolean isType2 = rules.stream().allMatch(rule -> {
            // Type 2: Context-Free Grammar
            // Each production is of the form A -> α (A is a single non-terminal, α is a string of terminals and/or non-terminals)
            return rule.left().length() == 1 &&
                   nonTerminals.contains(rule.left());
        });

        if (isType2) {
            return "Type 2 (Context-Free Grammar)";
        }

        boolean isType1 = rules.stream().allMatch(rule -> {
            // Type 1: Context-Sensitive Grammar
            // Each production is of the form αAβ -> αγβ (|γ| >= 1, α, β, γ are strings of terminals and/or non-terminals, A is a non-terminal)
            return rule.right().size() >= rule.left().length();
        });

        if (isType1) {
            return "Type 1 (Context-Sensitive Grammar)";
        }

        // Type 0: Unrestricted Grammar
        return "Type 0 (Unrestricted Grammar)";
    }
}
