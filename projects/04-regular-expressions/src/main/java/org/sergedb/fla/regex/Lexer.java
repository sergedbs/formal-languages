package org.sergedb.fla.regex;

import org.sergedb.fla.regex.model.Token;
import org.sergedb.fla.regex.model.TokenType;

import java.util.ArrayList;
import java.util.List;

public class Lexer {
    private final String input;
    private int currentPosition = 0;

    public Lexer(String input) {
        this.input = input;
    }

    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();
        while (currentPosition < input.length()) {
            char ch = input.charAt(currentPosition);
            switch (ch) {
                case '(':
                    tokens.add(new Token(TokenType.OPEN_PAREN, "("));
                    currentPosition++;
                    break;
                case ')':
                    tokens.add(new Token(TokenType.CLOSE_PAREN, ")"));
                    currentPosition++;
                    break;
                case '{':
                    tokens.add(new Token(TokenType.OPEN_BRACE, "{"));
                    currentPosition++;
                    break;
                case '}':
                    tokens.add(new Token(TokenType.CLOSE_BRACE, "}"));
                    currentPosition++;
                    break;
                case '*':
                    tokens.add(new Token(TokenType.STAR, "*"));
                    currentPosition++;
                    break;
                case '+':
                    tokens.add(new Token(TokenType.PLUS, "+"));
                    currentPosition++;
                    break;
                case '?':
                    tokens.add(new Token(TokenType.QUESTION, "?"));
                    currentPosition++;
                    break;
                case '|':
                    tokens.add(new Token(TokenType.OR, "|"));
                    currentPosition++;
                    break;
                case ',':
                    tokens.add(new Token(TokenType.COMMA, ","));
                    currentPosition++;
                    break;
                default:
                    if (Character.isDigit(ch)) {
                        StringBuilder sb = new StringBuilder();
                        while (currentPosition < input.length() && Character.isDigit(input.charAt(currentPosition))) {
                            sb.append(input.charAt(currentPosition));
                            currentPosition++;
                        }
                        tokens.add(new Token(TokenType.NUMBER, sb.toString()));
                    } else if (Character.isLetter(ch) || Character.isWhitespace(ch) && ch != ' ' && ch != '\n' && ch != '\r' && ch != '\t') {
                        tokens.add(new Token(TokenType.LITERAL, String.valueOf(ch)));
                        currentPosition++;
                    } else {
                        tokens.add(new Token(TokenType.LITERAL, String.valueOf(ch)));
                        currentPosition++;
                    }
                    break;
            }
        }
        tokens.add(new Token(TokenType.EOL, ""));
        return tokens;
    }

    private TokenType getLastSignificantTokenType(List<Token> tokens) {
        for (int i = tokens.size() - 1; i >= 0; i--) {
            if (tokens.get(i).type() != TokenType.EOL) {
                return tokens.get(i).type();
            }
        }
        return null;
    }
}
