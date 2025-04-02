package org.sergiu.lfa.grammars;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Main {


    public static final String RULES_FILE_PATH = "projects/01-regular-grammars/src/main/resources/rules.txt";
    public static final String REGEX_PATTERN = "(?i)%s\\s*=\\s*\\{\\s*((?:[^{}]|[\\r\\n])*)\\s*}";

    static Runner runner = new Runner();

    public static void main(String[] args) {
        runner.run();
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