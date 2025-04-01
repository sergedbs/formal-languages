package org.sergiu.lfa.grammars;

import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class GrammarParserTest {

    Grammar expected;
    Grammar actual;

    GrammarProcessor expectedGrammar;
    GrammarProcessor actualGrammar;

    @Before
    public void setUp() {
        GrammarParser parser = new GrammarParser();

        expected = new Grammar(
                Set.of("S", "B", "D"),
                Set.of("a", "b", "c", "d"),
                "S",
                List.of(
                        new GrammarRule("S", List.of("a", "S")),
                        new GrammarRule("S", List.of("b", "B")),
                        new GrammarRule("B", List.of("c", "B")),
                        new GrammarRule("B", List.of("d")),
                        new GrammarRule("B", List.of("a", "D")),
                        new GrammarRule("D", List.of("a", "B")),
                        new GrammarRule("D", List.of("b"))
                )
        );
        expectedGrammar = new GrammarProcessor(expected);

        String input = """
                V_N={S, B, D}
                V_T={a, b, c, d}
                P={ S -> aS,
                    S -> bB,
                    B -> cB,
                    B -> d,
                    B -> aD,
                    D -> aB,
                    D -> b\s
                }""";

        actual = parser.parseFromString(input);
        actualGrammar = new GrammarProcessor(actual);
    }

    @Test
    public void testNonTerminalsParse() {
        assertEquals(expectedGrammar.getNonTerminals(), actualGrammar.getNonTerminals());
    }

    @Test
    public void testTerminalsParse() {
        assertEquals(expectedGrammar.getTerminals(), actualGrammar.getTerminals());
    }

}
