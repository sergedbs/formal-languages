package org.sergedb.fla.chomsky.model;

import java.util.List;

/**
 * Represents a production rule in a formal grammar.
 * <p>
 * A grammar rule consists of:
 * <ul>
 *   <li>A left-hand side (LHS): a non-terminal symbol</li>
 *   <li>A right-hand side (RHS): a list of symbols (terminals and/or non-terminals)</li>
 * </ul>
 * <p>
 * For example, in a rule like "S -> aB":
 * <ul>
 *   <li>"S" is the left-hand side (a non-terminal)</li>
 *   <li>"aB" is the right-hand side, consisting of symbols "a" (terminal) and "B" (non-terminal)</li>
 * </ul>
 */
public record Production(String left, List<ProductionSymbol> right) {

    @Override
    public String toString() {
        if (right.isEmpty()) {
            return left + " ----> ε";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(left).append(" ----> ");
        for (ProductionSymbol symbol : right) {
            sb.append(symbol.value());
        }
        return sb.toString();
    }
}
