package org.sergiu.lfa.grammars;

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
        Set<GrammarRule> rules
) {
    @Override
    public String toString() {
        return "Non-terminals: " + nonTerminals +
                "\nTerminals: " + terminals +
                "\nStart symbol: " + startSymbol +
                "\nRules: " + rules;
    }

}
