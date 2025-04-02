package org.sergiu.lfa.grammars;

public class Main {


    public static final String RULES_FILE_PATH = "projects/01-regular-grammars/src/main/resources/rules.txt";
    public static final String REGEX_PATTERN = "(?i)%s\\s*=\\s*\\{\\s*((?:[^{}]|[\\r\\n])*)\\s*}";

    static Runner runner = new Runner();

    public static void main(String[] args) {
        runner.run();
    }
}