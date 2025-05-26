# Regular Expressions & String Generation

## Theory

Regular expressions are a powerful tool for pattern matching and string manipulation. They are widely used in text processing, validation, and parsing tasks. A regular expression defines a search pattern, which can be used to match strings or parts of strings. This project explores the implementation of a regular expression engine capable of parsing, generating, and testing strings based on given patterns.

## Objectives

1. Understand what regular expressions are and their applications in text processing and pattern matching.
2. Write a program to dynamically generate valid combinations of symbols conforming to given regular expressions.
3. Implement a limit of 5 repetitions for symbols with undefined repetition counts to avoid generating excessively long strings.
4. Provide a detailed sequence of processing steps for each regular expression as a bonus feature.
5. Write a comprehensive report covering all performed actions, faced difficulties, and how the code works.

## Implementation description

- **Regex Parsing**: The `Parser` class converts a regular expression into an abstract syntax tree (AST). It handles constructs like literals, quantifiers (`*`, `+`, `?`, `{m,n}`), alternation (`|`), and grouping (`()`).

```java
public RegexNode parse() {
    if (tokens == null || tokens.isEmpty() || (tokens.size() == 1 && tokens.get(0).type() == TokenType.EOL)) {
        throw new ParseException("Cannot parse an empty regular expression.");
    }
    RegexNode expression = parseExpr();
    if (!isAtEnd()) {
        throw new ParseException("Expected end of expression, but found token: " + peek() + " at position " + current);
    }
    return expression;
}
```

- **String Generation**: The `Generator` class generates random strings based on the AST. It recursively traverses the tree, applying the rules defined by the regex constructs.

```java
public List<String> generate(RegexNode astRoot, int count) {
    if (astRoot == null) {
        return Collections.emptyList();
    }
    Set<String> generatedStringsSet = new HashSet<>();
    allProcessingSteps.clear();

    for (int i = 0; i < count * 5 && generatedStringsSet.size() < count; i++) {
        List<String> currentSteps = new ArrayList<>();
        String generatedString = generateRecursive(astRoot, currentSteps);
        if (generatedString != null && !generatedString.isEmpty()) {
            if (generatedStringsSet.add(generatedString)) {
                allProcessingSteps.put(generatedString, currentSteps);
            }
        }
    }
    return new ArrayList<>(generatedStringsSet);
}
```

- **Tokenization**: The `Lexer` class tokenizes the input regex into meaningful components like literals, operators, and quantifiers.

```java
public List<Token> tokenize() {
    List<Token> tokens = new ArrayList<>();
    while (currentPosition < input.length()) {
        char ch = input.charAt(currentPosition);
        switch (ch) {
            case '(':
                tokens.add(new Token(TokenType.OPEN_PAREN, "("));
                currentPosition++;
                break;
            case ')':
                tokens.add(new Token(TokenType.CLOSE_PAREN, ")"));
                currentPosition++;
                break;
            // ...other cases...
        }
    }
    tokens.add(new Token(TokenType.EOL, ""));
    return tokens;
}
```

- **Main Runner**: The `Main` class orchestrates the execution, including regex parsing, string generation, and displaying results.

```java
public static void main(String[] args) {
    String testFilePath = "projects/04-regular-expressions/src/main/resources/test_inputs.txt";
    int stringsToGenerate = 5;

    System.out.println("--- Processing test_inputs.txt ---");
    try (BufferedReader reader = new BufferedReader(new FileReader(testFilePath))) {
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.trim().isEmpty() || line.startsWith("//")) {
                continue;
            }
            System.out.println("\n--- Regex from file: " + line + " ---");
            processRegex(line, stringsToGenerate);
        }
    } catch (IOException e) {
        System.err.println("Error reading test input file: " + testFilePath);
        e.printStackTrace();
    }
}
```

## Conclusion

This project demonstrates the power and flexibility of regular expressions in string generation and validation. By implementing a custom regex engine, we gained insights into the inner workings of regex parsing and string generation. The ability to handle diverse regex constructs and provide detailed processing steps enhances the usability and robustness of the implementation.
