package org.sergedb.fla.chomsky;

import org.sergedb.fla.chomsky.model.Grammar;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("No grammar file path provided, attempting to use default: src/main/resources/rules.txt");
            runConversion("projects/05-chomsky-normal-form/src/main/resources/rules.txt");
            return;
        }
        runConversion(args[0]);
    }

    private static void runConversion(String filePath) {
        try {
            System.out.println("Reading grammar from: " + filePath);
            String grammarDefinition = new String(Files.readAllBytes(Paths.get(filePath)));
            
            GrammarParser parser = new GrammarParser();
            Grammar grammar = parser.parseFromString(grammarDefinition);
            
            System.out.println("Successfully parsed grammar.");
            // System.out.println("Initial Grammar:");
            // System.out.println(grammar);

            CNF cnfConverter = new CNF();
            Grammar cnfGrammar = cnfConverter.convertToCNF(grammar);

            // System.out.println("Final CNF Grammar (from Main):");
            // System.out.println(cnfGrammar);

        } catch (IOException e) {
            System.err.println("Error reading grammar file: " + filePath);
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            System.err.println("Error parsing grammar definition:");
            e.printStackTrace();
        }
    }
}
