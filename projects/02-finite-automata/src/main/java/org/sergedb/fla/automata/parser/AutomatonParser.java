package org.sergedb.fla.automata.parser;

import org.sergedb.fla.automata.model.Automaton;
import org.sergedb.fla.automata.model.Transition;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Parses finite automaton definitions from text into structured {@link Automaton} objects.
 * <p>
 * This parser understands automaton definitions in the following format:
 * <pre>
 * Q = {q0, q1, q2, ...}     // Set of states
 * ∑ = {a, b, c, ...}       // Alphabet (input symbols)
 * F = {q2, ...}            // Set of final states
 * delta = {                // Transition function
 *     (q0,a) = q0,
 *     (q0,b) = q1,
 *     // ... other transitions ...
 * }
 * </pre>
 * The start state is assumed to be 'q0' if present, or the first state listed in Q otherwise.
 * Epsilon transitions can be defined using "ε" or "epsilon" as the symbol, which will be
 * normalized to {@link Transition#EPSILON}.
 * Comments can be added using "//" at the end of lines within the delta block.
 */
public class AutomatonParser {

    /**
     * Generic regex pattern for extracting content within curly braces, case-insensitive.
     * Expects format: KEY_NAME = { content }.
     * Group 1 captures the content within the braces.
     */
    private static final String REGEX_PATTERN = "(?i)%s\\s*=\\s*\\{\\s*([^}]*?)\\s*}";

    /**
     * Regex for the entire transition block: delta = { ... } or δ = { ... }.
     * Uses {@link #REGEX_PATTERN} with specific keywords for transitions.
     * Pattern.DOTALL allows the dot to match line terminators, accommodating multi-line blocks.
     */
    private static final Pattern TRANSITION_BLOCK_PATTERN = Pattern.compile(String.format(REGEX_PATTERN, "(?:δ|delta)"), Pattern.DOTALL);

    /**
     * Regex for a single transition definition: (from,symbol) = to.
     * Allows for optional spaces around elements and expects comma separation or end of content within the block.
     * Captures:
     * <ol>
     *     <li>fromState: The state from which the transition originates.</li>
     *     <li>symbol: The input symbol for the transition (can be epsilon).</li>
     *     <li>toState: The state to which the transition leads.</li>
     * </ol>
     * The non-capturing group (?:\s*,|\s*$) ensures that a transition is followed by a comma or is at the end of the parsable content.
     */
    private static final Pattern SINGLE_TRANSITION_PATTERN = Pattern.compile(
            "\\s*\\(\\s*([^,]+?)\\s*,\\s*([^)]+?)\\s*\\)\\s*=\\s*([^,]+?)(?:\\s*,|\\s*$)");

    /**
     * Regex for states (Q = {...}). Uses {@link #REGEX_PATTERN}.
     */
    private static final Pattern STATES_PATTERN = Pattern.compile(String.format(REGEX_PATTERN, "Q"), Pattern.DOTALL);
    /**
     * Regex for alphabet (∑ = {...} or Sigma = {...}). Uses {@link #REGEX_PATTERN}.
     */
    private static final Pattern ALPHABET_PATTERN = Pattern.compile(String.format(REGEX_PATTERN, "(?:∑|Sigma)"), Pattern.DOTALL);
    /**
     * Regex for final states (F = {...}). Uses {@link #REGEX_PATTERN}.
     */
    private static final Pattern FINAL_STATES_PATTERN = Pattern.compile(String.format(REGEX_PATTERN, "F"), Pattern.DOTALL);

    /**
     * Parses an automaton definition from a file.
     *
     * @param filePath Path to the file containing the automaton definition.
     * @return A structured {@link Automaton} object representing the parsed automaton.
     * @throws IOException              If the file cannot be read or does not exist.
     * @throws IllegalArgumentException If the automaton definition is invalid or malformed.
     */
    public Automaton parseFromFile(Path filePath) throws IOException {
        if (!Files.exists(filePath)) {
            throw new IOException("Automaton file does not exist: " + filePath);
        }
        String content = Files.readString(filePath);
        return parseFromString(content);
    }

    /**
     * Parses an automaton definition from a string.
     *
     * @param content String containing the automaton definition.
     * @return A structured {@link Automaton} object representing the parsed automaton.
     * @throws IllegalArgumentException If the automaton definition is invalid, malformed, or empty.
     */
    public Automaton parseFromString(String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Automaton content cannot be empty.");
        }

        Set<String> states = extractSet(content, STATES_PATTERN, "States (Q)");
        Set<String> alphabet = extractSet(content, ALPHABET_PATTERN, "Alphabet (∑ or Sigma)");
        Set<String> finalStates = extractSet(content, FINAL_STATES_PATTERN, "Final States (F)");

        String startState = states.contains("q0") ? "q0" : states.stream().findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Cannot determine start state. States set is empty or 'q0' not found."));

        Set<Transition> transitions = extractTransitions(content, states, alphabet);

        // Basic validations
        if (!states.containsAll(finalStates)) {
            Set<String> undefinedFinalStates = new HashSet<>(finalStates);
            undefinedFinalStates.removeAll(states);
            throw new IllegalArgumentException("Final states " + undefinedFinalStates + " are not defined in Q.");
        }
        if (!states.contains(startState)) {
            throw new IllegalArgumentException("Start state '" + startState + "' is not defined in Q.");
        }

        return new Automaton(
                Collections.unmodifiableSet(new LinkedHashSet<>(states)),
                Collections.unmodifiableSet(new LinkedHashSet<>(alphabet)),
                startState,
                Collections.unmodifiableSet(new HashSet<>(finalStates)),
                Collections.unmodifiableSet(new HashSet<>(transitions))
        );
    }

    /**
     * Extracts a set of elements (e.g., states, alphabet symbols) from a section of the automaton definition.
     *
     * @param content The full automaton definition text.
     * @param pattern The regex pattern to match the specific section (e.g., Q = {...}).
     * @param setName A descriptive name for the set being extracted (used for error messages).
     * @return An unmodifiable set of extracted elements, preserving insertion order using {@link LinkedHashSet}.
     * @throws IllegalArgumentException If the section is not found or is malformed.
     */
    private Set<String> extractSet(String content, Pattern pattern, String setName) {
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            String group = matcher.group(1);
            if (group.trim().isEmpty()) {
                return Collections.emptySet();
            }
            return Arrays.stream(group.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toCollection(LinkedHashSet::new));
        }
        throw new IllegalArgumentException(setName + " definition not found or is malformed.");
    }

    /**
     * Extracts and parses transition rules from the automaton definition.
     * Validates that states and symbols used in transitions are defined in Q and ∑ respectively (unless it's an epsilon transition).
     * Epsilon symbols ("ε", "epsilon") are normalized to {@link Transition#EPSILON}.
     * Handles comments (lines starting with //) within the transition block.
     *
     * @param content         The full automaton definition text.
     * @param definedStates   The set of all defined states (Q).
     * @param definedAlphabet The set of all defined alphabet symbols (∑).
     * @return A set of structured {@link Transition} objects.
     * @throws IllegalArgumentException If transitions are malformed, use undefined states/symbols, or if the transition block contains errors.
     */
    private Set<Transition> extractTransitions(String content, Set<String> definedStates, Set<String> definedAlphabet) {
        Set<Transition> transitions = new HashSet<>();
        Matcher blockMatcher = TRANSITION_BLOCK_PATTERN.matcher(content);

        if (blockMatcher.find()) {
            String transitionsBlockInnerContent = blockMatcher.group(1);
            if (transitionsBlockInnerContent == null) {
                transitionsBlockInnerContent = "";
            }

            String cleanedContent = Arrays.stream(transitionsBlockInnerContent.split("\\R"))
                    .map(line -> {
                        int commentStart = line.indexOf("//");
                        return (commentStart != -1) ? line.substring(0, commentStart).trim() : line.trim();
                    })
                    .filter(line -> !line.isEmpty())
                    .collect(Collectors.joining("\n"));

            if (cleanedContent.isEmpty()) {
                return Collections.emptySet();
            }

            Matcher transitionMatcher = SINGLE_TRANSITION_PATTERN.matcher(cleanedContent);
            boolean foundAtLeastOneTransition = false;
            int lastMatchEnd = 0;

            while (transitionMatcher.find()) {
                if (transitionMatcher.start() > lastMatchEnd) {
                    String gap = cleanedContent.substring(lastMatchEnd, transitionMatcher.start()).trim();
                    if (!gap.isEmpty()) {
                        throw new IllegalArgumentException(
                                "Malformed transition definition or unexpected characters '" + gap +
                                        "' found in transitions block before '" + transitionMatcher.group(0) + "'.");
                    }
                }

                foundAtLeastOneTransition = true;
                String fromState = transitionMatcher.group(1).trim();
                String rawSymbol = transitionMatcher.group(2).trim();
                String toState = transitionMatcher.group(3).trim();

                String finalSymbol;

                if ("ε".equals(rawSymbol) || "epsilon".equalsIgnoreCase(rawSymbol)) {
                    finalSymbol = Transition.EPSILON;
                } else {
                    finalSymbol = rawSymbol;
                }

                if (!definedStates.contains(fromState)) {
                    throw new IllegalArgumentException("Transition error: State '" + fromState + "' (from transition '" + transitionMatcher.group(0).trim() + "') is not defined in Q.");
                }

                if (!definedAlphabet.contains(finalSymbol) && !finalSymbol.equals(Transition.EPSILON)) {
                    throw new IllegalArgumentException("Transition error: Symbol '" + rawSymbol + "' (from transition '" + transitionMatcher.group(0).trim() + "') is not defined in ∑.");
                }

                if (!definedStates.contains(toState)) {
                    throw new IllegalArgumentException("Transition error: State '" + toState + "' (from transition '" + transitionMatcher.group(0).trim() + "') is not defined in Q.");
                }

                transitions.add(new Transition(fromState, toState, finalSymbol));
                lastMatchEnd = transitionMatcher.end();
            }

            if (lastMatchEnd < cleanedContent.length()) {
                String remainingContent = cleanedContent.substring(lastMatchEnd).trim();
                if (!remainingContent.isEmpty()) {
                    throw new IllegalArgumentException(
                            "Transition (δ or delta) block found but no valid transitions could be parsed within it, or they are malformed. Unexpected trailing content: '" + remainingContent + "'");
                }
            }

            if (!foundAtLeastOneTransition && !cleanedContent.isEmpty()) {
                throw new IllegalArgumentException(
                        "Transition (δ or delta) block has content that is not solely comments, but no valid transitions could be parsed. Content: '" +
                                cleanedContent + "'");
            }

        } else {
            Pattern keywordDeltaPattern = Pattern.compile("\\bdelta\\b", Pattern.CASE_INSENSITIVE);
            if (content.contains("δ") || keywordDeltaPattern.matcher(content).find()) {
                throw new IllegalArgumentException(
                        "Transition (δ or delta) keyword found, but transitions are not correctly " +
                                "enclosed in a 'delta = { ... }' or 'δ = { ... }' block. " +
                                "Please use the format 'delta = { (q0,a)=q1, ... }'.");
            }
        }
        return transitions;
    }
}
