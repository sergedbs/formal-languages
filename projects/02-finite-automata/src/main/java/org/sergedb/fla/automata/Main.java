package org.sergedb.fla.automata;

import org.sergedb.fla.automata.parser.AutomatonParser;

public class Main {

    public static final String RULES_FILE_PATH = "projects/02-finite-automata/src/main/resources/rules.txt";

    public static void main(String[] args) {

        // Create the AutomatonParser instance
        AutomatonParser parser = new AutomatonParser();

        // Create the Runner instance with dependency injection
        Runner runner = new Runner(parser);

        // Run the application
        runner.run();
    }
}
