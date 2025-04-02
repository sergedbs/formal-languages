package org.sergiu.lfa.grammars;

public record TokenRHS(String value, boolean isTerminal) {

        @Override
        public String toString() {
            return value;
        }
    }
