package org.sergedb.fla.regex;

import org.sergedb.fla.regex.model.Token;
import org.sergedb.fla.regex.model.TokenType;

import java.util.List;

public class Parser {

    public interface RegexNode {}
    public record LiteralNode(String value) implements RegexNode {
        @Override
        public String toString() { return value; }
    }
    public record ConcatNode(RegexNode left, RegexNode right) implements RegexNode {
        @Override
        public String toString() { return String.format("(%s%s)", left, right); }
    }
    public record OrNode(RegexNode left, RegexNode right) implements RegexNode {
        @Override
        public String toString() { return String.format("(%s|%s)", left, right); }
    }
    public record StarNode(RegexNode operand) implements RegexNode { 
        @Override
        public String toString() { return String.format("(%s)*", operand); }
    }
    public record PlusNode(RegexNode operand) implements RegexNode { 
        @Override
        public String toString() { return String.format("(%s)+", operand); }
    }
    public record QuestionNode(RegexNode operand) implements RegexNode { 
        @Override
        public String toString() { return String.format("(%s)?", operand); }
    }
    public record RepetitionNode(RegexNode operand, int minOccurrences, Integer maxOccurrences) implements RegexNode { 
        @Override
        public String toString() {
            if (maxOccurrences == null) {
                return String.format("(%s){%d,}", operand, minOccurrences);
            }
            if (minOccurrences == maxOccurrences) {
                return String.format("(%s){%d}", operand, minOccurrences);
            }
            return String.format("(%s){%d,%d}", operand, minOccurrences, maxOccurrences);
        }
    }

    public static class ParseException extends RuntimeException {
        public ParseException(String message) {
            super(message);
        }
    }

    private final List<Token> tokens;
    private int current = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public RegexNode parse() {
        // Simplified check for empty or effectively empty token list
        if (tokens == null || tokens.isEmpty() || (tokens.size() == 1 && tokens.getFirst().type() == TokenType.EOL)) {
            throw new ParseException("Cannot parse an empty regular expression.");
        }
        RegexNode expression = parseExpr();
        if (!isAtEnd()) {
            throw new ParseException("Expected end of expression, but found token: " + peek() + " at position " + current);
        }
        return expression;
    }

    private RegexNode parseExpr() {
        RegexNode node = parseTerm();
        while (match(TokenType.OR)) {
            RegexNode right = parseTerm(); // parseTerm will handle empty right side
            node = new OrNode(node, right);
        }
        return node;
    }

    private RegexNode parseTerm() {
        if (check(TokenType.OR) || check(TokenType.CLOSE_PAREN) || isAtEnd()) {
            return new LiteralNode(""); // Represents an empty string (epsilon)
        }

        RegexNode node = parseFactor();

        while (check(TokenType.LITERAL) || check(TokenType.NUMBER) || check(TokenType.OPEN_PAREN)) {
            RegexNode right = parseFactor();
            node = new ConcatNode(node, right);
        }
        return node;
    }

    private RegexNode parseFactor() {
        RegexNode node = parseAtom();

        if (match(TokenType.STAR)) {
            node = new StarNode(node);
        } else if (match(TokenType.PLUS)) {
            node = new PlusNode(node);
        } else if (match(TokenType.QUESTION)) {
            node = new QuestionNode(node);
        } else if (check(TokenType.OPEN_BRACE)) {
            advance(); // Consume '{'

            if (!check(TokenType.NUMBER)) {
                throw new ParseException("Expected a number for minimum repetition count after '{', but got " + peek());
            }
            Token minToken = consume(TokenType.NUMBER, "Expected number for minimum repetition count.");
            int min = Integer.parseInt(minToken.value());
            Integer max = min; // Default: {n} means min=max=n

            if (match(TokenType.COMMA)) { // Found a comma, so it's {n,} or {n,m}
                if (check(TokenType.NUMBER)) { // It's {n,m}
                    Token maxToken = consume(TokenType.NUMBER, "Expected number for maximum repetition count after ','.");
                    max = Integer.parseInt(maxToken.value());
                } else { // It's {n,}
                    max = null; // No upper bound
                }
            }
            consume(TokenType.CLOSE_BRACE, "Expected '}' to close repetition quantifier, but got " + peek());

            if (max != null && min > max) {
                throw new ParseException(String.format("Invalid repetition range: min %d cannot be greater than max %d.", min, max));
            }
            if (min < 0) { // Max can't be negative if min isn't, and min > max is checked.
                 throw new ParseException(String.format("Repetition count cannot be negative: %d.", min));
            }
            node = new RepetitionNode(node, min, max);
        }
        return node;
    }

    private RegexNode parseAtom() {
        if (match(TokenType.LITERAL)) {
            return new LiteralNode(previous().value());
        } else if (match(TokenType.NUMBER)) { // Treat NUMBER as a LITERAL
            return new LiteralNode(previous().value());
        } else if (match(TokenType.OPEN_PAREN)) {
            RegexNode expr = parseExpr();
            consume(TokenType.CLOSE_PAREN, "Expected ')' after expression in parentheses.");
            return expr;
        }
        throw new ParseException("Unexpected token in atom: " + peek() + " at position " + current + ". Expected a literal, number, or '('.");
    }

    private Token peek() {
        if (current < tokens.size()) {
            return tokens.get(current);
        }
        // Return EOL if at the end, to prevent IndexOutOfBounds and provide a clear end token
        return new Token(TokenType.EOL, "");
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private boolean isAtEnd() {
        return current >= tokens.size() || tokens.get(current).type() == TokenType.EOL;
    }

    private Token advance() {
        if (!isAtEnd()) {
            current++;
        }
        return previous();
    }

    private boolean check(TokenType type) {
        if (isAtEnd()) {
            return false;
        }
        return peek().type() == type;
    }

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    private Token consume(TokenType type, String message) {
        if (check(type)) {
            return advance();
        }
        throw new ParseException(message + " Found " + peek().type() + " with value '" + peek().value() + "' instead.");
    }
}
