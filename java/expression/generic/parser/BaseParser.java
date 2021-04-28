package expression.generic.parser;

import expression.generic.exceptions.parserException.ParserException;

public class BaseParser {
    private final ExpressionSource source;
    protected char ch;

    protected BaseParser(final ExpressionSource source) {
        this.source = source;
    }

    protected void nextChar() {
        ch = source.hasNext() ? source.next() : '\0';
    }

    boolean hasNextChar() {
        return source.hasNext();
    }

    char getChar() {
        char res = ch;
        nextChar();
        return res;
    }

    protected boolean test(char expected) {
        if (ch == expected) {
            nextChar();
            return true;
        }
        return false;
    }

    protected boolean test(String expected) {
        if (source.test(expected)) {
            nextChar();
            return true;
        } else {
            return false;
        }
    }

    protected void expect(final char c) throws ParserException {
        if (ch != c) {
            throw error("Expected '" + c + "', found '" + ch + "'");
        }
        nextChar();
    }

    protected void expect(final String value) throws ParserException {
        for (char c : value.toCharArray()) {
            expect(c);
        }
    }

    protected int getPos() {
        return source.getPos();
    }

    protected ParserException error(final String message) {
        return source.error("pos: " + getPos() + " " + message);
    }

    protected boolean between(final char from, final char to) {
        return from <= ch && ch <= to;
    }
}

