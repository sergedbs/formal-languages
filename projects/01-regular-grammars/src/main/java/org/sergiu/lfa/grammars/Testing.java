package org.sergiu.lfa.grammars;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.sergiu.lfa.grammars.Main.RULES_FILE_PATH;

public class Testing {

    GrammarParser grammarParser = new GrammarParser();

    public void test() throws IOException {
        String filePath = RULES_FILE_PATH;

        Path path = Path.of(filePath);
        if (!Files.exists(path)) {
            throw new IOException("File does not exist: " + Path.of(filePath));
        }

        Grammar grammar = grammarParser.parseFromFile(path);
    }
}
