package org.sergedb.fla.parser;

import org.sergedb.fla.parser.model.ASTNode;
import org.sergedb.fla.parser.model.Token;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter an expression:");
        String input = scanner.nextLine();

        Lexer lexer = new Lexer(input);
        List<Token> tokens = lexer.scanTokens();

        System.out.println("Tokens:");
        for (Token token : tokens) {
            System.out.println(token);
        }

        Parser parser = new Parser(tokens);
        try {
            ASTNode ast = parser.parse();
            System.out.println("\nAST:");
            if (ast != null) {
                ast.print("", true);
            }
        } catch (Parser.ParseException e) {
            System.err.println("Parsing error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }

        scanner.close();
    }
}
