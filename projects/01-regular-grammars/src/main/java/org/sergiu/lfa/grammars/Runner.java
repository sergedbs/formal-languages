package org.sergiu.lfa.grammars;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.sergiu.lfa.grammars.Main.RULES_FILE_PATH;

/**
 * Orchestrates the overall execution flow for the grammar processor.
 * Responsible for loading the grammar, processing it, and displaying results.
 */
public class Runner {
    private final GrammarParser grammarParser;

    /**
     * Creates a new Runner with the specified grammar parser.
     *
     * @param grammarParser The parser to use for grammar processing
     */
    public Runner(GrammarParser grammarParser) {
        this.grammarParser = grammarParser;
    }

    /**
     * Executes the main workflow:
     * 1. Reads the grammar from a file
     * 2. Parses the grammar
     * 3. Processes it to generate strings
     * 4. Displays the results
     * Handles errors that might occur during processing.
     */
    public void run() {
        try {
            Path path = Path.of(RULES_FILE_PATH);
            if (!Files.exists(path)) {
                throw new IOException("No grammar found at: " + path);
            }
            Grammar grammar = grammarParser.parseFromFile(path);
            GrammarProcessor grammarProcessor = new GrammarProcessor(grammar);

            System.out.println("--- PARSED GRAMMAR ---");
            System.out.println(grammar.toString());

            System.out.println("\n--- GENERATED STRINGS ---");
            for (int i = 0; i < 5; i++) {
                String test = grammarProcessor.generateString();
                System.out.println(test);
            }

            // System.out.println("\n--- FINITE AUTOMATON ---");

        } catch (IOException e) {
            System.err.println("Error processing grammar file: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

