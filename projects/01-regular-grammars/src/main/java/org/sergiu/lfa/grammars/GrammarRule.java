package org.sergiu.lfa.grammars;

import java.util.List;

public record GrammarRule(String left, List<String> right) {

    @Override
    public String toString() {
        return left + " -> " + (right.isEmpty() ? "ε" : String.join(" ", right));
    }

}

