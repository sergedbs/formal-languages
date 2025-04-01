package org.sergiu.lfa.grammars;

import java.util.List;
import java.util.Set;

public record Grammar(
        Set<String> nonTerminals,
        Set<String> terminals,
        String startSymbol,
        List<GrammarRule> rules
) {
    @Override
    public String toString() {
        return "Non terminals: " + nonTerminals +
                "\nTerminals: " + terminals +
                "\nStart symbol: " + startSymbol +
                "\nRules: " + rules;
    }

}
