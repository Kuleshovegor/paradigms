package expression.generic.parser;

import expression.generic.exceptions.parserException.ParserException;

public interface ExpressionSource {
    boolean hasNext();
    char next();
    boolean test(String string);
    int getPos();
    ParserException error(final String message);
}
