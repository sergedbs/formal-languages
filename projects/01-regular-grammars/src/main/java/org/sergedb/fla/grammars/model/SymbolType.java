package org.sergedb.fla.grammars.model;

/**
 * Represents the type of grammar symbol.
 * <p>
 * In formal grammar theory, symbols are categorized into two types:
 * <ul>
 *   <li>TERMINAL - Symbols that appear in the final generated strings (e.g., 'a', 'b')</li>
 *   <li>NON_TERMINAL - Symbols that can be replaced by applying production rules (e.g., 'S', 'A')</li>
 * </ul>
 * This enum is used to classify symbols during grammar parsing and processing.
 */
public enum SymbolType {
    /**
     * Represents a terminal symbol that cannot be further expanded
     * and appears in the final generated strings.
     */
    TERMINAL,

    /**
     * Represents a non-terminal symbol that can be replaced by
     * applying production rules from the grammar.
     */
    NON_TERMINAL
}
