package org.sergiu.lfa.grammars;

import java.util.List;
import java.util.Set;

public record GrammarRule(String left, Set<String> right) {

    @Override
    public String toString() {
        return left + " -> " + (right.isEmpty() ? "ε" : String.join(" ", right));
    }

}

