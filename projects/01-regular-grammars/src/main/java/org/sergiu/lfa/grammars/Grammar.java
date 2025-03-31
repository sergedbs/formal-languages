package org.sergiu.lfa.grammars;

import java.util.List;
import java.util.Random;
import java.util.Set;

public class Grammar {
    private Set<String> nonTerminals;
    private Set<String> terminals;
    private List<GrammarRule> rules;
    private String startSymbol;
    private Random random;

    public Grammar(Set<String> nonTerminals, Set<String> terminals, List<GrammarRule> rules, String startSymbol) {
        this.nonTerminals = nonTerminals;
        this.terminals = terminals;
        this.rules = rules;
        this.startSymbol = startSymbol;
        this.random = new Random();
    }

    public String generateString() {

        return null;
    }

}
