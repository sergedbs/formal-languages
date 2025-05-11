package org.sergedb.fla.grammars;

import org.sergedb.fla.grammars.automaton.FiniteAutomaton;
import org.sergedb.fla.grammars.interactive.StringTester;
import org.sergedb.fla.grammars.model.Grammar;
import org.sergedb.fla.grammars.parser.GrammarParser;
import org.sergedb.fla.grammars.processor.GrammarProcessor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.sergedb.fla.grammars.Main.RULES_FILE_PATH;

public class Runner {
    private final GrammarParser grammarParser;
    private List<String> generatedStrings;
    private Grammar grammar;
    private FiniteAutomaton automaton;

    public Runner(GrammarParser grammarParser) {
        this.grammarParser = grammarParser;
        this.generatedStrings = new ArrayList<>();
    }

    public void run() {
        try {
            long startTime = System.nanoTime();

            Path path = Path.of(RULES_FILE_PATH);
            if (!Files.exists(path)) {
                throw new IOException("No grammar found at: " + path);
            }
            grammar = grammarParser.parseFromFile(path);
            GrammarProcessor grammarProcessor = new GrammarProcessor(grammar);

            System.out.println("--- PARSED GRAMMAR ---");
            System.out.println(grammar.toString());

            System.out.println("\n--- GENERATED STRINGS ---");
            // Generate strings and store them in the list
            generatedStrings = generateStrings(grammarProcessor, 5);

            // Print the generated strings
            for (String str : generatedStrings) {
                System.out.println(str);
            }

            System.out.println("\n--- FINITE AUTOMATON ---");
            automaton = new FiniteAutomaton(grammar);

            // Print transitions
            automaton.printTransitions();

            // Test all stored strings with the automaton
            testAllGeneratedStrings();

            // Generate accepted strings up to length 5
            // System.out.println("\nAccepted Strings (length <= 5): " + automaton.getAcceptedStrings(5));


            long stopTime = System.nanoTime();
            System.out.println("\n\nExecution time: " + (stopTime - startTime) / 1_000_000 + "ms");

            // Start interactive testing mode
            System.out.print("\nDo you want to test your own strings? (y/n): ");
            try (Scanner scanner = new Scanner(System.in)) {
                String response = scanner.nextLine().trim().toLowerCase();
                
                if (response.equals("y") || response.equals("yes")) {
                    StringTester tester = new StringTester(automaton);
                    tester.startInteractiveMode();
                }
            }

        } catch (IOException e) {
            System.err.println("Error processing grammar file: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Generates a specified number of strings using the grammar processor.
     *
     * @param processor the grammar processor
     * @param count number of strings to generate
     * @return list of generated strings
     */
    private List<String> generateStrings(GrammarProcessor processor, int count) {
        List<String> strings = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            strings.add(processor.generateString());
        }
        return strings;
    }

    /**
     * Tests all the generated strings against the finite automaton.
     */
    private void testAllGeneratedStrings() {
        System.out.println("\n--- TESTING GENERATED STRINGS ---");
        for (String str : generatedStrings) {
            boolean accepted = automaton.accepts(str);
            System.out.printf("String \"%s\" is %s by the automaton%n",
                    str, accepted ? "ACCEPTED" : "REJECTED");
        }
    }

    /**
     * Gets the list of generated strings.
     *
     * @return the list of generated strings
     */
    public List<String> getGeneratedStrings() {
        return new ArrayList<>(generatedStrings);
    }

    /**
     * Gets the grammar used in this run.
     *
     * @return the grammar
     */
    public Grammar getGrammar() {
        return grammar;
    }

    /**
     * Gets the automaton created from the grammar.
     *
     * @return the finite automaton
     */
    public FiniteAutomaton getAutomaton() {
        return automaton;
    }
}