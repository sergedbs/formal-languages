package org.sergiu.lfa.grammars;

public record TokenRHS(String value, SymbolType type) {

        @Override
        public String toString() {
            return value;
        }
    }
