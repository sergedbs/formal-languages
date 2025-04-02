package org.sergiu.lfa.grammars;

import java.util.List;

public record GrammarRule(String left, List<TokenRHS> right) {

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(left).append(" --> ");
        if (right.isEmpty()) {
            sb.append("ε");
        } else {
            for (TokenRHS token : right) {
                sb.append(token).append(" ");
            }
        }
        return sb.toString().trim();
    }

}

