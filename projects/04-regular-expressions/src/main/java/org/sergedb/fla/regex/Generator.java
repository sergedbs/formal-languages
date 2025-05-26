package org.sergedb.fla.regex;

import org.sergedb.fla.regex.Parser.*; 

import java.util.*;

public class Generator {
    private final Random random;
    private final int maxIterations; 
    private final Map<String, List<String>> allProcessingSteps;

    public Generator() {
        this(5); 
    }

    public Generator(int maxIterations) {
        this.random = new Random();
        this.maxIterations = Math.max(1, maxIterations); 
        this.allProcessingSteps = new LinkedHashMap<>();
    }

    public List<String> generate(RegexNode astRoot, int count) {
        if (astRoot == null) {
            return Collections.emptyList();
        }
        Set<String> generatedStringsSet = new HashSet<>();
        allProcessingSteps.clear();

        
        for (int i = 0; i < count * 5 && generatedStringsSet.size() < count; i++) {
            List<String> currentSteps = new ArrayList<>();
            String generatedString = generateRecursive(astRoot, currentSteps);
            if (!generatedString.isEmpty()) {
                 if(generatedStringsSet.add(generatedString)) {
                    allProcessingSteps.put(generatedString, currentSteps);
                 }
            } else if (generatedString != null) {
                if (generatedStringsSet.add("")) {
                     allProcessingSteps.put("", currentSteps);
                }
            }
        }
        return new ArrayList<>(generatedStringsSet);
    }

    public Map<String, List<String>> getAllProcessingSteps() {
        return Collections.unmodifiableMap(allProcessingSteps);
    }

    private void addStep(String step, List<String> stepsList) {
        stepsList.add(step);
    }

    private String generateRecursive(RegexNode node, List<String> currentGenerationSteps) {
        if (node == null) {
            return "";
        }
        StringBuilder result = new StringBuilder();

        if (node instanceof LiteralNode literalNode) {
            addStep("Appending literal: '" + literalNode.value() + "'", currentGenerationSteps);
            result.append(literalNode.value());
        } else if (node instanceof ConcatNode concatNode) {
            addStep("Processing concatenation:", currentGenerationSteps);
            addStep("  Left part of concatenation: " + concatNode.left(), currentGenerationSteps);
            result.append(generateRecursive(concatNode.left(), currentGenerationSteps));
            addStep("  Right part of concatenation: " + concatNode.right(), currentGenerationSteps);
            result.append(generateRecursive(concatNode.right(), currentGenerationSteps));
            addStep("Finished concatenation.", currentGenerationSteps);
        } else if (node instanceof OrNode orNode) {
            addStep("Processing OR (|) choice for: " + orNode, currentGenerationSteps);
            if (random.nextBoolean()) {
                addStep("  Chose left side of OR: " + orNode.left(), currentGenerationSteps);
                result.append(generateRecursive(orNode.left(), currentGenerationSteps));
            } else {
                addStep("  Chose right side of OR: " + orNode.right(), currentGenerationSteps);
                result.append(generateRecursive(orNode.right(), currentGenerationSteps));
            }
            addStep("Finished OR choice.", currentGenerationSteps);
        } else if (node instanceof StarNode starNode) {
            int repetitions = random.nextInt(maxIterations + 1);
            addStep("Processing STAR (*) quantifier for: " + starNode.operand() + ". Repeating " + repetitions + " times.", currentGenerationSteps);
            for (int i = 0; i < repetitions; i++) {
                addStep("  STAR repetition " + (i + 1) + "/" + repetitions + " for " + starNode.operand(), currentGenerationSteps);
                result.append(generateRecursive(starNode.operand(), currentGenerationSteps));
            }
            addStep("Finished STAR quantifier for: " + starNode.operand(), currentGenerationSteps);
        } else if (node instanceof PlusNode plusNode) {
            int repetitions = 1 + random.nextInt(maxIterations); // Ensures at least one repetition
            addStep("Processing PLUS (+) quantifier for: " + plusNode.operand() + ". Repeating " + repetitions + " times.", currentGenerationSteps);
            for (int i = 0; i < repetitions; i++) {
                addStep("  PLUS repetition " + (i + 1) + "/" + repetitions + " for " + plusNode.operand(), currentGenerationSteps);
                result.append(generateRecursive(plusNode.operand(), currentGenerationSteps));
            }
            addStep("Finished PLUS quantifier for: " + plusNode.operand(), currentGenerationSteps);
        } else if (node instanceof QuestionNode questionNode) {
            addStep("Processing QUESTION (?) quantifier for: " + questionNode.operand(), currentGenerationSteps);
            if (random.nextBoolean()) {
                addStep("  QUESTION: Including operand " + questionNode.operand(), currentGenerationSteps);
                result.append(generateRecursive(questionNode.operand(), currentGenerationSteps));
            } else {
                addStep("  QUESTION: Excluding operand " + questionNode.operand(), currentGenerationSteps);
            }
            addStep("Finished QUESTION quantifier for: " + questionNode.operand(), currentGenerationSteps);
        } else if (node instanceof RepetitionNode repNode) {
            int min = repNode.minOccurrences();
            Integer max = repNode.maxOccurrences();
            int repetitions;

            String repetitionType;
            if (max == null) { // {min,}
                repetitions = min + random.nextInt(maxIterations + 1); // min or more
                repetitionType = "{" + min + ",}";
            } else if (min == max) { // {min} or {n}
                repetitions = min;
                repetitionType = "{" + min + "}";
            } else { // {min,max}
                if (min > max) {
                    addStep("Warning: Min repetitions " + min + " > Max repetitions " + max + " for " + repNode.operand() + ". Using min.", currentGenerationSteps);
                    repetitions = min;
                } else {
                    repetitions = min + random.nextInt(max - min + 1);
                }
                repetitionType = "{" + min + "," + max + "}";
            }
            
            repetitions = Math.max(0, repetitions); // Ensure repetitions is not negative

            addStep("Processing REPETITION " + repetitionType + " quantifier for: " + repNode.operand() + ". Repeating " + repetitions + " times.", currentGenerationSteps);
            for (int i = 0; i < repetitions; i++) {
                addStep("  REPETITION " + (i + 1) + "/" + repetitions + " for " + repNode.operand(), currentGenerationSteps);
                result.append(generateRecursive(repNode.operand(), currentGenerationSteps));
            }
            addStep("Finished REPETITION " + repetitionType + " quantifier for: " + repNode.operand(), currentGenerationSteps);
        } else {
            addStep("Encountered an unknown node type: " + node.getClass().getName(), currentGenerationSteps);
        }
        return result.toString();
    }
}
