package expression.generic.element.operation;

import expression.generic.MyExpression;
import expression.generic.calculate.Calculate;

public class Negate<T> extends AbstractUnaryOperation<T> {
    private final static String SYMBOL_OF_OPERATION = "-";
    private final static int LEVEL_PRIORITY = 2;

    public Negate(MyExpression<T> argument)
    {
        super(argument, SYMBOL_OF_OPERATION, LEVEL_PRIORITY);
    }

    @Override
    public T evaluate(Calculate<T> calculate, final T x, final T y, final T z) {
        return calculate.negate(argument.evaluate(calculate, x, y, z));
    }
}
