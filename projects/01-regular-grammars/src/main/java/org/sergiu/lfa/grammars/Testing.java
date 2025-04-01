package org.sergiu.lfa.grammars;

import java.io.IOException;

public class Testing {

    GrammarParser grammarParser = new GrammarParser();

    public void test() throws IOException {
        String filePath = "rules.txt";
        Grammar grammar = grammarParser.parseFromFile(filePath);
    }
}
