# Regular Grammars & Finite Automata

## Theory

Regular grammars are a subset of formal grammars used to describe regular languages. They consist of production rules that define how terminal and non-terminal symbols can be combined to form strings in the language. Finite automata are computational models that recognize regular languages by processing input strings through states and transitions. This project demonstrates the equivalence between regular grammars and finite automata.

## Objectives

* Parse a regular grammar from a formal definition.
* Generate random strings that conform to the grammar.
* Construct a finite automaton from the grammar.
* Test strings for acceptance by the finite automaton.
* Provide an interactive mode for user-defined string testing.

## Features

* **Grammar Parsing**: Parse grammars defined in a formal notation.
* **String Generation**: Generate random strings that conform to the grammar rules.
* **Finite Automaton Construction**: Convert a regular grammar into a finite automaton.
* **String Testing**: Test strings against the finite automaton for acceptance.
* **Interactive Mode**: Test custom strings interactively via the console.

## Implementation description

* **Grammar Parsing**: The `GrammarParser` class parses grammar definitions from a file or string. It validates the grammar components and constructs a `Grammar` object.

  ```java
  public Grammar parseFromFile(Path filePath) throws IOException {
  }
  ```

* **String Generation**: The `GrammarProcessor` class generates random strings by recursively applying production rules starting from the start symbol.

  ```java
  public String generateString() {
  }
  ```

* **Finite Automaton Construction**: The `FiniteAutomaton` class converts the grammar into a finite automaton by mapping production rules to states and transitions.

  ```java
  public FiniteAutomaton(Grammar grammar) {
  }
  ```

* **Interactive String Testing**: The `StringTester` class provides a console-based interface for testing strings against the finite automaton.

  ```java
  public void startInteractiveMode() {
  }
  ```

* **Main Runner**: The `Runner` class orchestrates the execution, including grammar parsing, string generation, automaton construction, and interactive testing.

  ```java
  public void run() {
  }
  ```

## Grammar Format

The grammar is defined in a text file using the following format:

```text
V_N={S, A, B, ...}     // Non-terminal symbols
V_T={a, b, c, ...}     // Terminal symbols
P={ S -> aA | bB,      // Production rules
    A -> c | ε,
    B -> d
}
```

* `V_N`: Set of non-terminal symbols.
* `V_T`: Set of terminal symbols.
* `P`: Set of production rules.
* `S`: Start symbol (must be included in `V_N`).

## Project Structure

* **`src/main/java`**: Contains the main application code.
  * `parser`: Parses grammar definitions.
  * `processor`: Processes grammars for string generation.
  * `automaton`: Constructs finite automata from grammars.
  * `interactive`: Provides an interactive console for string testing.
* **`src/test/java`**: Contains unit tests for the project.
* **`src/main/resources`**: Contains the grammar definition file (`rules.txt`).

## Example Output

### Parsed Grammar

```text
Non-terminals: [S, B, D]
Terminals: [a, b, c, d]
Start symbol: S
Rules:
    S --(a)--> S
    S --(b)--> B
    B --(c)--> B
    B --(d)--> ε
    B --(a)--> D
    D --(a)--> B
    D --(b)--> ε
```

### Generated Strings

```text
--- GENERATED STRINGS ---
ab
abcd
bd
```

### Finite Automaton

```text
State Transitions:
  δ(S,a) = S
  δ(S,b) = B
  δ(B,c) = B
  δ(B,d) = B_final
  δ(B,a) = D
  δ(D,a) = B
  δ(D,b) = D_final
Final states (F): [B_final, D_final]
```

### String Testing

```text
String "ab" is ACCEPTED by the automaton
String "abcd" is ACCEPTED by the automaton
String "bd" is ACCEPTED by the automaton
```

## Conclusion

Regular grammars and finite automata are equivalent models for representing regular languages, as demonstrated in this project through parsing, string generation, and testing. The interactive mode further enhances user engagement by enabling custom string testing, showcasing the practical applications of these concepts.
