package org.sergiu.lfa.grammars;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.sergiu.lfa.grammars.Main.*;

public class GrammarParser {

    public Grammar parseFromFile(Path filePath) throws IOException {
        String content = Files.readString(filePath);
        return parseFromString(content);
    }

    public Grammar parseFromString(String content) {
        Set<String> nonTerminals = new HashSet<>(extractElements(content, "V_N"));
        Set<String> terminals = new HashSet<>(extractElements(content, "V_T"));

        // Extract rules
        Set<GrammarRule> rules = new HashSet<>(extractRules(content));
        System.out.println(rules);

        String startSymbol = nonTerminals.contains("S")? "S" : null;
        if (startSymbol == null) {
            throw new IllegalArgumentException("Start symbol 'S' is not defined in the non-terminals set");
        }

        return new Grammar(nonTerminals, terminals, startSymbol, rules);
    }

    private Set<String> extractElements(String content, String label) {
        String regexPatter = String.format(REGEX_PATTERN, label);
        Pattern pattern = Pattern.compile(regexPatter, Pattern.DOTALL);
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
        Set<String> elements = extractElements(content, "P");
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