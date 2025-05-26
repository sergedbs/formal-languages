package org.sergedb.fla.grammars.interactive;

import org.sergedb.fla.grammars.automaton.FiniteAutomaton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Interactive console interface for testing strings against a finite automaton.
 * <p>
 * This class provides a command-line interface that allows users to:
 * <ul>
 *   <li>Enter strings to test against the automaton</li>
 *   <li>View detailed transition steps during string processing</li>
 *   <li>Continue testing multiple strings in a loop until exiting</li>
 * </ul>
 * <p>
 * The user can exit the interactive mode by entering "exit;" as input.
 */
public class StringTester {
    private static final String EXIT_COMMAND = "exit;";
    private final FiniteAutomaton automaton;
    private final Scanner scanner;

    /**
     * Creates a new string tester for the given automaton.
     *
     * @param automaton The automaton to test strings against
     */
    public StringTester(FiniteAutomaton automaton) {
        this.automaton = automaton;
        this.scanner = new Scanner(System.in);
    }

    /**
     * Starts the interactive testing loop.
     * <p>
     * Prompts the user for input strings, tests them against the automaton,
     * and displays detailed transition steps.
     */
    public void startInteractiveMode() {
        System.out.println("\n=== Interactive String Testing Mode ===");
        System.out.println("Enter strings to test or '" + EXIT_COMMAND + "' to quit");

        while (true) {
            System.out.print("\nEnter a string: ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase(EXIT_COMMAND)) {
                System.out.println("Exiting interactive mode");
                break;
            }

            testStringWithTracing(input);
        }
    }

    /**
     * Tests a string against the automaton and displays each transition step.
     *
     * @param input The string to test
     */
    private void testStringWithTracing(String input) {
        if (input == null || input.isEmpty()) {
            System.out.println("Empty string provided");
            return;
        }

        System.out.println("\nProcessing string: \"" + input + "\"");
        System.out.println("Starting state: " + automaton.getStartState());

        String currentState = automaton.getStartState();
        boolean accepted = true;
        List<TransitionStep> steps = new ArrayList<>();

        // Process each character and record transitions
        for (int i = 0; i < input.length(); i++) {
            String symbol = String.valueOf(input.charAt(i));

            TransitionResult result = processTransition(currentState, symbol);
            steps.add(new TransitionStep(i, currentState, symbol, result));

            if (!result.isValid()) {
                accepted = false;
                break;
            }

            currentState = result.nextState();
        }

        // Print transition table
        printTransitionTable(steps);

        // Final acceptance check
        if (accepted && !automaton.getFinalStates().contains(currentState)) {
            accepted = false;
            System.out.println("Stopped in non-final state: " + currentState);
        }

        // Print final result
        System.out.println("\nResult: String \"" + input + "\" is " +
                (accepted ? "ACCEPTED" : "REJECTED") + " by the automaton");
    }

    /**
     * Processes a single transition step.
     *
     * @param state  Current state
     * @param symbol Input symbol
     * @return Transition result containing validity and next state
     */
    private TransitionResult processTransition(String state, String symbol) {
        // Check if symbol is in alphabet
        if (!automaton.getAlphabet().contains(symbol)) {
            return new TransitionResult(false, null, "Symbol not in alphabet");
        }

        // Get transitions map for state
        Map<String, String> transitions = getTransitionsForState(state);

        // Check if transition exists
        if (transitions == null || !transitions.containsKey(symbol)) {
            return new TransitionResult(false, null, "No transition defined");
        }

        // Return next state
        String nextState = transitions.get(symbol);
        return new TransitionResult(true, nextState, "Transition found");
    }

    /**
     * Gets the transition map for a specific state.
     * <p>
     * This is a workaround since we don't have direct access to the transition
     * function in FiniteAutomaton. In a production system, this would be replaced
     * with a proper accessor method in FiniteAutomaton.
     *
     * @param state The state to get transitions for
     * @return Map of transitions or null if not found
     */
    private Map<String, String> getTransitionsForState(String state) {
        // This is a limitation since we don't have direct access to the delta map
        // In a production system, FiniteAutomaton should provide a getter for this
        return automaton.getTransitionMapForState(state);
    }

    /**
     * Prints a formatted table of transition steps.
     *
     * @param steps List of transition steps to print
     */
    private void printTransitionTable(List<TransitionStep> steps) {
        System.out.println("\nTransition Steps:");
        System.out.println("+---------+--------------+---------+--------------+------------------+");
        System.out.println("| Step    | Current State| Symbol  | Next State   | Status           |");
        System.out.println("+---------+--------------+---------+--------------+------------------+");

        for (TransitionStep step : steps) {
            TransitionResult result = step.result();
            String nextState = result.isValid() ? result.nextState() : "ERROR";
            String status = result.isValid() ? "Valid" : "Invalid: " + result.message();

            System.out.printf("| %-7d | %-12s | %-7s | %-12s | %-16s |%n",
                    step.position() + 1,
                    step.state(),
                    step.symbol(),
                    nextState,
                    status);
        }

        System.out.println("+---------+--------------+---------+--------------+------------------+");
    }
}