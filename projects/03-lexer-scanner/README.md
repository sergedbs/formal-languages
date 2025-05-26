# Regular Grammars and Finite Automata

## Theory

Lexical analysis is a fundamental step in processing structured text formats, such as programming languages and markup languages. It involves converting a raw sequence of characters into meaningful components called **tokens**. This process is performed by a **lexer** (or tokenizer), which simplifies subsequent stages like parsing by breaking down the input into structured elements. Additionally, lexical analysis removes unnecessary characters like whitespace and comments, and detects malformed sequences early, preventing errors in later stages.

A **lexeme** is a sequence of characters that forms a meaningful unit based on predefined rules, such as keywords, operators, or identifiers. For example, in the statement `int x = 10;`, the lexemes include `int`, `x`, `=`, `10`, and `;`. These lexemes are categorized into **tokens**, which consist of a type (e.g., keyword, operator, number) and optional metadata (e.g., the value of a number). The lexer uses rules, often defined by regular expressions or finite state machines, to identify and classify these lexemes into tokens, enabling structured and error-free processing of the input.

## Objectives

1. Understand what lexical analysis is.
2. Get familiar with the inner workings of a lexer/scanner/tokenizer.
3. Implement a sample lexer for a calculator and show how it works.

## Features

* **Tokenization**: Converts a string of characters into a sequence of tokens.
* **Supported Tokens**:
  * `NUMBER`: Integers (e.g., `42`) and floating-point numbers (e.g., `3.14`).
  * `PLUS`: `+`
  * `MINUS`: `-`
  * `MULTIPLY`: `*`
  * `DIVIDE`: `/`
  * `LPAREN`: `(`
  * `RPAREN`: `)`
  * `SIN`: `sin` keyword for the sine function.
  * `COS`: `cos` keyword for the cosine function.
  * `TAN`: `tan` keyword for the tangent function.
  * `POWER`: `^` for exponentiation.
  * `EOL`: End of Line/Input.
  * `INVALID`: Any character or sequence not recognized.
* **Whitespace Handling**: Skips whitespace characters (spaces, tabs).
* **File-based Testing**: Reads expressions from `src/main/resources/test_inputs.txt` and processes them.
* **Interactive Mode**: Allows users to input expressions directly into the console for tokenization.

## Implementation description

### The `matchNumber` method

This method identifies numeric values in the input, including integers and floating-point numbers. It begins by checking if the current character is a digit. If not, it immediately returns `null`. The method then iterates through the input, tracking digits and a single decimal point to ensure valid number formatting. Once the number is fully parsed, it extracts and returns the substring representing the number.

### The `matchKeyword` method

This method detects specific keywords, such as `sin`, `cos`, and `tan`, which represent trigonometric functions. It compares the current input substring to the expected keyword and ensures it is not part of a longer identifier. If a match is found, the keyword is returned; otherwise, the method returns `null`.

### The `getNextToken` method

This method orchestrates the tokenization process by identifying the next token in the input. It skips whitespace, checks for numbers using `matchNumber`, and matches keywords like trigonometric functions. If no match is found, it processes single-character symbols (e.g., `+`, `-`, `*`) or flags unrecognized characters as invalid tokens.

### The `tokenize` method

This method generates a list of tokens from the input string. It resets the input position and repeatedly calls `getNextToken` until the end of the input is reached. Each identified token is added to a list, which is returned as the final output, representing the tokenized structure of the input.

### The `skipWhitespace` method

This utility method advances the input position past any whitespace characters, ensuring that spaces and tabs do not interfere with token recognition. It simplifies the tokenization process by focusing only on meaningful characters.

### The `getCurrentChar` method

This method retrieves the character at the current input position, ensuring safe access within bounds. If the position exceeds the input length, it returns a null character, signaling the end of the input.

### The `TokenType` Enum

The `TokenType` enum defines the categories of tokens the lexer can recognize, such as `NUMBER`, `PLUS`, `MINUS`, `SIN`, and `INVALID`. These types standardize the classification of input elements, enabling consistent tokenization and processing.

## How to Build

This project uses Maven. To build the project, navigate to the `03-lexer-scanner` directory in your terminal and run:

```bash
mvn clean package
```

This will compile the source code and create a JAR file in the `target` directory.

## How to Run

After building the project, you can run the application using the following Maven command from the `03-lexer-scanner` directory:

```bash
mvn exec:java -Dexec.mainClass="org.sergedb.fla.lexer.Main"
```

Alternatively, if you have built the JAR file, you can run it directly (the exact command might vary based on your JAR configuration in `pom.xml`):

```bash
java -cp target/03-lexer-scanner.jar org.sergedb.fla.lexer.Main
```

(Replace `03-lexer-scanner.jar` with the actual name of the generated JAR file).

## Example Usage

Upon running, the application will:

1. First, process all expressions listed in `src/main/resources/test_inputs.txt`, printing the input string and the list of tokens generated for each.
2. Then, it will enter an interactive mode:
   * It will display the prompt: `Enter expressions to tokenize (type 'exit' to quit):`
   * You can type an expression and press Enter.
   * The lexer will tokenize your input, and the application will print the list of tokens.
   * Type `exit` to quit the interactive mode and terminate the application.

**From `test_inputs.txt`:**

If `test_inputs.txt` contains:

```plaintext
3.14 + 2
sin(0.5)
```

The initial output might look like:

```text
--- Processing test_inputs.txt ---

Input: 3.14 + 2
Tokens:
  Token(NUMBER, 3.14)
  Token(PLUS, +)
  Token(NUMBER, 2)

Input: sin(0.5)
Tokens:
  Token(SIN, sin)
  Token(LPAREN, ()
  Token(NUMBER, 0.5)
  Token(RPAREN, ))

--- Starting Interactive Mode ---
Enter expressions to tokenize (type 'exit' to quit):
>
```

**Interactive Mode:**

```text
> 10 * (2 + cos(0))
Tokens:
  Token(NUMBER, 10)
  Token(MULTIPLY, *)
  Token(LPAREN, ()
  Token(NUMBER, 2)
  Token(PLUS, +)
  Token(COS, cos)
  Token(LPAREN, ()
  Token(NUMBER, 0)
  Token(RPAREN, ))
  Token(RPAREN, ))
> exit
Exiting interactive mode.
```

## Conclusions

This lab provided valuable insights into lexical analysis and the implementation of a basic lexer for a calculator. It highlighted the importance of correctly parsing input and handling diverse token types, which required careful design and attention to detail. Overcoming challenges in tokenization deepened my understanding of how lexers process input streams into meaningful tokens, reinforcing the critical role of lexical analysis in structured text processing.
