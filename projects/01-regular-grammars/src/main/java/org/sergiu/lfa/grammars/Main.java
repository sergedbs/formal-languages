package org.sergiu.lfa.grammars;

import org.sergiu.lfa.grammars.parser.GrammarParser;

/**
 * Main application entry point for the Regular Grammar Processor.
 * Responsible for bootstrapping the application components and starting the execution.
 */
public class Main {

    /** Default path to the file containing the grammar rules */
    public static final String RULES_FILE_PATH = "projects/01-regular-grammars/src/main/resources/rules.txt";

    /** Regular expression pattern for parsing grammar sections */
    public static final String REGEX_PATTERN = "(?i)%s\\s*=\\s*\\{\\s*((?:[^{}]|[\\r\\n])*)\\s*}";

    /**
     * Application entry point.
     * Creates necessary components, executes the grammar processor, and measures execution time.
     *
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {

        // Create the GrammarParser instance
        GrammarParser parser = new GrammarParser();

        // Create the Runner instance with dependency injection
        Runner runner = new Runner(parser);

        // Execute and measure the run time
        long startTime = System.nanoTime();
        runner.run();
        long stopTime = System.nanoTime();
        System.out.println("\n\nExecution time: " + (stopTime - startTime) / 1_000_000 + "ms");
    }
}