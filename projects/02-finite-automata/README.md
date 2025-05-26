# Finite Automata

## Theory

Finite automata are abstract computational models used to recognize patterns within input strings. They consist of states, transitions, and an initial state, and can be deterministic (DFA) or non-deterministic (NDFA). Finite automata are widely used in lexical analysis, text processing, and formal language theory. They are closely related to regular grammars, as every regular language can be represented by a finite automaton.

## Objectives

1. Parse a finite automaton from a formal definition.
2. Convert a non-deterministic finite automaton (NDFA) to a deterministic finite automaton (DFA).
3. Convert a finite automaton into an equivalent regular grammar.
4. Test the automaton for determinism and other properties.
5. Provide an interactive mode for processing automaton definitions and conversions.

## Features

* **Automaton Parsing**: Parse automaton definitions from a file or string.
* **NDFA to DFA Conversion**: Convert non-deterministic finite automata to deterministic finite automata.
* **Automaton to Grammar Conversion**: Generate an equivalent regular grammar from a finite automaton.
* **Automaton Properties**: Analyze and display properties such as determinism.
* **Interactive Mode**: Process automaton definitions interactively via the console.

## Implementation description

* **Automaton Parsing**: The `AutomatonParser` class parses automaton definitions from a file or string. It validates the components and constructs an `Automaton` object.

* **NDFA to DFA Conversion**: The `AutomatonProcessor` class implements the conversion of non-deterministic finite automata to deterministic finite automata using epsilon-closure and state transitions.

* **Automaton to Grammar Conversion**: The `AutomatonProcessor` class also converts finite automata into equivalent regular grammars by mapping states and transitions to production rules.

* **Interactive Mode**: The `Runner` class provides a console-based interface for parsing, converting, and analyzing automata.

## Example Usage

1. Define the automaton in a file (e.g., `rules.txt`) with the following format:

   ```plaintext
   Q = {q0,q1,q2}
   ∑ = {a,b,c}
   F = {q2}

   δ = {
       (q0,a) = q0,
       (q0,b) = q1,
       (q1,c) = q1,
       (q1,c) = q2,
       (q2,a) = q0,
       (q1,a) = q1
   }
   ```

2. Run the application to parse the automaton, convert it to a DFA, and generate the equivalent regular grammar.

3. View the results in the console output.

## Conclusion

This project demonstrates the practical applications of finite automata in formal language theory and computational models. By parsing, analyzing, and converting automata, it bridges the gap between theoretical concepts and real-world implementations, providing a solid foundation for further exploration in automata theory and its applications.
