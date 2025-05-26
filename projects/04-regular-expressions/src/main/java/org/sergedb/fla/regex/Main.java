package org.sergedb.fla.regex;

import org.sergedb.fla.regex.model.Token;
import org.sergedb.fla.regex.model.TokenType;
import org.sergedb.fla.regex.Parser.RegexNode; 

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        String testFilePath = "projects/04-regular-expressions/src/main/resources/test_inputs.txt"; 
        int stringsToGenerate = 5; 

        System.out.println("--- Processing test_inputs.txt ---");
        try (BufferedReader reader = new BufferedReader(new FileReader(testFilePath))) {
            String line;
            boolean foundRegex = false;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("//")) { 
                    continue;
                }
                foundRegex = true;
                System.out.println("\n--- Regex from file: " + line + " ---");
                processRegex(line, stringsToGenerate);
            }
            if (!foundRegex) {
                System.out.println("No regex patterns found in " + testFilePath);
            }
        } catch (IOException e) {
            System.err.println("Error reading test input file: " + testFilePath);
            e.printStackTrace();
        }

        System.out.println("\n--- All regex patterns from file processed. Exiting application. ---");
    }

    private static void processRegex(String regex, int count) {
        try {
            System.out.println("Input Regex: " + regex);

            
            Lexer lexer = new Lexer(regex);
            List<Token> tokens = lexer.tokenize();
            System.out.println("Tokens: ");
            for(Token token : tokens) {
                System.out.print(token + " ");
            }
            System.out.println();

            
            Parser parser = new Parser(tokens);
            RegexNode ast = parser.parse();
            System.out.println("AST: " + ast.toString());

            
            Generator generator = new Generator(); 
            List<String> generatedStrings = generator.generate(ast, count);

            if (generatedStrings.isEmpty()) {
                System.out.println("No strings were generated. The regex might be very restrictive, produce only empty string, or there could be an issue.");
                 
                if (generator.getAllProcessingSteps().containsKey("")) {
                    System.out.println("Generated string: (empty string)");
                    System.out.println("Processing steps for empty string:");
                    for (String step : generator.getAllProcessingSteps().get("")) {
                        System.out.println("  - " + step);
                    }
                }
            } else {
                System.out.println("Generated strings (" + generatedStrings.size() + "):");
                for (String str : generatedStrings) {
                    System.out.println("  \"" + str + "\"");
                }

                Map<String, List<String>> allSteps = generator.getAllProcessingSteps();
                System.out.println("\nProcessing steps for each generated string:");
                for (Map.Entry<String, List<String>> entry : allSteps.entrySet()) {
                    System.out.println("\nString: \"" + entry.getKey() + "\"");
                    System.out.println("Steps:");
                    for (String step : entry.getValue()) {
                        System.out.println("  - " + step);
                    }
                }
            }

        } catch (Parser.ParseException e) {
            System.err.println("Parser Error: " + e.getMessage());
            e.printStackTrace(); 
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("-------------------------------------");
    }
}
