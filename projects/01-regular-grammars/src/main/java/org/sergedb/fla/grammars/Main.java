package org.sergedb.fla.grammars;

import org.sergedb.fla.grammars.parser.GrammarParser;

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
     * Creates necessary components and executes the grammar processor.
     *
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {

        // Create the GrammarParser instance
        GrammarParser parser = new GrammarParser();

        // Create the Runner instance with dependency injection
        Runner runner = new Runner(parser);

        // Run the application
        runner.run();
    }
}