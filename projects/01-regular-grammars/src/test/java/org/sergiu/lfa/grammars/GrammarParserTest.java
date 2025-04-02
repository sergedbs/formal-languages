package org.sergiu.lfa.grammars;

import org.junit.Before;
import org.junit.Test;
import org.sergiu.lfa.grammars.model.Grammar;
import org.sergiu.lfa.grammars.model.Production;
import org.sergiu.lfa.grammars.model.ProductionSymbol;
import org.sergiu.lfa.grammars.model.SymbolType;
import org.sergiu.lfa.grammars.parser.GrammarParser;
import org.sergiu.lfa.grammars.processor.GrammarProcessor;

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
                        new Production("S", List.of(new ProductionSymbol("a", SymbolType.TERMINAL), new ProductionSymbol("S", SymbolType.NON_TERMINAL))),
                        new Production("S", List.of(new ProductionSymbol("b", SymbolType.TERMINAL), new ProductionSymbol("B", SymbolType.NON_TERMINAL))),
                        new Production("B", List.of(new ProductionSymbol("c", SymbolType.TERMINAL), new ProductionSymbol("B", SymbolType.NON_TERMINAL))),
                        new Production("B", List.of(new ProductionSymbol("d", SymbolType.TERMINAL))),
                        new Production("B", List.of(new ProductionSymbol("a", SymbolType.TERMINAL), new ProductionSymbol("D", SymbolType.NON_TERMINAL))),
                        new Production("D", List.of(new ProductionSymbol("a", SymbolType.TERMINAL), new ProductionSymbol("B", SymbolType.NON_TERMINAL))),
                        new Production("D", List.of(new ProductionSymbol("b", SymbolType.TERMINAL)))
                )
        );
        expectedGrammar = new GrammarProcessor(expected);

        String input = """
                V_N={S, B, D}
                V_T={a, b, c, d}
                P={ S -> aS | bB,
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
