package org.sergiu.lfa.grammars;

import java.util.List;

public record GrammarRule(String left, List<TokenRHS> right) {

    @Override
    public String toString() {
        if (right.isEmpty()) {
            return left + " ----> ε";
        }
        TokenRHS terminal = right.stream().filter(TokenRHS::isTerminal).findFirst().orElse(null);
        TokenRHS nonTerminal = right.stream().filter(token -> !token.isTerminal()).findFirst().orElse(null);
        return "\n\t" + left + " --" + (terminal != null ? "(" + terminal.value() + ")" : "---") + "--> " + (nonTerminal != null ? nonTerminal.value() : "ε");
    }
}