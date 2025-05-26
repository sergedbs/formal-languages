package org.sergedb.fla.automata.parser;

import org.junit.Test;
import org.sergedb.fla.automata.model.Automaton;
import org.sergedb.fla.automata.model.Transition;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class AutomatonParserTest {

    private final AutomatonParser parser = new AutomatonParser();

    @Test
    public void testParseValidAutomatonFromFile() throws IOException {
        String automatonDefinition = """
                Q = {q0,q1,q2}
                ∑ = {a,b,c}
                F = {q2}
                delta = {
                    (q0,a) = q0,
                    (q0,b) = q1,
                    (q1,c) = q1,
                    (q1,c) = q2,
                    (q2,a) = q0,
                    (q1,a) = q1
                }
                """;

        Automaton fa = parser.parseFromString(automatonDefinition);

        assertNotNull(fa);
        assertEquals(Set.of("q0", "q1", "q2"), fa.states());
        assertEquals(Set.of("a", "b", "c"), fa.alphabet());
        assertEquals("q0", fa.initialState());
        assertEquals(Set.of("q2"), fa.finalStates());

        Set<Transition> expectedTransitions = new HashSet<>();
        expectedTransitions.add(new Transition("q0", "q0", "a"));
        expectedTransitions.add(new Transition("q0", "q1", "b"));
        expectedTransitions.add(new Transition("q1", "q1", "c"));
        expectedTransitions.add(new Transition("q1", "q2", "c"));
        expectedTransitions.add(new Transition("q2", "q0", "a"));
        expectedTransitions.add(new Transition("q1", "q1", "a"));

        assertEquals(expectedTransitions, fa.transitions());
    }

    @Test
    public void testParseAutomatonWithEmptyFinalStates() {
        String automatonDefinition = """
                Q = {q0, q1}
                ∑ = {a}
                F = {}
                delta = {
                    (q0,a) = q1
                }
                """;
        Automaton fa = parser.parseFromString(automatonDefinition);
        assertNotNull(fa);
        assertEquals(Set.of("q0", "q1"), fa.states());
        assertEquals(Set.of("a"), fa.alphabet());
        assertEquals("q0", fa.initialState());
        assertTrue(fa.finalStates().isEmpty());

        Set<Transition> expectedTransitions = new HashSet<>();
        expectedTransitions.add(new Transition("q0", "q1", "a"));
        assertEquals(expectedTransitions, fa.transitions());
    }

    @Test
    public void testParseAutomatonWithNoTransitions() {
        String automatonDefinition = """
                Q = {q0}
                ∑ = {a}
                F = {q0}
                delta = {}
                """;
        Automaton fa = parser.parseFromString(automatonDefinition);
        assertNotNull(fa);
        assertEquals(Set.of("q0"), fa.states());
        assertEquals(Set.of("a"), fa.alphabet());
        assertEquals("q0", fa.initialState());
        assertEquals(Set.of("q0"), fa.finalStates());
        assertTrue(fa.transitions().isEmpty());
    }

    @Test
    public void testParseWithSigmaSymbolAndDifferentSpacing() {
        String automatonDefinition = """
                Q = {s1, s2}
                Sigma = {0,1}
                F = {s2}
                delta = { 
                         (s1, 0) = s1, 
                         (s1, 1) = s2, 
                         (s2, 0) = s2, 
                         (s2, 1) = s1 
                       }
                """;
        Automaton fa = parser.parseFromString(automatonDefinition);
        assertNotNull(fa);
        assertEquals(Set.of("s1", "s2"), fa.states());
        assertEquals(Set.of("0", "1"), fa.alphabet());
        assertEquals("s1", fa.initialState());
        assertEquals(Set.of("s2"), fa.finalStates());

        Set<Transition> expectedTransitions = new HashSet<>();
        expectedTransitions.add(new Transition("s1", "s1", "0"));
        expectedTransitions.add(new Transition("s1", "s2", "1"));
        expectedTransitions.add(new Transition("s2", "s2", "0"));
        expectedTransitions.add(new Transition("s2", "s1", "1"));
        assertEquals(expectedTransitions, fa.transitions());
    }

    // --- Tests for malformed input ---

    @Test
    public void testMissingStatesDefinition() {
        String automatonDefinition = """
                ∑ = {a,b}
                F = {q0}
                """;
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            parser.parseFromString(automatonDefinition);
        });
        assertTrue(exception.getMessage().contains("States (Q) definition not found"));
    }

    @Test
    public void testMissingAlphabetDefinition() {
        String automatonDefinition = """
                Q = {q0,q1}
                F = {q1}
                """;
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            parser.parseFromString(automatonDefinition);
        });
        assertTrue(exception.getMessage().contains("Alphabet (∑ or Sigma) definition not found"));
    }

    @Test
    public void testMissingFinalStatesDefinition() {
        String automatonDefinition = """
                Q = {q0,q1}
                ∑ = {a,b}
                """;
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            parser.parseFromString(automatonDefinition);
        });
        assertTrue(exception.getMessage().contains("Final States (F) definition not found"));
    }

    @Test
    public void testTransitionWithUndefinedState() {
        String automatonDefinition =
                "Q = {q0}\n" +
                        "∑ = {a}\n" +
                        "F = {q0}\n" +
                        "delta = { (q0,a) = q1 }"; // q1 is not in Q

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            parser.parseFromString(automatonDefinition);
        });
        assertTrue(exception.getMessage().contains("Transition error: State 'q1'"));
    }

    @Test
    public void testTransitionWithUndefinedSymbol() {
        String automatonDefinition = """
                Q = {q0, q1}
                ∑ = {a}
                F = {q1}
                delta = { (q0,b) = q1 }
                """; // b is not in ∑
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            parser.parseFromString(automatonDefinition);
        });
        assertTrue(exception.getMessage().contains("Transition error: Symbol 'b'"));
    }

    @Test
    public void testMalformedTransitionBlock() {
        String automatonDefinition = """
                Q = {q0, q1}
                ∑ = {a}
                F = {q1}
                delta = { (q0,a) = q1, (q0,b)  q2 } // Malformed second transition
                """;
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            parser.parseFromString(automatonDefinition);
        });
        assertTrue(exception.getMessage().contains("Transition (δ or delta) block found but no valid transitions could be parsed within it, or they are malformed"));
    }

    @Test
    public void testTransitionBlockPresentButEmpty() {
        String automatonDefinition = """
                Q = {q0}
                ∑ = {a}
                F = {q0}
                delta = {
                    // No transitions here
                }
                """;
        Automaton fa = parser.parseFromString(automatonDefinition);
        assertNotNull(fa);
        assertTrue("Transitions should be empty when block is empty", fa.transitions().isEmpty());
    }

    @Test
    public void testTransitionBlockWithOnlyWhitespace() {
        String automatonDefinition = """
                Q = {q0}
                ∑ = {a}
                F = {q0}
                delta = {
                
                }
                """;
        Automaton fa = parser.parseFromString(automatonDefinition);
        assertNotNull(fa);
        assertTrue("Transitions should be empty when block contains only whitespace", fa.transitions().isEmpty());
    }

    @Test
    public void testAutomatonWithEpsilonTransition() {
        String automatonDefinition = """
                Q = {q0, q1}
                ∑ = {a}
                F = {q1}
                delta = {
                    (q0,ε) = q1,
                    (q0,a) = q0
                }
                """;
        Automaton fa = parser.parseFromString(automatonDefinition);
        assertNotNull(fa);
        assertEquals(Set.of("q0", "q1"), fa.states());
        assertEquals(Set.of("a"), fa.alphabet());
        assertEquals(Set.of("q1"), fa.finalStates());

        Set<Transition> expectedTransitions = new HashSet<>();
        expectedTransitions.add(new Transition("q0", "q1", Transition.EPSILON)); // Use Transition.EPSILON
        expectedTransitions.add(new Transition("q0", "q0", "a"));
        assertEquals(expectedTransitions, fa.transitions());
    }

    @Test
    public void testAutomatonWithEpsilonKeywordTransition() {
        String automatonDefinition = """
                Q = {q0, q1}
                ∑ = {a}
                F = {q1}
                delta = {
                    (q0,epsilon) = q1,
                    (q0,a) = q0
                }
                """;
        Automaton fa = parser.parseFromString(automatonDefinition);
        assertNotNull(fa);

        Set<Transition> expectedTransitions = new HashSet<>();
        expectedTransitions.add(new Transition("q0", "q1", Transition.EPSILON)); // Use Transition.EPSILON
        expectedTransitions.add(new Transition("q0", "q0", "a"));
        assertEquals(expectedTransitions, fa.transitions());
    }

    @Test
    public void testTransitionWithUnknownSymbolIfNotEpsilon() {
        String automatonDefinition = """
                Q = {q0, q1}
                ∑ = {a}
                F = {q1}
                delta = { (q0,unknown) = q1 }
                """; // unknown is not in ∑ and not ε/epsilon
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            parser.parseFromString(automatonDefinition);
        });
        assertTrue(exception.getMessage().contains("Transition error: Symbol 'unknown'"));
    }

    @Test
    public void testFinalStateNotInStatesSet() {
        String automatonDefinition = """
                Q = {q0}
                ∑ = {a}
                F = {q1} 
                """; // q1 is not in Q
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            parser.parseFromString(automatonDefinition);
        });
        assertTrue(exception.getMessage().contains("Final states [q1] are not defined in Q."));
    }

    @Test
    public void testParseFromFileActual() throws IOException {
        Path rulesPath = Paths.get("src/main/resources/rules.txt");
        if (!Files.exists(rulesPath)) {
            System.err.println("Skipping testParseFromFileActual: rules.txt not found at " + rulesPath.toAbsolutePath());
            return;
        }

        Automaton fa = parser.parseFromFile(rulesPath);

        assertNotNull(fa);
        assertEquals(Set.of("q0", "q1", "q2"), fa.states());
        assertEquals(Set.of("a", "b", "c"), fa.alphabet());
        assertEquals("q0", fa.initialState());
        assertEquals(Set.of("q2"), fa.finalStates());

        Set<Transition> expectedTransitions = new HashSet<>();
        expectedTransitions.add(new Transition("q0", "q0", "a"));
        expectedTransitions.add(new Transition("q0", "q1", "b"));
        expectedTransitions.add(new Transition("q1", "q1", "c"));
        expectedTransitions.add(new Transition("q1", "q2", "c"));
        expectedTransitions.add(new Transition("q2", "q0", "a"));
        expectedTransitions.add(new Transition("q1", "q1", "a"));

        assertEquals(expectedTransitions, fa.transitions());
    }
}
