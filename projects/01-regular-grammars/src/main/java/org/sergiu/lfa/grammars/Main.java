package org.sergiu.lfa.grammars;

import java.util.*;

public class Main {
    public static void main(String[] args) {

        Set<String> nonTerminals = new HashSet<>(Arrays.asList("S", "B", "D"));
        Set<String> terminals = new HashSet<>(Arrays.asList("a", "b", "c", "d"));

        List<GrammarRules> rules = new ArrayList<>();
        rules.add(new GrammarRules("S", "a", "S"));
        rules.add(new GrammarRules("S", "b", "B"));
        rules.add(new GrammarRules("B", "c", "B"));
        rules.add(new GrammarRules("B", "d", null));
        rules.add(new GrammarRules("B", "a", "D"));
        rules.add(new GrammarRules("D", "a", "B"));
        rules.add(new GrammarRules("D", "b", null));

        String startSymbol = "S";

        Grammar grammar = new Grammar(nonTerminals, terminals, rules, startSymbol);
    }
}
