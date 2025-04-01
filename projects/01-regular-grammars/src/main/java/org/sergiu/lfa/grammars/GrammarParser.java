package org.sergiu.lfa.grammars;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GrammarParser {
    private static final Pattern NON_TERMINALS_PATTERN = Pattern.compile("V_N=\\{\\s*([^}]*)\\s*};?\n");
    private static final Pattern TERMINALS_PATTERN = Pattern.compile("V_T=\\{\\s*([^}]*)\\s*};?\n");

    public Grammar parseFromFile(String filePath) throws IOException {
        String content = Files.readString(Path.of(filePath));
        return parseFromString(content);
    }

    public Grammar parseFromString(String content) {
        // Extract non-terminals
        Set<String> nonTerminals = new LinkedHashSet<>(extractElements(content, NON_TERMINALS_PATTERN));
        System.out.println(nonTerminals);


        // Extract terminals
        Set<String> terminals = new HashSet<>(extractElements(content, TERMINALS_PATTERN));
        System.out.println(terminals);

        // Check if the start symbol is defined
        String startSymbol = nonTerminals.contains("S")? "S" : null;
        if (startSymbol == null) {
            throw new IllegalArgumentException("Start symbol 'S' is not defined in the non-terminals set");
        }

        // Extract rules
        // List<GrammarRule> rules = extractRules(content, nonTerminals, terminals);


        // return new Grammar(nonTerminals, terminals, null, startSymbol);
        return null;
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


/*    private static List<GrammarRule> extractRules(String content, Set<String> nonTerminals, Set<String> terminals) {
        List<GrammarRule> rules = new ArrayList<>();
        int pStart = content.indexOf("P={");

        if (pStart != -1) {
            int endPos = content.indexOf('}', pStart + 3);
            if (endPos == -1) endPos = content.length();

            String rulesSection = content.substring(pStart + 3, endPos);
            String[] ruleStrings = rulesSection.split("(?:,\\s*|\\n+)");

            for (String ruleStr : ruleStrings) {
                ruleStr = ruleStr.trim();
                if (ruleStr.isEmpty()) continue;

                // Find the arrow separator
                int arrowIndex = ruleStr.indexOf("->");
                if (arrowIndex == -1) arrowIndex = ruleStr.indexOf("-");

                if (arrowIndex == -1) continue;

                String leftSide = ruleStr.substring(0, arrowIndex).trim();
                String rightSide = ruleStr.substring(arrowIndex + (ruleStr.charAt(arrowIndex + 1) == '>' ? 2 : 1)).trim();

                if (leftSide.isEmpty() || rightSide.isEmpty()) continue;

                // Process alternatives
                if (rightSide.contains("|")) {
                    String[] alternatives = rightSide.split("\\|");
                    for (String alt : alternatives) {
                        alt = alt.trim();
                        if (!alt.isEmpty()) {
                            processRule(rules, leftSide, alt, nonTerminals, terminals);
                        }
                    }
                } else {
                    processRule(rules, leftSide, rightSide, nonTerminals, terminals);
                }
            }
        }

        return rules;
    }

    private static void processRule(List<GrammarRule> rules, String from, String rightSide,
                                   Set<String> nonTerminals, Set<String> terminals) {
        if (rightSide.length() == 0) return;

        // The first character is the terminal
        String terminal = rightSide.substring(0, 1);

        // Check if the terminal is actually in the terminals set
        if (!terminals.contains(terminal)) {
            throw new IllegalArgumentException("Terminal '" + terminal + "' is not in the terminals set");
        }

        // The rest (if any) is the non-terminal to transition to
        String to = null;
        if (rightSide.length() > 1) {
            to = rightSide.substring(1);

            // Verify the non-terminal
            if (!nonTerminals.contains(to)) {
                throw new IllegalArgumentException("Non-terminal '" + to + "' is not in the non-terminals set");
            }
        }

        rules.add(new GrammarRule(from, terminal, to));
    }*/

/*    public static void printGrammar(Grammar grammar) {
        System.out.println("Non-Terminals: " + grammar.getNonTerminals());
        System.out.println("Terminals: " + grammar.getTerminals());
        System.out.println("Start Symbol: " + grammar.getStartSymbol());
        System.out.println("Rules:");

        Map<String, List<String>> groupedRules = new LinkedHashMap<>();
        for (GrammarRule rule : grammar.getRules()) {
            groupedRules.putIfAbsent(rule.from(), new ArrayList<>());
            String rhs = rule.terminal() + (rule.to() != null ? rule.to() : "");
            groupedRules.get(rule.from()).add(rhs);
        }

        for (Map.Entry<String, List<String>> entry : groupedRules.entrySet()) {
            System.out.println("  " + entry.getKey() + " -> " + String.join(" | ", entry.getValue()));
        }
    }*/
}