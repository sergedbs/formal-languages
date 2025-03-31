package org.sergiu.lfa.grammars;

import java.util.*;
import java.util.stream.Collectors;

public class Grammar {
    private final Set<String> nonTerminals;
    private final Set<String> terminals;
    private final List<GrammarRule> rules;
    private final String startSymbol;
    private final Random random;

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
            String finalCurrent = current;
            List<GrammarRule> applicable = rules.stream()
                    .filter(rule -> rule.from().equals(finalCurrent))
                    .toList();

            if (applicable.isEmpty()) {
                break;
            }

            GrammarRule selected = applicable.get(random.nextInt(applicable.size()));
            result.append(selected.terminal());
            current = selected.to() != null ? selected.to() : "";
        }

        return result.toString();
    }

    public FiniteAutomaton toFiniteAutomaton() {
        Set<String> states = new HashSet<>(nonTerminals);
        Set<String> alphabet = new HashSet<>(terminals);
        Map<String, Map<String, String>> transitions = states.stream()
                .collect(Collectors.toMap(state -> state, state -> new HashMap<>()));
        Set<String> finalStates = new HashSet<>();

        for (GrammarRule rule : rules) {
            String from = rule.from();
            String symbol = rule.terminal();
            String to = rule.to();

            if (to != null) {
                transitions.get(from).put(symbol, to);
            } else {
                finalStates.add(from);
                transitions.get(from).put(symbol, "ε");
            }
        }
        return new FiniteAutomaton(states, alphabet, transitions, startSymbol, finalStates);
    }
}
