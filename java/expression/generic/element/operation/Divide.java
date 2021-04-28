package expression.generic.element.operation;

import expression.generic.MyExpression;
import expression.generic.calculate.Calculate;

public class Divide<T> extends AbstractBinaryOperation<T> {
    private final static String symbolOfOperation = "/";
    private final static int levelPriority = 2;

    public Divide(MyExpression<T> firstExpression, MyExpression<T> secondExpression) {
        super(firstExpression, secondExpression, symbolOfOperation, levelPriority);
    }

    public static String getSymbolOfOperation() {
        return symbolOfOperation;
    }

    @Override
    public boolean isAssociative() {
        return false;
    }

    @Override
    public T evaluate(Calculate<T> calculate, final T x, final T y, final T z) {
        return calculate.divide(firstArgument.evaluate(calculate, x, y, z), secondArgument.evaluate(calculate, x, y, z));
    }
}
