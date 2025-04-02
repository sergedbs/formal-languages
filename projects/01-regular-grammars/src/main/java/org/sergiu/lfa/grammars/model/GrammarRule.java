package org.sergiu.lfa.grammars.model;

import org.sergiu.lfa.grammars.SymbolType;

import java.util.List;

/**
 * Represents a production rule in a formal grammar.
 * <p>
 * A grammar rule consists of:
 * <ul>
 *   <li>A left-hand side (LHS): a non-terminal symbol</li>
 *   <li>A right-hand side (RHS): a list of tokens (terminals and/or non-terminals)</li>
 * </ul>
 * <p>
 * For example, in a rule like "S -> aB":
 * <ul>
 *   <li>"S" is the left-hand side (a non-terminal)</li>
 *   <li>"aB" is the right-hand side, consisting of token "a" (terminal) and "B" (non-terminal)</li>
 * </ul>
 * <p>
 * In the context of regular grammars, rules typically have a specific structure:
 * either `A → a` or`A → aB` where 'A', 'B' are non-terminals and 'a' is a terminal.
 */
public record GrammarRule(String left, List<TokenRHS> right) {

    @Override
    public String toString() {
        if (right.isEmpty()) {
            return left + " ----> ε";
        }
        TokenRHS terminal = right.stream().filter(token -> token.type() == SymbolType.TERMINAL).findFirst().orElse(null);
        TokenRHS nonTerminal = right.stream().filter(token -> token.type() == SymbolType.NON_TERMINAL).findFirst().orElse(null);
        return "\n\t" + left + " --" + (terminal != null ? "(" + terminal.value() + ")" : "---") + "--> " + (nonTerminal != null ? nonTerminal.value() : "ε");
    }
}