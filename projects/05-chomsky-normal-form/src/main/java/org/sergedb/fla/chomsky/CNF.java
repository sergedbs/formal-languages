package org.sergedb.fla.chomsky;

import org.sergedb.fla.chomsky.model.Grammar;
import org.sergedb.fla.chomsky.model.Production;
import org.sergedb.fla.chomsky.model.ProductionSymbol;
import org.sergedb.fla.chomsky.model.SymbolType;

import java.util.*;
import java.util.stream.Collectors;

public class CNF {

    private int newSymbolCounter = 0;

    private String generateNewNonTerminalName(Set<String> existingNonTerminals, Set<String> existingTerminals) {
        String newName;
        do {
            newName = "X" + newSymbolCounter++;
        } while (existingNonTerminals.contains(newName) || existingTerminals.contains(newName));
        return newName;
    }

    public Grammar convertToCNF(Grammar grammar) {
        System.out.println("Original Grammar:");
        System.out.println(grammar);
        printSeparator();

        Grammar currentGrammar = grammar;

        // Handle empty language case early if start symbol is not in non-terminals
        // or if grammar has no rules but non-terminals/terminals are defined.
        if (currentGrammar.rules().isEmpty() && !currentGrammar.nonTerminals().contains(currentGrammar.startSymbol())) {
            System.out.println("Grammar is initially empty or start symbol is invalid. Resulting language is empty.");
            // For now, let the steps handle it.
        }


        System.out.println("Step 1: Eliminate ε-productions");
        currentGrammar = eliminateEpsilonProductions(currentGrammar);
        System.out.println(currentGrammar);
        printSeparator();

        System.out.println("Step 2: Eliminate unit productions (renaming)");
        currentGrammar = eliminateUnitProductions(currentGrammar);
        System.out.println(currentGrammar);
        printSeparator();

        System.out.println("Step 3: Eliminate non-productive symbols");
        currentGrammar = eliminateNonProductiveSymbols(currentGrammar);
        System.out.println(currentGrammar);
        printSeparator();

        System.out.println("Step 4: Eliminate inaccessible symbols");
        currentGrammar = eliminateInaccessibleSymbols(currentGrammar);
        System.out.println(currentGrammar);
        printSeparator();

        System.out.println("Step 5: Transform productions to CNF (A -> BC or A -> a)");
        currentGrammar = transformToChomskyForm(currentGrammar);
        System.out.println(currentGrammar);
        printSeparator();
        
        // Post-CNF cleanup is crucial as previous steps can reintroduce issues
        System.out.println("Step 6: Post-CNF cleanup - Eliminate unit productions");
        currentGrammar = eliminateUnitProductions(currentGrammar);
        System.out.println(currentGrammar);
        printSeparator();

        System.out.println("Step 7: Post-CNF cleanup - Eliminate non-productive symbols");
        currentGrammar = eliminateNonProductiveSymbols(currentGrammar);
        System.out.println(currentGrammar);
        printSeparator();

        System.out.println("Step 8: Post-CNF cleanup - Eliminate inaccessible symbols");
        currentGrammar = eliminateInaccessibleSymbols(currentGrammar);
        System.out.println(currentGrammar);
        printSeparator();

        System.out.println("Final CNF Grammar:");
        System.out.println(currentGrammar);
        return currentGrammar;
    }

    private void printSeparator() {
        System.out.println("--------------------------------------------------");
    }

    private Grammar eliminateEpsilonProductions(Grammar grammar) {
        Set<String> nullable = new HashSet<>();
        boolean changed;

        // Initial pass: find all A -> ε
        for (Production p : grammar.rules()) {
            if (p.right().isEmpty()) { 
                nullable.add(p.left());
            }
        }

        // Iteratively find all nullable non-terminals
        do {
            changed = false;
            for (Production p : grammar.rules()) {
                if (!nullable.contains(p.left())) { // Only consider if A is not yet found to be nullable
                    boolean allRhsNullable = !p.right().isEmpty(); 
                    // If RHS is non-empty, check if all its symbols are nullable.
                    if (allRhsNullable) { 
                        for (ProductionSymbol ps : p.right()) {
                            if (ps.type() == SymbolType.TERMINAL || !nullable.contains(ps.value())) {
                                allRhsNullable = false;
                                break;
                            }
                        }
                    }
                    if (allRhsNullable) {
                        if (nullable.add(p.left())) {
                            changed = true;
                        }
                    }
                }
            }
        } while (changed);

        Set<Production> newRules = new HashSet<>();
        for (Production p : grammar.rules()) {
            // Do not add original A -> ε rules
            if (!p.right().isEmpty()) { 
                List<List<ProductionSymbol>> combinations = generateNullableCombinations(p.right(), nullable);
                for (List<ProductionSymbol> combination : combinations) {
                    // Only add the new production if its RHS is not empty
                    if (!combination.isEmpty()) { 
                        newRules.add(new Production(p.left(), combination));
                    }
                }
            }
        }
        
        // Non-terminals and terminals might change after subsequent steps.
        // For this step, they are formally the same, but some might become unused.
        return new Grammar(new HashSet<>(grammar.nonTerminals()), new HashSet<>(grammar.terminals()), grammar.startSymbol(), newRules);
    }

