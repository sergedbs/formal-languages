package org.sergiu.lfa.grammars.model;

import org.sergiu.lfa.grammars.SymbolType;

/**
 * Represents a token on the right-hand side of a grammar rule.
 * <p>
 * Each token in a grammar rule has:
 * <ul>
 *   <li>A value (the actual symbol)</li>
 *   <li>A type (terminal or non-terminal)</li>
 * </ul>
 * <p>
 * This record is used during grammar parsing and string generation to
 * distinguish between symbols that can be expanded (non-terminals) and
 * those that appear directly in the generated string (terminals).
 */
public record TokenRHS(String value, SymbolType type) {

        @Override
        public String toString() {
            return value;
        }
    }
