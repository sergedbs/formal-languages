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
                Set.of(
                        new GrammarRule("S", List.of(new TokenRHS("a", SymbolType.TERMINAL), new TokenRHS("S", SymbolType.NON_TERMINAL))),
                        new GrammarRule("S", List.of(new TokenRHS("b", SymbolType.TERMINAL), new TokenRHS("B", SymbolType.NON_TERMINAL))),
                        new GrammarRule("B", List.of(new TokenRHS("c", SymbolType.TERMINAL), new TokenRHS("B", SymbolType.NON_TERMINAL))),
                        new GrammarRule("B", List.of(new TokenRHS("d", SymbolType.TERMINAL))),
                        new GrammarRule("B", List.of(new TokenRHS("a", SymbolType.TERMINAL), new TokenRHS("D", SymbolType.NON_TERMINAL))),
                        new GrammarRule("D", List.of(new TokenRHS("a", SymbolType.TERMINAL), new TokenRHS("B", SymbolType.NON_TERMINAL))),
                        new GrammarRule("D", List.of(new TokenRHS("b", SymbolType.TERMINAL)))
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

    @Test
    public void testRulesParse() {
        assertEquals(expectedGrammar.getRules(), actualGrammar.getRules());
    }

    @Test
    public void testGrammarParse() {
        assertEquals(expected, actual);
    }

}
