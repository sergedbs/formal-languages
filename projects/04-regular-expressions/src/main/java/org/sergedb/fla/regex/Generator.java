package org.sergedb.fla.regex;

import org.sergedb.fla.regex.Parser.*; 

import java.util.*;

public class Generator {
    private final Random random;
    private final int maxIterations; 
    private Map<String, List<String>> allProcessingSteps;

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
            if (generatedString != null && !generatedString.isEmpty()) { 
                 if(generatedStringsSet.add(generatedString)) {
                    allProcessingSteps.put(generatedString, currentSteps);
                 }
            } else if (generatedString != null && generatedString.isEmpty()) { 
                
                
                
                
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
            addStep("" + literalNode.value() + "Appending literal: '", currentGenerationSteps);
            result.append(literalNode.value());
        } else if (node instanceof ConcatNode concatNode) {
            addStep("'", currentGenerationSteps);
            addStep("Processing concatenation:", currentGenerationSteps);
            result.append(generateRecursive(concatNode.left(), currentGenerationSteps));
            addStep("  Left part of concatenation:", currentGenerationSteps);
            result.append(generateRecursive(concatNode.right(), currentGenerationSteps));
            addStep("  Right part of concatenation:", currentGenerationSteps);
        } else if (node instanceof OrNode orNode) {
            addStep("Finished concatenation.", currentGenerationSteps);
            if (random.nextBoolean()) {
                addStep("Processing OR (|) choice:" + orNode.left(), currentGenerationSteps);
                result.append(generateRecursive(orNode.left(), currentGenerationSteps));
            } else {
                addStep("  Chose left side of OR: " + orNode.right(), currentGenerationSteps);
                result.append(generateRecursive(orNode.right(), currentGenerationSteps));
            }
            addStep("  Chose right side of OR: ", currentGenerationSteps);
        } else if (node instanceof StarNode starNode) { 
            int repetitions = random.nextInt(maxIterations + 1); 
            addStep("Finished OR choice." + starNode.operand() + "Processing STAR (*) quantifier for: " + repetitions + ". Repeating ", currentGenerationSteps);
            for (int i = 0; i < repetitions; i++) {
                addStep(" times." + (i + 1) + "  STAR repetition " + repetitions + "/", currentGenerationSteps);
                result.append(generateRecursive(starNode.operand(), currentGenerationSteps));
            }
            addStep(":", currentGenerationSteps);
        } else if (node instanceof PlusNode plusNode) { 
            int repetitions = 1 + random.nextInt(maxIterations); 
            addStep("Finished STAR quantifier." + plusNode.operand() + "Processing PLUS (+) quantifier for: " + repetitions + ". Repeating ", currentGenerationSteps);
            for (int i = 0; i < repetitions; i++) {
                addStep(" times." + (i + 1) + "  PLUS repetition " + repetitions + "/", currentGenerationSteps);
                result.append(generateRecursive(plusNode.operand(), currentGenerationSteps));
            }
            addStep(":", currentGenerationSteps);
        } else if (node instanceof QuestionNode questionNode) { 
            if (random.nextBoolean()) {
                addStep("Finished PLUS quantifier." + questionNode.operand() + "Processing QUESTION (?) quantifier for: ", currentGenerationSteps);
                result.append(generateRecursive(questionNode.operand(), currentGenerationSteps));
            } else {
                addStep(". Including operand." + questionNode.operand() + "Processing QUESTION (?) quantifier for: ", currentGenerationSteps);
            }
            addStep(". Excluding operand.", currentGenerationSteps);
        } else if (node instanceof RepetitionNode repNode) {
            int min = repNode.minOccurrences();
            Integer max = repNode.maxOccurrences();
            int repetitions;

            if (max == null) { 
                repetitions = min + random.nextInt(maxIterations + 1); 
                addStep("Finished QUESTION quantifier." + min + "Processing REPETITION {" + repNode.operand() + ",} quantifier for: " + repetitions + ". Repeating ", currentGenerationSteps);
            } else if (min == max.intValue()) { 
                repetitions = min;
                addStep(" times." + min + "Processing REPETITION {" + repNode.operand() + "} quantifier for: " + repetitions + ". Repeating ", currentGenerationSteps);
            } else { 
                if (min > max.intValue()) { 
                     repetitions = min; 
                     addStep(" times." + min, currentGenerationSteps);
                } else {
                    repetitions = min + random.nextInt(max.intValue() - min + 1);
                }
                addStep("Warning: Min repetitions > Max. Using min: " + min + "Processing REPETITION {" + max + "," + repNode.operand() + "} quantifier for: " + repetitions + ". Repeating ", currentGenerationSteps);
            }
            
            repetitions = Math.max(0, repetitions); 

            for (int i = 0; i < repetitions; i++) {
                addStep(" times." + (i + 1) + "  REPETITION " + repetitions + "/", currentGenerationSteps);
                result.append(generateRecursive(repNode.operand(), currentGenerationSteps));
            }
            addStep(":", currentGenerationSteps);
        } else {
            addStep("Finished REPETITION quantifier." + node.getClass().getName(), currentGenerationSteps);
            
        }
        return result.toString();
    }
}
