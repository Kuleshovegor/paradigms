package expression.generic.element.operation;

import expression.generic.MyExpression;
import expression.generic.calculate.Calculate;

public class Max<T> extends AbstractBinaryOperation<T> {
    private final static String SYMBOL_OF_OPERATION = "max";
    private final static int LEVEL_PRIORITY = 0;

    public Max(MyExpression<T> firstExpression, MyExpression<T> secondExpression) {
        super(firstExpression, secondExpression, SYMBOL_OF_OPERATION, LEVEL_PRIORITY);
    }

    public static String getSymbolOfOperation() {
        return SYMBOL_OF_OPERATION;
    }

    @Override
    public boolean isAssociative() {
        return true;
    }

    @Override
    public T evaluate(Calculate<T> calculate, final T x, final T y, final T z) {
        return calculate.max(firstArgument.evaluate(calculate, x, y, z), secondArgument.evaluate(calculate, x, y, z));
    }
}
