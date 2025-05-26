package org.sergedb.fla.parser;

import org.sergedb.fla.parser.model.Token;
import org.sergedb.fla.parser.model.TokenType;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;

    private static final Pattern P_SIN = Pattern.compile("\\bsin\\b");
    private static final Pattern P_COS = Pattern.compile("\\bcos\\b");
    private static final Pattern P_TAN = Pattern.compile("\\btan\\b");
    private static final Pattern P_NUMBER = Pattern.compile("[0-9]+(\\.[0-9]+)?");
    private static final Pattern P_PLUS = Pattern.compile("\\+");
    private static final Pattern P_MINUS = Pattern.compile("-");
    private static final Pattern P_MULTIPLY = Pattern.compile("\\*");
    private static final Pattern P_DIVIDE = Pattern.compile("/");
    private static final Pattern P_LPAREN = Pattern.compile("\\(");
    private static final Pattern P_RPAREN = Pattern.compile("\\)");
    private static final Pattern P_WHITESPACE = Pattern.compile("[ \\t\\r]+");
    private static final Pattern P_NEWLINE = Pattern.compile("\\n");

    public Lexer(String source) {
        this.source = source;
    }

    public List<Token> scanTokens() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }
        tokens.add(new Token(TokenType.EOF, "", null, line));
        return tokens;
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private void scanToken() {

        if (matchAndConsume(P_NEWLINE)) {
            line++;
            return;
        }

        if (matchAndConsume(P_WHITESPACE)) {
            // Ignore whitespace
            return; 
        }

        if (matchAndConsume(P_SIN)) {
            addToken(TokenType.SIN);
        } else if (matchAndConsume(P_COS)) {
            addToken(TokenType.COS);
        } else if (matchAndConsume(P_TAN)) {
            addToken(TokenType.TAN);
        } else if (matchAndConsume(P_NUMBER)) {
            addToken(TokenType.NUMBER, Double.parseDouble(source.substring(start, current)));
        } else if (matchAndConsume(P_PLUS)) {
            addToken(TokenType.PLUS);
        } else if (matchAndConsume(P_MINUS)) {
            addToken(TokenType.MINUS);
        } else if (matchAndConsume(P_MULTIPLY)) {
            addToken(TokenType.MULTIPLY);
        } else if (matchAndConsume(P_DIVIDE)) {
            addToken(TokenType.DIVIDE);
        } else if (matchAndConsume(P_LPAREN)) {
            addToken(TokenType.LPAREN);
        } else if (matchAndConsume(P_RPAREN)) {
            addToken(TokenType.RPAREN);
        } else {
            if (!isAtEnd()) {
                char unknownChar = source.charAt(current);
                current++; 
                addToken(TokenType.UNKNOWN, String.valueOf(unknownChar));
            }
        }
    }

    private boolean matchAndConsume(Pattern pattern) {
        Matcher matcher = pattern.matcher(source);
        if (matcher.find(current) && matcher.start() == current) {
            current = matcher.end();
            return true;
        }
        return false;
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }
}
