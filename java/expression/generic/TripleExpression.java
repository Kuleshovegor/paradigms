package expression.generic;

import expression.generic.ToMiniString;
import expression.generic.calculate.Calculate;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public interface TripleExpression<T> extends ToMiniString {
    T evaluate(Calculate<T> calculate, T x, T y, T z);
}