package org.sergiu.lfa.grammars;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.sergiu.lfa.grammars.Main.*;

public class GrammarParser {

    private static final Pattern NON_TERMINALS_PATTERN = Pattern.compile(String.format(REGEX_PATTERN, "V_N"), Pattern.DOTALL);
    private static final Pattern TERMINALS_PATTERN = Pattern.compile(String.format(REGEX_PATTERN, "V_T"), Pattern.DOTALL);
    private static final Pattern RULES_PATTERN = Pattern.compile(String.format(REGEX_PATTERN, "P"), Pattern.DOTALL);

    public Grammar parseFromFile(Path filePath) throws IOException {
        String content = Files.readString(filePath);
        return parseFromString(content);
    }

    public Grammar parseFromString(String content) {
        Set<String> nonTerminals = extractElements(content, NON_TERMINALS_PATTERN);
        Set<String> terminals = extractElements(content, TERMINALS_PATTERN);
        Set<GrammarRule> rules = extractRules(content);

        String startSymbol = nonTerminals.contains("S")? "S" : null;
        if (startSymbol == null) {
            throw new IllegalArgumentException("Start symbol 'S' is not defined in the non-terminals set");
        }

        return new Grammar(nonTerminals, terminals, startSymbol, rules);
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

    private Set<GrammarRule> extractRules(String content) {
        Set<String> elements = extractElements(content, RULES_PATTERN);
        Set<GrammarRule> result = new LinkedHashSet<>();

        for (String element : elements) {
            String[] parts = element.split("->");
            if (parts.length == 2) {
                String left = parts[0].trim();
                String[] rightParts = parts[1].split("\\|");
                for (String right : rightParts) {
                    Set<String> rightSet = new LinkedHashSet<>(Arrays.asList(right.trim().split("\\s+")));
                    result.add(new GrammarRule(left, rightSet));
                }
            }
        }

        return result;
    }
}