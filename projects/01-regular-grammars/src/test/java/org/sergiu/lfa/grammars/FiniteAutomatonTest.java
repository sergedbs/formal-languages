package org.sergiu.lfa.grammars;

import org.junit.Before;
import org.junit.Test;
import org.sergiu.lfa.grammars.automaton.FiniteAutomaton;
import org.sergiu.lfa.grammars.model.Grammar;
import org.sergiu.lfa.grammars.model.Production;
import org.sergiu.lfa.grammars.model.ProductionSymbol;
import org.sergiu.lfa.grammars.model.SymbolType;
import org.sergiu.lfa.grammars.processor.GrammarProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class FiniteAutomatonTest {

    private Grammar grammar;
    private GrammarProcessor grammarProcessor;
    private FiniteAutomaton finiteAutomaton;

    @Before
    public void setUp() {
        grammar = new Grammar(
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
    }


    @Test
    public void testAutomatonConstruction() {
        finiteAutomaton = new FiniteAutomaton(grammar);

        // Check if the automaton has the expected states
        assert finiteAutomaton.getStates().containsAll(grammar.nonTerminals());
        assert finiteAutomaton.getAlphabet().containsAll(grammar.terminals());

        // Check if the start state is correct
        assert finiteAutomaton.getStartState().equals(grammar.startSymbol());

        // Check if the final states are correctly identified
        assert finiteAutomaton.getFinalStates().containsAll(List.of("B", "D"));
    }

    @Test
    public void testStringAcceptance() {
        finiteAutomaton = new FiniteAutomaton(grammar);

        assert finiteAutomaton.accepts("abab");
        assert finiteAutomaton.accepts("bd");
        assert finiteAutomaton.accepts("abcd");
        assert !finiteAutomaton.accepts("aaab");
        assert !finiteAutomaton.accepts("b");
    }

    @Test
    public void testStringGeneration() {
        grammarProcessor = new GrammarProcessor(grammar);
        finiteAutomaton = new FiniteAutomaton(grammar);

        int count = 5;

        // Generate test strings
        List<String> generatedStrings = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            generatedStrings.add(grammarProcessor.generateString());
        }

        // Check if the generated strings are valid
        for (String str : generatedStrings) {
            assert finiteAutomaton.accepts(str);
        }
    }

}
