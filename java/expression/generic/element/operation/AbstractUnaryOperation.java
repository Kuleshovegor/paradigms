package expression.generic.element.operation;

import expression.generic.CommonExpression;
import expression.generic.MyExpression;

import java.util.Objects;

public abstract class AbstractUnaryOperation<T> implements MyExpression<T>, CommonExpression<T> {
    protected final MyExpression<T> argument;
    private final int levelPriority;
    private final String symbolOfOperation;

    public AbstractUnaryOperation (MyExpression<T> argument, final String symbolOfOperation, final int levelPriority) {
        this.argument = argument;
        this.symbolOfOperation = symbolOfOperation;
        this.levelPriority = levelPriority;
    }

    @Override
    public int getLevelPriority() {
        return levelPriority;
    }

    @Override
    public boolean isOperation() {
        return true;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        return getClass() == obj.getClass() && argument.equals(((AbstractUnaryOperation) obj).argument);
    }

    @Override
    public int hashCode() {
        return Objects.hash(argument, levelPriority, symbolOfOperation);
    }

    @Override
    public String toString() {
        return symbolOfOperation + "(" + argument.toString() + ")";
    }
}
