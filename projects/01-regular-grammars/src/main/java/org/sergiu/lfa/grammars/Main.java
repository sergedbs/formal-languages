package org.sergiu.lfa.grammars;

import java.util.*;

public class Main {
    public static void main(String[] args) {

        Set<String> nonTerminals = new HashSet<>(Arrays.asList("S", "B", "D"));
        Set<String> terminals = new HashSet<>(Arrays.asList("a", "b", "c", "d"));

        List<GrammarRule> rules = new ArrayList<>();
        rules.add(new GrammarRule("S", "a", "S"));
        rules.add(new GrammarRule("S", "b", "B"));
        rules.add(new GrammarRule("B", "c", "B"));
        rules.add(new GrammarRule("B", "d", null));
        rules.add(new GrammarRule("B", "a", "D"));
        rules.add(new GrammarRule("D", "a", "B"));
        rules.add(new GrammarRule("D", "b", null));

        String startSymbol = "S";

        Grammar grammar = new Grammar(nonTerminals, terminals, rules, startSymbol);

        System.out.println("Generated strings:");
        for (int i = 0; i < 5; i++) {
            System.out.println("  → " + grammar.generateString());
        }
    }
}
