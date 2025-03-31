package org.sergiu.lfa.grammars;

public record GrammarRule(String from, String terminal, String to) {

    @Override
    public String toString() {
        return from + " -> " + terminal + (to != null ? to : "");
    }
}
