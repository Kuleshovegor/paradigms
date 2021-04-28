package expression.generic.parser;

import expression.generic.TripleExpression;
import expression.generic.parser.numberParser.NumberParser;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public interface Parser<T> {
    TripleExpression parse(NumberParser<T> numberParser, String expression) throws Exception;
}