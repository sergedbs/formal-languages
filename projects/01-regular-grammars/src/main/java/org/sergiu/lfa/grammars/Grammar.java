package org.sergiu.lfa.grammars;

import java.util.*;
import java.util.stream.Collectors;

public class Grammar {
    private final Set<String> nonTerminals;
    private final Set<String> terminals;
    private final List<GrammarRule> rules;
    private final String startSymbol;
    private final Random random;

    // private final Map<String, List<GrammarRule>> rulesByFrom;

    public Grammar(Set<String> nonTerminals, Set<String> terminals,
                  List<GrammarRule> rules, String startSymbol) {
        Objects.requireNonNull(nonTerminals, "Non-terminals cannot be null");
        Objects.requireNonNull(terminals, "Terminals cannot be null");
        Objects.requireNonNull(rules, "Rules cannot be null");
        Objects.requireNonNull(startSymbol, "Start symbol cannot be null");

        this.nonTerminals = new HashSet<>(nonTerminals);
        this.terminals = new HashSet<>(terminals);
        this.rules = new ArrayList<>(rules);
        this.startSymbol = startSymbol;
        this.random = new Random();

/*        // Pre-compute rules by non-terminal for faster access
        this.rulesByFrom = rules.stream()
                .collect(Collectors.groupingBy(GrammarRule::from));*/
    }

    public Set<String> getNonTerminals() {
        return Collections.unmodifiableSet(nonTerminals);
    }

    public Set<String> getTerminals() {
        return Collections.unmodifiableSet(terminals);
    }

    public List<GrammarRule> getRules() {
        return Collections.unmodifiableList(rules);
    }

    public String getStartSymbol() {
        return startSymbol;
    }

/*    public String generateString() {
        StringBuilder result = new StringBuilder();
        String current = startSymbol;

        int maxIterations = 100;
        int iterations = 0;

        while (nonTerminals.contains(current) && iterations < maxIterations) {
            List<GrammarRule> applicable = rulesByFrom.getOrDefault(current, Collections.emptyList());

            if (applicable.isEmpty()) {
                break;
            }

            GrammarRule selected = applicable.get(random.nextInt(applicable.size()));
            result.append(selected.terminal());
            current = selected.to() != null ? selected.to() : "";
            iterations++;
        }

        return result.toString();
    }*/

/*    public FiniteAutomaton toFiniteAutomaton() {
        Set<String> states = new HashSet<>(nonTerminals);
        Set<String> alphabet = new HashSet<>(terminals);
        Map<String, Map<String, String>> transitions = new HashMap<>();
        Set<String> finalStates = new HashSet<>();

        // Initialize transitions map for all states
        for (String state : states) {
            transitions.put(state, new HashMap<>());
        }

        // Build transitions from grammar rules
        for (GrammarRule rule : rules) {
            String from = rule.from();
            String symbol = rule.terminal();
            String to = rule.to();

            // If there's no "to" state, it means this is a terminal rule
            if (to == null) {
                transitions.get(from).put(symbol, "ε");
                finalStates.add(from);
            } else {
                transitions.get(from).put(symbol, to);
            }
        }

        // Add start state to final states if it can produce ε
        if (canProduceEmptyString(startSymbol, new HashSet<>())) {
            finalStates.add(startSymbol);
        }

        return new FiniteAutomaton(states, alphabet, transitions, startSymbol, finalStates);
    }


    private boolean canProduceEmptyString(String symbol, Set<String> visited) {
        if (visited.contains(symbol)) {
            return false;
        }

        visited.add(symbol);

        List<GrammarRule> applicable = rulesByFrom.getOrDefault(symbol, Collections.emptyList());

        for (GrammarRule rule : applicable) {
            if (rule.to() == null) {
                return true;
            }
        }

        return false;
    } */
}