    private List<List<ProductionSymbol>> generateNullableCombinations(List<ProductionSymbol> rhs, Set<String> nullableSymbols) {
        List<List<ProductionSymbol>> allCombinations = new ArrayList<>();
        int n = rhs.size();
        List<Integer> nullableIndicesInRhs = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            ProductionSymbol ps = rhs.get(i);
            // Only non-terminals can be nullable in this context
            if (ps.type() == SymbolType.NON_TERMINAL && nullableSymbols.contains(ps.value())) {
                nullableIndicesInRhs.add(i);
            }
        }

        int numNullableInRhs = nullableIndicesInRhs.size();
        // Iterate through all subsets of these nullable symbols in the RHS
        for (int i = 0; i < (1 << numNullableInRhs); i++) { 
            List<ProductionSymbol> currentCombination = new ArrayList<>();
            Set<Integer> indicesToOmit = new HashSet<>(); // Indices from the original RHS to omit

            // Determine which nullable symbols to omit for this subset
            for (int j = 0; j < numNullableInRhs; j++) {
                // If the j-th bit is set in i, omit the j-th nullable symbol
                if ((i & (1 << j)) != 0) { 
                    indicesToOmit.add(nullableIndicesInRhs.get(j));
                }
            }

            // Construct the new RHS by including symbols not marked for omission
            for (int k = 0; k < n; k++) {
                if (!indicesToOmit.contains(k)) {
                    currentCombination.add(rhs.get(k));
                }
            }
            allCombinations.add(currentCombination);
        }
        // Return distinct combinations
        return allCombinations.stream().distinct().collect(Collectors.toList());
    }

    private Grammar eliminateUnitProductions(Grammar grammar) {
        Set<Production> currentRules = new HashSet<>(grammar.rules());
        boolean changedInOuterLoop = true;

        while (changedInOuterLoop) {
            changedInOuterLoop = false;
            Set<Production> rulesToAdd = new HashSet<>();
            Set<Production> rulesToRemove = new HashSet<>();

            for (Production p1 : currentRules) {
                if (p1.right().size() == 1 && p1.right().get(0).type() == SymbolType.NON_TERMINAL) {
                    String A = p1.left();
                    String B = p1.right().get(0).value();
                    
                    if (A.equals(B)) { // Rule is A -> A
                        rulesToRemove.add(p1);
                        continue; 
                    }

                    // Found a unit rule A -> B (where A != B)
                    rulesToRemove.add(p1); // Mark A -> B for removal

                    // For every rule B -> α, add A -> α, unless A -> α is A -> A.
                    for (Production p2 : grammar.rules()) { 
                        if (p2.left().equals(B)) {
                            Production newProduction = new Production(A, p2.right());
                            
                            // Avoid adding A -> A if B -> A was a rule
                            boolean isSelfLoop = newProduction.left().equals(newProduction.right().size() == 1 && 
                                                                            newProduction.right().get(0).type() == SymbolType.NON_TERMINAL &&
                                                                            newProduction.right().get(0).value().equals(A));
                            
                            if (!isSelfLoop) {
                                // Add if not already in currentRules and not already in rulesToAdd
                                if (!currentRules.contains(newProduction) && !rulesToAdd.contains(newProduction)) {
                                     rulesToAdd.add(newProduction);
                                }
                            }
                        }
                    }
                }
            }

            if (!rulesToRemove.isEmpty() || !rulesToAdd.isEmpty()) {
                 // Consolidate changes
                 Set<Production> oldCurrentRulesSnapshot = new HashSet<>(currentRules);
                 currentRules.removeAll(rulesToRemove);
                 currentRules.addAll(rulesToAdd);
                 if (!currentRules.equals(oldCurrentRulesSnapshot)) {
                     changedInOuterLoop = true;
                 }
            }
        }
        return new Grammar(new HashSet<>(grammar.nonTerminals()), new HashSet<>(grammar.terminals()), grammar.startSymbol(), currentRules);
    }

    private Grammar eliminateNonProductiveSymbols(Grammar grammar) {
        Set<String> productiveNonTerminals = new HashSet<>();
        boolean changed;

        // Iteratively find all non-terminals that can produce a string of terminals
        do {
            changed = false;
            for (Production p : grammar.rules()) {
                if (!productiveNonTerminals.contains(p.left())) { // If LHS is not yet productive
                    boolean ruleLeadsToProductive = true;
                    if (p.right().isEmpty()) { // A -> ε
                        // A->ε makes A productive.
                         ruleLeadsToProductive = true; 
                    } else {
                        for (ProductionSymbol ps : p.right()) {
                            // If a symbol in RHS is a non-terminal and not yet productive, this rule doesn't help yet
                            if (ps.type() == SymbolType.NON_TERMINAL && !productiveNonTerminals.contains(ps.value())) {
                                ruleLeadsToProductive = false;
                                break;
                            }
                        }
                    }
                    if (ruleLeadsToProductive) {
                        if (productiveNonTerminals.add(p.left())) {
                            changed = true;
                        }
                    }
                }
            }
        } while (changed);

        Set<Production> productiveRules = new HashSet<>();
        for (Production p : grammar.rules()) {
            // LHS must be productive
            if (productiveNonTerminals.contains(p.left())) {
                boolean rhsAllProductiveOrTerminal = true;
                for (ProductionSymbol ps : p.right()) {
                    // Each non-terminal in RHS must also be productive
                    if (ps.type() == SymbolType.NON_TERMINAL && !productiveNonTerminals.contains(ps.value())) {
                        rhsAllProductiveOrTerminal = false;
                        break;
                    }
                }
                if (rhsAllProductiveOrTerminal) {
                    productiveRules.add(p);
                }
            }
        }

        // Determine the set of terminals actually used in the productive part of the grammar
        Set<String> finalTerminals = new HashSet<>();
        for(Production p : productiveRules){
            for(ProductionSymbol ps : p.right()){
                if(ps.type() == SymbolType.TERMINAL){
                    finalTerminals.add(ps.value());
                }
            }
        }
        
        String finalStartSymbol = grammar.startSymbol();
        // If the original start symbol is not productive, the language is empty.
        if (!productiveNonTerminals.contains(finalStartSymbol) && !grammar.rules().isEmpty() && grammar.nonTerminals().contains(finalStartSymbol)) {
             System.out.println("Warning: Start symbol '" + finalStartSymbol + "' is non-productive. Language is empty.");
        }


        return new Grammar(productiveNonTerminals, finalTerminals, finalStartSymbol, productiveRules);
    }

    private Grammar eliminateInaccessibleSymbols(Grammar grammar) {
        Set<String> accessibleSymbols = new HashSet<>();
        String startSymbol = grammar.startSymbol();

        // Start symbol is accessible only if it's a non-terminal in the current grammar
        if (grammar.nonTerminals().contains(startSymbol)) {
            accessibleSymbols.add(startSymbol);
        } else if (!grammar.rules().isEmpty()){
            // If start symbol isn't in non-terminals (e.g. removed by non-productive step)
            // and there are rules, then nothing is accessible from it.
             System.out.println("Warning: Start symbol '" + startSymbol + "' is not in the set of non-terminals for accessibility check. Language might be empty.");
        }


        boolean changed;
        do {
            changed = false;
            for (Production p : grammar.rules()) {
                // If LHS (A) is accessible
                if (accessibleSymbols.contains(p.left())) {
                    // Then all symbols in RHS are accessible
                    for (ProductionSymbol ps : p.right()) {
                        // Ensure the symbol is actually part of the grammar's defined symbols.
                        if (grammar.nonTerminals().contains(ps.value()) || grammar.terminals().contains(ps.value())) {
                           if (accessibleSymbols.add(ps.value())) { 
                                changed = true;
                            }
                        }
                    }
                }
            }
        } while (changed);

        Set<String> accessibleNonTerminals = new HashSet<>();
        for (String nt : grammar.nonTerminals()) {
            if (accessibleSymbols.contains(nt)) {
                accessibleNonTerminals.add(nt);
            }
        }

        Set<String> finalAccessibleTerminals = new HashSet<>();
        for (Production p : grammar.rules()) { 
            if (accessibleNonTerminals.contains(p.left())) { // Rule is accessible if its LHS is
                for (ProductionSymbol ps : p.right()) {
                    // If a terminal appears in an accessible rule, it's an accessible terminal
                    if (ps.type() == SymbolType.TERMINAL && grammar.terminals().contains(ps.value())) { 
                        finalAccessibleTerminals.add(ps.value());
                    }
                }
            }
        }


        Set<Production> accessibleRules = new HashSet<>();
        for (Production p : grammar.rules()) {
            // A rule A -> α is accessible if A is accessible AND all non-terminals in α are accessible
            if (accessibleNonTerminals.contains(p.left())) {
                boolean rhsAllAccessible = true;
                for(ProductionSymbol ps : p.right()){
                    if(ps.type() == SymbolType.NON_TERMINAL && !accessibleNonTerminals.contains(ps.value())){
                        rhsAllAccessible = false;
                        break;
                    }
                }
                if(rhsAllAccessible){
                    accessibleRules.add(p);
                }
            }
        }
        
        // If the original start symbol is no longer accessible
        if (!accessibleNonTerminals.contains(startSymbol) && grammar.nonTerminals().contains(startSymbol) && !grammar.rules().isEmpty()) {
             System.out.println("Warning: Start symbol '" + startSymbol + "' became inaccessible. Language is empty.");
        }


        return new Grammar(accessibleNonTerminals, finalAccessibleTerminals, startSymbol, accessibleRules);
    }

    private Grammar transformToChomskyForm(Grammar grammar) {
        Set<Production> currentRules = new HashSet<>(grammar.rules());
        Set<String> currentNonTerminals = new HashSet<>(grammar.nonTerminals());
        Set<String> originalTerminals = new HashSet<>(grammar.terminals()); 

        boolean rulesChangedOverall = true;
        
        // Loop until no more transformations (TERM or BIN) can be applied in a pass
        while(rulesChangedOverall) {
            rulesChangedOverall = false;
            Set<Production> rulesToAddThisPass = new HashSet<>();
            Set<Production> rulesToRemoveThisPass = new HashSet<>();
            
            // Map to track T_a -> a rules created or existing in this pass to reuse them.
            Map<String, String> terminalToNonTerminalProxy = new HashMap<>();
            // Pre-populate with existing A -> t rules
            for (Production p : currentRules) {
                if (p.right().size() == 1 && p.right().get(0).type() == SymbolType.TERMINAL) {
                    terminalToNonTerminalProxy.put(p.right().get(0).value(), p.left());
                }
            }

            for (Production p : new HashSet<>(currentRules)) { // Iterate on a copy
                List<ProductionSymbol> rhs = p.right();
                String lhs = p.left();

                // Check if rule is already in CNF: A -> a or A -> BC
                if (rhs.size() == 1 && rhs.get(0).type() == SymbolType.TERMINAL) {
                    rulesToAddThisPass.add(p); // Keep A -> a
                } else if (rhs.size() == 2 && rhs.get(0).type() == SymbolType.NON_TERMINAL && rhs.get(1).type() == SymbolType.NON_TERMINAL) {
                    rulesToAddThisPass.add(p); // Keep A -> BC
                } else {
                    // Rule needs transformation
                    rulesToRemoveThisPass.add(p); 

                    List<ProductionSymbol> transformedRhs = new ArrayList<>();
                    boolean rhsWasModifiedByTermStep = false;

                    // TERM step: Replace terminals t with T_t if RHS is not just t.
                    if (!rhs.isEmpty()) {
                        for (int i = 0; i < rhs.size(); i++) {
                            ProductionSymbol ps = rhs.get(i);
                            if (ps.type() == SymbolType.TERMINAL) {
                                // If a terminal exists in a rule not of form A->t, it needs proxy.
                                String termVal = ps.value();
                                String termNtName = terminalToNonTerminalProxy.get(termVal);

                                if (termNtName == null) { // No existing T_val -> val rule found
                                    // Check if one was just added in this pass
                                    for (Production addedRule : rulesToAddThisPass) {
                                        if (addedRule.right().size() == 1 && 
                                            addedRule.right().get(0).type() == SymbolType.TERMINAL &&
                                            addedRule.right().get(0).value().equals(termVal)) {
                                            termNtName = addedRule.left();
                                            break;
                                        }
                                    }
                                }
                                
                                if (termNtName == null) { // Still not found, create a new T_val
                                    // Generate a unique name like T_a, T_b_0, etc.
                                    String baseName = "T_" + termVal.replaceAll("[^a-zA-Z0-9]", "");
                                    String tempName = baseName;
                                    int suffix = 0;
                                    String finalTempName = tempName;
                                    while(currentNonTerminals.contains(tempName) ||
                                          rulesToAddThisPass.stream().anyMatch(r -> r.left().equals(finalTempName)) ||
                                          originalTerminals.contains(tempName) ) { // also check against original terminals
                                        tempName = baseName + "_" + suffix++;
                                    }
                                    termNtName = tempName;

                                    // Add the new rule T_val -> val
                                    rulesToAddThisPass.add(new Production(termNtName, List.of(new ProductionSymbol(termVal, SymbolType.TERMINAL))));
                                    currentNonTerminals.add(termNtName); 
                                    terminalToNonTerminalProxy.put(termVal, termNtName);
                                    rhsWasModifiedByTermStep = true;
                                }
                                transformedRhs.add(new ProductionSymbol(termNtName, SymbolType.NON_TERMINAL));
                            } else { // It's a non-terminal, add as is
                                transformedRhs.add(ps); 
                            }
                        }
                    } else { // RHS is empty - should have been handled by epsilon elimination
                        continue; 
                    }
                    
                    // After TERM step, transformedRhs contains only non-terminals
                    // Now apply BIN step if necessary
                    if (transformedRhs.size() == 1) { 
                        // This could be A -> N (a unit rule, e.g. from A -> t becoming A -> T_t)
                        rulesToAddThisPass.add(new Production(lhs, transformedRhs));
                        if (rhsWasModifiedByTermStep) rulesChangedOverall = true; 
                    } else if (transformedRhs.size() == 2) { 
                        // Already A -> N1 N2 (CNF form after TERM)
                         rulesToAddThisPass.add(new Production(lhs, transformedRhs));
                         if (rhsWasModifiedByTermStep) rulesChangedOverall = true; 
                    } else if (transformedRhs.size() > 2) {
                        // BIN step: A -> N1 N2 N3 ...
                        rulesChangedOverall = true; // Binarization is a structural change
                        String currentLhsForBinarization = lhs;
                        for (int i = 0; i < transformedRhs.size() - 2; i++) {
                            String newIntermediateNt = generateNewNonTerminalName(currentNonTerminals, originalTerminals);
                            currentNonTerminals.add(newIntermediateNt); 
                            
                            // Add rule: currentLhs -> N_i X_i
                            rulesToAddThisPass.add(new Production(currentLhsForBinarization, 
                                List.of(transformedRhs.get(i), new ProductionSymbol(newIntermediateNt, SymbolType.NON_TERMINAL))));
                            currentLhsForBinarization = newIntermediateNt; 
                        }
                        // Add final rule: X_{k-2} -> N_{k-1} N_k
                        rulesToAddThisPass.add(new Production(currentLhsForBinarization, 
                            List.of(transformedRhs.get(transformedRhs.size() - 2), transformedRhs.get(transformedRhs.size() - 1))));
                    }
                }
            } 

            // Apply changes for this pass
            if (!rulesToRemoveThisPass.isEmpty() || !rulesToAddThisPass.isEmpty()) {
                 Set<Production> oldRulesSnapshot = new HashSet<>(currentRules);
                 currentRules.removeAll(rulesToRemoveThisPass);
                 currentRules.addAll(rulesToAddThisPass);
                 if (!currentRules.equals(oldRulesSnapshot) && !rulesChangedOverall) {
                     rulesChangedOverall = true;
                 } 
            }
        } 
        
        // Final set of terminals for the CNF grammar are those appearing in A -> a rules
        Set<String> finalTerminalsForCNF = new HashSet<>();
        for(Production p : currentRules) {
            if (p.right().size() == 1 && p.right().get(0).type() == SymbolType.TERMINAL) {
                finalTerminalsForCNF.add(p.right().get(0).value());
            }
        }

        return new Grammar(currentNonTerminals, finalTerminalsForCNF, grammar.startSymbol(), currentRules);
    }
}
