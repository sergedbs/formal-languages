package org.sergiu.lfa.grammars;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.sergiu.lfa.grammars.Main.*;

public class GrammarParser {

    private static final Pattern NON_TERMINALS_PATTERN = Pattern.compile(String.format(REGEX_PATTERN, "V_N"), Pattern.DOTALL);
    private static final Pattern TERMINALS_PATTERN = Pattern.compile(String.format(REGEX_PATTERN, "V_T"), Pattern.DOTALL);
    private static final Pattern RULES_PATTERN = Pattern.compile(String.format(REGEX_PATTERN, "P"), Pattern.DOTALL);
    private static final String START_SYMBOL = "S";

    public Grammar parseFromFile(Path filePath) throws IOException {
        String content = Files.readString(filePath);
        return parseFromString(content);
    }

    public Grammar parseFromString(String content) {
        Set<String> nonTerminals = extractElements(content, NON_TERMINALS_PATTERN);
        Set<String> terminals = extractElements(content, TERMINALS_PATTERN);
        Set<GrammarRule> rules = extractRules(content, terminals, nonTerminals);

        if (!nonTerminals.contains(START_SYMBOL)) {
            throw new IllegalArgumentException("Start symbol '" + START_SYMBOL + "' is not defined in the non-terminals set");
        }

        return new Grammar(nonTerminals, terminals, START_SYMBOL, rules);
    }

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

    private Set<GrammarRule> extractRules(String content, Set<String> terminals, Set<String> nonTerminals) {
        Set<String> elements = extractElements(content, RULES_PATTERN);
        Set<GrammarRule> result = new LinkedHashSet<>();

        for (String element : elements) {
            String[] parts = element.split("->");
            if (parts.length == 2) {
                String left = parts[0].trim();
                String[] rightParts = parts[1].split("\\|");
                for (String right : rightParts) {
                    List<TokenRHS> rhsTokens = tokenizeRHS(right.trim(), terminals, nonTerminals);
                    result.add(new GrammarRule(left, rhsTokens));
                }
            } else {
                throw new IllegalArgumentException("Invalid rule format: " + element);
            }
        }

        return result;
    }

    private List<TokenRHS> tokenizeRHS(String rhs, Set<String> terminals, Set<String> nonTerminals) {
        List<String> vocabulary = Stream.concat(terminals.stream(), nonTerminals.stream())
                .sorted((a, b) -> Integer.compare(b.length(), a.length()))
                .toList();

        List<TokenRHS> tokens = new ArrayList<>();
        int i = 0;

        while (i < rhs.length()) {
            boolean matched = false;

            for (String symbol : vocabulary) {
                if (rhs.startsWith(symbol, i)) {
                    tokens.add(new TokenRHS(symbol, terminals.contains(symbol)));
                    i += symbol.length();
                    matched = true;
                    break;
                }
            }

            if (!matched) {
                throw new IllegalArgumentException(
                        "Unknown symbol in RHS at index " + i + ": '" + rhs.substring(i) + "'");
            }
        }

        return tokens;
    }
}