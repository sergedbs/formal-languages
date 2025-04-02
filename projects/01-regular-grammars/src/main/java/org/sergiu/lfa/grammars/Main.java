package org.sergiu.lfa.grammars;

public class Main {


    public static final String RULES_FILE_PATH = "projects/01-regular-grammars/src/main/resources/rules.txt";
    public static final String REGEX_PATTERN = "(?i)%s\\s*=\\s*\\{\\s*((?:[^{}]|[\\r\\n])*)\\s*}";

    public static void main(String[] args) {
        long startTime = System.nanoTime();
        Runner runner = new Runner();
        runner.run();
        long stopTime = System.nanoTime();
        System.out.println("\n\nExecution time: " + (stopTime - startTime) / 1_000_000 + "ms");
    }
}