package org.sergiu.lfa.grammars;

import java.io.IOException;
import java.util.*;

public class Main {

    static Testing testing = new Testing();

    public static void main(String[] args) {
        try {
            String filePath = "rules.txt";

            if (args.length > 0) {
                filePath = args[0];
            }

            testing.test();

            System.out.println("--- PARSED GRAMMAR ---");
            // GrammarParser.printGrammar(grammar);

            System.out.println("\n--- FINITE AUTOMATON ---");
/*            fa.printTransitions();

            testGeneratedStrings(grammar, fa);

            testUserInput(fa, grammar.getTerminals());*/

        } catch (IOException e) {
            System.err.println("Error processing grammar file: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }

/*    private static void testGeneratedStrings(Grammar grammar, FiniteAutomaton automaton) {
        System.out.println("\nTESTING GENERATED STRINGS:");
        for (int i = 0; i < 5; i++) {
            String test = grammar.generateString();
            boolean accepted = automaton.accepts(test);
            System.out.printf("String: %s -> %s%n", test, accepted ? "ACCEPTED" : "REJECTED");
        }
    }

    private static void testUserInput(FiniteAutomaton automaton, Set<String> validSymbols) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("\nUSER INPUT TESTING:");
            System.out.println("Valid symbols: " + String.join(", ", validSymbols));

            while (true) {
                System.out.print("\nEnter a string to test (or 'exit' to quit): ");
                String input = scanner.nextLine().trim();

                if (input.equalsIgnoreCase("exit")) {
                    break;
                }

                if (validateInput(input, validSymbols)) {
                    boolean accepted = automaton.accepts(input);
                    System.out.printf("'%s' is %s by the automaton%n",
                            input, accepted ? "ACCEPTED" : "REJECTED");
                }
            }

            System.out.println("Goodbye!");
        }
    }*/

    private static boolean validateInput(String input, Set<String> validSymbols) {
        if (input.isEmpty()) {
            System.out.println("Please enter a non-empty string.");
            return false;
        }

        for (char c : input.toCharArray()) {
            String symbol = String.valueOf(c);
            if (!validSymbols.contains(symbol)) {
                System.out.printf("Invalid character '%s'. Valid symbols are: %s%n",
                        symbol, String.join(", ", validSymbols));
                return false;
            }
        }

        return true;
    }
}