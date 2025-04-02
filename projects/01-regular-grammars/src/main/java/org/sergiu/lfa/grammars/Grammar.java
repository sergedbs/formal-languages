package org.sergiu.lfa.grammars;

import java.util.LinkedHashSet;
import java.util.Set;

public record Grammar(
        Set<String> nonTerminals,
        Set<String> terminals,
        String startSymbol,
        Set<GrammarRule> rules
) {
    @Override
    public String toString() {
        return "Non terminals: " + nonTerminals +
                "\nTerminals: " + terminals +
                "\nStart symbol: " + startSymbol +
                "\nRules: " + rules;
    }

}
