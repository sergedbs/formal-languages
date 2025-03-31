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
        FiniteAutomaton fa = grammar.toFiniteAutomaton();

        fa.printTransitions();

        System.out.println("\nTesting generated strings:");
        for (int i = 0; i < 5; i++) {
            String test = grammar.generateString();
            System.out.println(test + " -> " + (fa.accepts(test) ? "ACCEPTED" : "REJECTED"));
        }
    }
}
