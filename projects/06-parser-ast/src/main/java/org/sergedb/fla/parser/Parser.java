package org.sergedb.fla.parser;

import org.sergedb.fla.parser.model.ASTNode;
import org.sergedb.fla.parser.model.NodeType;
import org.sergedb.fla.parser.model.Token;
import org.sergedb.fla.parser.model.TokenType;

import java.util.List;

public class Parser {
    private final List<Token> tokens;
    private int position;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.position = 0;
    }

    private Token getCurrentToken() {
        if (position < tokens.size()) {
            return tokens.get(position);
        }
        // Use the existing EOF token from the list if available, otherwise create one.
        // The Lexer should already add an EOF token.
        Token lastToken = tokens.get(tokens.size() - 1);
        if (lastToken.type() == TokenType.EOF) {
            return lastToken;
        }
        return new Token(TokenType.EOF, "", null, 0); // Fallback, should ideally not be reached if lexer is correct
    }

    private void advance() {
        if (position < tokens.size()) { // Ensure we don't advance past the end of actual tokens
            position++;
        }
    }

    public ASTNode parse() {
        ASTNode result = parseExpression();
        if (getCurrentToken().type() != TokenType.EOF) {
            // This check helps ensure the entire input was consumed if it's a valid expression.
            // However, the example doesn't explicitly throw if not EOF, it just stops parsing.
            // Depending on strictness, you might want to throw an error here if not EOF.
            // For now, let's stick to the example's behavior.
        }
        return result;
    }

    // expression -> term ( (PLUS | MINUS) term )*
    private ASTNode parseExpression() {
        ASTNode node = parseTerm();

        while (getCurrentToken().type() == TokenType.PLUS ||
               getCurrentToken().type() == TokenType.MINUS) {
            Token operatorToken = getCurrentToken();
            advance();
            ASTNode right = parseTerm();
            // The ASTNode in your project takes NodeType and a String value.
            // The value for an operation can be the operator's lexeme or its type name.
            // Example used op.getType().toString()
            ASTNode operationNode = new ASTNode(NodeType.BINARY_OPERATION, operatorToken.lexeme());
            operationNode.addChild(node);
            operationNode.addChild(right);
            node = operationNode;
        }
        return node;
    }

    // term -> factor ( (MULTIPLY | DIVIDE) factor )*
    private ASTNode parseTerm() {
        ASTNode node = parseFactor();

        while (getCurrentToken().type() == TokenType.MULTIPLY ||
               getCurrentToken().type() == TokenType.DIVIDE) {
            Token operatorToken = getCurrentToken();
            advance();
            ASTNode right = parseFactor();
            ASTNode operationNode = new ASTNode(NodeType.BINARY_OPERATION, operatorToken.lexeme());
            operationNode.addChild(node);
            operationNode.addChild(right);
            node = operationNode;
        }
        return node;
    }

    // factor -> NUMBER | ( LPAREN expression RPAREN ) | ( (SIN | COS | TAN) LPAREN expression RPAREN ) | ( MINUS factor )
    private ASTNode parseFactor() {
        Token token = getCurrentToken();

        if (token.type() == TokenType.NUMBER) {
            advance();
            // ASTNode constructor expects a String value for the number.
            return new ASTNode(NodeType.NUMBER, token.lexeme());
        } else if (token.type() == TokenType.SIN || token.type() == TokenType.COS || token.type() == TokenType.TAN) {
            Token functionToken = token;
            advance();
            expect(TokenType.LPAREN);
            ASTNode argument = parseExpression();
            expect(TokenType.RPAREN);
            // The value for a function call can be the function's name (lexeme).
            ASTNode funcNode = new ASTNode(NodeType.FUNCTION_CALL, functionToken.lexeme());
            funcNode.addChild(argument);
            return funcNode;
        } else if (token.type() == TokenType.LPAREN) {
            advance();
            ASTNode node = parseExpression();
            expect(TokenType.RPAREN);
            return node;
        } else if (token.type() == TokenType.MINUS) { // Handling unary minus
            Token operatorToken = token;
            advance();
            ASTNode operand = parseFactor(); // Recursively call parseFactor for the operand
            ASTNode unaryNode = new ASTNode(NodeType.UNARY_OPERATION, operatorToken.lexeme());
            unaryNode.addChild(operand);
            return unaryNode;
        }else {
            throw new ParseException("Unexpected token: " + token.lexeme() + " of type " + token.type(), token);
        }
    }

    private void expect(TokenType type) {
        Token current = getCurrentToken();
        if (current.type() == type) {
            advance();
        } else {
            throw new ParseException("Expected token type " + type + " but got " + current.type() + " with lexeme '" + current.lexeme() + "'", current);
        }
    }

    // Custom ParseException class
    public static class ParseException extends RuntimeException {
        public ParseException(String message, Token token) {
            super(message + " at line " + token.line() + " near '" + token.lexeme() + "'");
        }
    }
}
