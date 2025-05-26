package org.sergedb.fla.automata.model;

/**
 * Represents a symbol on the right-hand side of a production rule.
 * <p>
 * Each symbol in a production rule consists of:
 * <ul>
 *   <li>A value (the actual character or symbol)</li>
 *   <li>A type (terminal or non-terminal)</li>
 * </ul>
 * <p>
 * This record is essential for grammar parsing and string generation as it
 * helps determine whether a symbol should be expanded further or included
 * directly in the output.
 */
public record ProductionSymbol(String value, SymbolType type) {

    @Override
    public String toString() {
        return value;
    }
}
