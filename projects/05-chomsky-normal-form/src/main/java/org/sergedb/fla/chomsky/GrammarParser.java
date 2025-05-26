package org.sergedb.fla.chomsky;

import org.sergedb.fla.chomsky.model.Grammar;
import org.sergedb.fla.chomsky.model.Production;
import org.sergedb.fla.chomsky.model.ProductionSymbol;
import org.sergedb.fla.chomsky.model.SymbolType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses grammar definitions from text into structured {@link Grammar} objects.
 * <p>
 * This parser understands grammar definitions in the following format:
 * <pre>
 * V_N={S, A, B, ...}     // Non-terminal symbols
 * V_T={a, b, c, ...}     // Terminal symbols
 * P={ S -> aA,           // Production rules
 *     A -> bB,
 *     B -> c
 * }
 * </pre>
 * <p>
 * The parser extracts these components and constructs a formal grammar representation,
 * validating that the grammar contains the required start symbol and that all symbols
 * in the production rules are defined.
 */
public class GrammarParser {

    private static final String REGEX_PATTERN = "(?i)%s\\s*=\\s*\\{\\s*((?:[^{}]|[\\r\\n])*)\\s*}";
    private static final Pattern NON_TERMINALS_PATTERN = Pattern.compile(String.format(REGEX_PATTERN, "V_N"), Pattern.DOTALL);
    private static final Pattern TERMINALS_PATTERN = Pattern.compile(String.format(REGEX_PATTERN, "V_T"), Pattern.DOTALL);
    private static final Pattern RULES_PATTERN = Pattern.compile(String.format(REGEX_PATTERN, "P"), Pattern.DOTALL);
    private static final String START_SYMBOL = "S";
    private static final String EPSILON = "ε";

    /**
     * Parses a grammar definition from a file.
     *
     * @param filePath Path to the file containing the grammar definition
     * @return A structured {@link Grammar} object representing the parsed grammar
     * @throws IOException              If the file cannot be read
     * @throws IllegalArgumentException If the grammar definition is invalid
     */
    public Grammar parseFromFile(Path filePath) throws IOException {
        if (!Files.exists(filePath)) {
            throw new IOException("Grammar file does not exist: " + filePath);
        }

        String content = Files.readString(filePath);
        return parseFromString(content);
    }

    /**
     * Parses a grammar definition from a string.
     *
     * @param content String containing the grammar definition
     * @return A structured {@link Grammar} object representing the parsed grammar
     * @throws IllegalArgumentException If the grammar definition is invalid
     */
    public Grammar parseFromString(String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Grammar content cannot be empty");
        }

        Set<String> nonTerminals = extractElements(content, NON_TERMINALS_PATTERN);
        if (nonTerminals.isEmpty()) {
            throw new IllegalArgumentException("No non-terminal symbols found in the grammar");
        }

        Set<String> terminals = extractElements(content, TERMINALS_PATTERN);
        if (terminals.isEmpty()) {
            throw new IllegalArgumentException("No terminal symbols found in the grammar");
        }

        Set<String> intersection = new HashSet<>(nonTerminals);
        intersection.retainAll(terminals);
        if (!intersection.isEmpty()) {
            throw new IllegalArgumentException("Symbols cannot be both terminal and non-terminal: " + intersection);
        }

        if (!nonTerminals.contains(START_SYMBOL)) {
            throw new IllegalArgumentException("Start symbol '" + START_SYMBOL + "' is not defined in the non-terminals set");
        }

        Map<String, SymbolType> symbolTypeMap = buildSymbolTypeMap(terminals, nonTerminals);

        Set<Production> rules = extractRules(content, symbolTypeMap);
        if (rules.isEmpty()) {
            throw new IllegalArgumentException("No production rules found in the grammar");
        }

