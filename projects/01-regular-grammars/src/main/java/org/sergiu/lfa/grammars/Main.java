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

        testUserInput(fa, terminals);
    }

    private static void testUserInput(FiniteAutomaton automaton, Set<String> validSymbols) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nEnter a string to test (or 'exit' to quit): ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("exit")) {
                break;
            }

            boolean validInput = true;
            for (char c : input.toCharArray()) {
                if (!validSymbols.contains(String.valueOf(c))) {
                    System.out.println("Invalid character '" + c + "'. Valid symbols are: " + validSymbols);
                    validInput = false;
                    break;
                }
            }

            if (validInput) {
                boolean accepted = automaton.accepts(input);
                System.out.println("'" + input + "' is " + (accepted ? "ACCEPTED" : "REJECTED") + " by the automaton");
            }
        }

        scanner.close();
        System.out.println("Goodbye!");
    }
}
