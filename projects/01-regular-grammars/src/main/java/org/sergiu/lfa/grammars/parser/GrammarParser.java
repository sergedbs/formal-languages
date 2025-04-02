package org.sergiu.lfa.grammars.parser;

import org.sergiu.lfa.grammars.model.SymbolType;
import org.sergiu.lfa.grammars.model.Grammar;
import org.sergiu.lfa.grammars.model.Production;
import org.sergiu.lfa.grammars.model.ProductionSymbol;

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
    private static final String START_SYMBOL = "S";

    public Grammar parseFromFile(Path filePath) throws IOException {
        String content = Files.readString(filePath);
        return parseFromString(content);
    }

    public Grammar parseFromString(String content) {
        Set<String> nonTerminals = extractElements(content, NON_TERMINALS_PATTERN);
        Set<String> terminals = extractElements(content, TERMINALS_PATTERN);
        Set<Production> rules = extractRules(content, terminals, nonTerminals);

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

    private Set<Production> extractRules(String content, Set<String> terminals, Set<String> nonTerminals) {
        Set<String> elements = extractElements(content, RULES_PATTERN);
        Set<Production> result = new LinkedHashSet<>();

        Map<String, SymbolType> symbolTypeMap = new HashMap<>();
        terminals.forEach(t -> symbolTypeMap.put(t, SymbolType.TERMINAL));
        nonTerminals.forEach(nt -> symbolTypeMap.put(nt, SymbolType.NON_TERMINAL));

        for (String element : elements) {
            String[] parts = element.split("->");
            if (parts.length == 2) {
                String left = parts[0].trim();
                String[] rightParts = parts[1].split("\\|");
                for (String right : rightParts) {
                    List<ProductionSymbol> tokenRHS = tokenizeRHS(right.trim(), symbolTypeMap);
                    result.add(new Production(left, tokenRHS));
                }
            } else {
                throw new IllegalArgumentException("Invalid rule format: " + element);
            }
        }

        return result;
    }

    private List<ProductionSymbol> tokenizeRHS(String rhs, Map<String, SymbolType> symbolTypeMap) {
        List<ProductionSymbol> tokens = new ArrayList<>();

        for (int i = 0; i < rhs.length(); i++) {
            String s = String.valueOf(rhs.charAt(i));
            SymbolType type = symbolTypeMap.get(s);
            if (type != null) {
                tokens.add(new ProductionSymbol(s, type));
            } else {
                throw new IllegalArgumentException("Unknown symbol in RHS at index " + i + ": '" + rhs.substring(i) + "'");
            }
        }
        return tokens;
    }
}