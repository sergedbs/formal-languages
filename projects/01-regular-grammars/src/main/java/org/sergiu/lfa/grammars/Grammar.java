package org.sergiu.lfa.grammars;

import java.util.*;

public class Grammar {
    private Set<String> nonTerminals;
    private Set<String> terminals;
    private List<GrammarRule> rules;
    private String startSymbol;
    private Random random;

    public Grammar(Set<String> nonTerminals, Set<String> terminals,
                   List<GrammarRule> rules, String startSymbol) {
        this.nonTerminals = nonTerminals;
        this.terminals = terminals;
        this.rules = rules;
        this.startSymbol = startSymbol;
        this.random = new Random();
    }

    public String generateString() {
        StringBuilder result = new StringBuilder();
        String current = startSymbol;

        while (nonTerminals.contains(current)) {
            List<GrammarRule> applicable = new ArrayList<>();
            for (GrammarRule rule : rules) {
                if (rule.getFrom().equals(current)) {
                    applicable.add(rule);
                }
            }

            if (applicable.isEmpty()) {
                break;
            }

            GrammarRule selected = applicable.get(random.nextInt(applicable.size()));

            result.append(selected.getTerminal());

            current = selected.getTo() != null ? selected.getTo() : "";
        }

        return result.toString();
    }

    public FiniteAutomaton toFiniteAutomaton() {
        Set<String> states = new HashSet<>(nonTerminals);
        Set<String> alphabet = new HashSet<>(terminals);
        Map<String, Map<String, String>> transitions = new HashMap<>();
        Set<String> finalStates = new HashSet<>();

        for (String state : states) {
            transitions.put(state, new HashMap<>());
        }

        for (GrammarRule rule : rules) {
            String from = rule.getFrom();
            String symbol = rule.getTerminal();
            String to = rule.getTo();

            if (to != null) {
                transitions.get(from).put(symbol, to);
            } else {
                finalStates.add(from);
                transitions.get(from).put(symbol, "__FINAL__");
            }
        }

        System.out.println("Final states: " + finalStates);
        return new FiniteAutomaton(states, alphabet, transitions, startSymbol, finalStates);
    }

}