        return new Grammar(nonTerminals, terminals, START_SYMBOL, rules);
    }

    /**
     * Builds a mapping from symbol values to their types.
     *
     * @param terminals    Set of terminal symbols
     * @param nonTerminals Set of non-terminal symbols
     * @return Map of symbol values to their types
     */
    private Map<String, SymbolType> buildSymbolTypeMap(Set<String> terminals, Set<String> nonTerminals) {
        Map<String, SymbolType> symbolTypeMap = new HashMap<>(terminals.size() + nonTerminals.size());
        terminals.forEach(t -> symbolTypeMap.put(t, SymbolType.TERMINAL));
        nonTerminals.forEach(nt -> symbolTypeMap.put(nt, SymbolType.NON_TERMINAL));
        return symbolTypeMap;
    }

    /**
     * Extracts set elements from a section of the grammar definition.
     *
     * @param content The full grammar definition text
     * @param pattern The regex pattern to match the specific section
     * @return A set of extracted elements
     */
    private Set<String> extractElements(String content, Pattern pattern) {
        Set<String> result = new LinkedHashSet<>();
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            String[] elements = matcher.group(1).split(",");
            for (String element : elements) {
                String trimmed = element.trim();
                if (!trimmed.isEmpty()) {
                    result.add(trimmed);
                }
            }
        }
        return result;
    }

    /**
     * Extracts and parses production rules from the grammar definition.
     *
     * @param content       The full grammar definition text
     * @param symbolTypeMap Map of symbols to their types for classification
     * @return A set of structured {@link Production} objects
     * @throws IllegalArgumentException If a rule has invalid format or contains undefined symbols
     */
    private Set<Production> extractRules(String content, Map<String, SymbolType> symbolTypeMap) {
        Set<String> elements = extractElements(content, RULES_PATTERN);
        if (elements.isEmpty()) {
            return Set.of();
        }

        Set<Production> result = new LinkedHashSet<>();

        for (String element : elements) {
            element = element.trim();
            if (element.isEmpty()) continue;

            String[] parts = element.split("->");
            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid rule format (check for missing ->): " + element);
            }

            String left = parts[0].trim();
            if (!symbolTypeMap.containsKey(left)) {
                throw new IllegalArgumentException("Left-hand side must be a non-terminal: " + left);
            }

            String rightSide = parts[1].trim();
            for (String right : rightSide.split("\\|")) {
                parseRightSide(left, right.trim(), symbolTypeMap, result);
            }
        }

        return result;
    }

    private void parseRightSide(String left, String right, Map<String, SymbolType> symbolTypeMap, Set<Production> result) {
        if (right.isEmpty()) {
            return;
        }

        if (right.equals(EPSILON)) {
            result.add(new Production(left, List.of()));
            return;
        }

        List<ProductionSymbol> tokenRHS = tokenizeRHS(right, symbolTypeMap);
        result.add(new Production(left, tokenRHS));
    }

    /**
     * Tokenizes the right-hand side of a production rule into individual symbols.
     *
     * @param rhs           The right-hand side string of a production rule
     * @param symbolTypeMap Map of symbols to their types for classification
     * @return Ordered list of {@link ProductionSymbol} objects representing the RHS
     * @throws IllegalArgumentException If an undefined symbol is encountered
     */
    private List<ProductionSymbol> tokenizeRHS(String rhs, Map<String, SymbolType> symbolTypeMap) {
        if (rhs.isEmpty()) {
            return List.of();
        }

        String noSpaces = rhs.replaceAll("\\s+", "");
        if (noSpaces.isEmpty()) {
            return List.of();
        }

        List<ProductionSymbol> tokens = new ArrayList<>(noSpaces.length());

        for (int i = 0; i < noSpaces.length(); i++) {
            String s = String.valueOf(noSpaces.charAt(i));
            SymbolType type = symbolTypeMap.get(s);

            if (type == null) {
                throw new IllegalArgumentException(
                        "Unknown symbol in production at position " + i + ": '" + s + "' in '" + rhs + "'"
                );
            }

            tokens.add(new ProductionSymbol(s, type));
        }

        return tokens;
    }
}