package org.sergedb.fla.automata;

import org.sergedb.fla.automata.model.Automaton; // New import
import org.sergedb.fla.automata.parser.AutomatonParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.sergedb.fla.automata.Main.RULES_FILE_PATH;

public class Runner {

    private final AutomatonParser automatonParser;
    private Automaton automaton;

    public Runner(AutomatonParser automatonParser) {
        this.automatonParser = automatonParser;
    }

    public void run() {
        try {
            Path path = Path.of(RULES_FILE_PATH);
            if (!Files.exists(path)) {
                throw new IOException("No automaton found at: " + path);
            }

            automaton = automatonParser.parseFromFile(path);

            System.out.println("--- PARSED AUTOMATON ---");
            System.out.println(automaton.toString());


        } catch (IOException e) {
            System.err.println("Error reading the automaton file: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
