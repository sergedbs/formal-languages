package org.sergiu.lfa.grammars;

import java.util.*;

public class Grammar {
    private Set<String> nonTerminals;
    private Set<String> terminals;
    private String startSymbol;
    private Random random;

    public Grammar(Set<String> nonTerminals, Set<String> terminals, String startSymbol) {
        this.nonTerminals = nonTerminals;
        this.terminals = terminals;
        this.startSymbol = startSymbol;
        this.random = new Random();
    }

    public String generateString() {

        return null;
    }

}
