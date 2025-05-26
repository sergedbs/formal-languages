package org.sergedb.fla.lexer;

import org.sergedb.fla.lexer.model.Token;

import java.util.List;
import java.util.Scanner;

public class Input {
    public void runInteractiveMode() {
        Scanner scanner = new Scanner(System.in);
        String line;
        System.out.println("Enter expressions to tokenize (type 'exit' to quit):");
        while (true) {
            System.out.print("> ");
            line = scanner.nextLine();
            if ("exit".equalsIgnoreCase(line)) {
                break;
            }
            try {
                Lexer lexer = new Lexer(line);
                List<Token> tokens = lexer.tokenize();
                System.out.println("Tokens:");
                for (Token token : tokens) {
                    System.out.println("  " + token);
                }
            } catch (Exception e) {
                System.err.println("Error processing input: " + e.getMessage());
            }
        }
        scanner.close();
        System.out.println("Exiting interactive mode.");
    }
}
