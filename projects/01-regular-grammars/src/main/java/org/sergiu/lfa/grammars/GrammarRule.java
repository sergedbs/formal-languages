package org.sergiu.lfa.grammars;

public class GrammarRule {
    private final String from;
    private final String terminal;
    private final String to;

    public GrammarRule(String from, String terminal, String to) {
        this.from = from;
        this.terminal = terminal;
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public String getTerminal() {
        return terminal;
    }

    public String getTo() {
        return to;
    }

    @Override
    public String toString() {
        return from + " -> " + terminal + (to != null ? to : "");
    }
}
