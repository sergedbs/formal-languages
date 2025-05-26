package org.sergedb.fla.lexer;

import org.sergedb.fla.lexer.model.Token;
import org.sergedb.fla.lexer.model.TokenType;

import java.util.ArrayList;
import java.util.List;

public class Lexer {
    private final String input;
    private int position;

    public Lexer(String input) {
        this.input = input;
        this.position = 0;
    }

    private char getCurrentChar() {
        return (position < input.length()) ? input.charAt(position) : '\0';
    }

    private void advance() {
        position++;
    }

    private void advance(int steps) {
        position += steps;
    }

    private String matchNumber() {
        if (position >= input.length() || !Character.isDigit(getCurrentChar())) {
            return null;
        }

        int start = position;
        boolean hasDecimal = false;

        while (position < input.length() &&
                (Character.isDigit(getCurrentChar()) || getCurrentChar() == '.')) {
            if (getCurrentChar() == '.') {
                if (hasDecimal) {
                    break;
                }
                hasDecimal = true;
            }
            advance();
        }

        return input.substring(start, position);
    }


    private String matchKeyword(String keyword) {
        if (position + keyword.length() <= input.length() &&
                input.startsWith(keyword, position)) {

            int endPos = position + keyword.length();
            if (endPos >= input.length() || !Character.isLetter(input.charAt(endPos))) {
                return keyword;
            }
        }
        return null;
    }

    private void skipWhitespace() {
        while (position < input.length() && Character.isWhitespace(getCurrentChar())) {
            advance();
        }
    }

    public Token getNextToken() {

        skipWhitespace();

        if (position >= input.length()) {
            return new Token(TokenType.EOL, "");
        }

        String number = matchNumber();
        if (number != null) {
            return new Token(TokenType.NUMBER, number);
        }

        String keyword;
        if ((keyword = matchKeyword("sin")) != null) {
            advance(3);
            return new Token(TokenType.SIN, keyword);
        }

        if ((keyword = matchKeyword("cos")) != null) {
            advance(3);
            return new Token(TokenType.COS, keyword);
        }

        if ((keyword = matchKeyword("tan")) != null) {
            advance(3);
            return new Token(TokenType.TAN, keyword);
        }


        char currentChar = getCurrentChar();
        advance();

        return switch (currentChar) {
            case '+' -> new Token(TokenType.PLUS, "+");
            case '-' -> new Token(TokenType.MINUS, "-");
            case '*' -> new Token(TokenType.MULTIPLY, "*");
            case '/' -> new Token(TokenType.DIVIDE, "/");
            case '^' -> new Token(TokenType.POWER, "^");
            case '(' -> new Token(TokenType.LPAREN, "(");
            case ')' -> new Token(TokenType.RPAREN, ")");
            default -> new Token(TokenType.INVALID, String.valueOf(currentChar));
        };
    }

    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();
        Token token;
        position = 0;

        while ((token = getNextToken()).type() != TokenType.EOL) {
            tokens.add(token);
        }

        return tokens;
    }
}
