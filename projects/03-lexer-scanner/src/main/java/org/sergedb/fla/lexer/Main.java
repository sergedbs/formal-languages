package org.sergedb.fla.lexer;

import org.sergedb.fla.lexer.model.Token;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String testFilePath = "projects/03-lexer-scanner/src/main/resources/test_inputs.txt"; // Relative path to the test file

        System.out.println("--- Processing test_inputs.txt ---");
        try (BufferedReader reader = new BufferedReader(new FileReader(testFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("//")) { // Skip empty lines and comments
                    continue;
                }
                System.out.println("\nInput: " + line);
                Lexer lexer = new Lexer(line);
                List<Token> tokens = lexer.tokenize();

                System.out.println("Tokens:");
                for (Token token : tokens) {
                    System.out.println("  " + token);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading test input file: " + testFilePath);
            e.printStackTrace();
        }
        System.out.println("\n--- Starting Interactive Mode ---");
        Input inputHandler = new Input();
        inputHandler.runInteractiveMode();
    }
}