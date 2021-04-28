package expression.generic.element;

import expression.generic.CommonExpression;
import expression.generic.MyExpression;
import expression.generic.calculate.Calculate;

import java.util.Objects;

public class Const<T> implements MyExpression<T>, CommonExpression<T> {
    private final T value;
    private final static int levelPriority = 4;

    public Const(T value) {
        this.value = value;
    }

    public int getLevelPriority() {
        return levelPriority;
    }

    @Override
    public boolean isOperation() {
        return false;
    }

    public T getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        return getClass() == obj.getClass() && ((Const) obj).getValue() == value;

    }

    @Override
    public T evaluate(Calculate<T> calculate, final T x, final T y, final T z) {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public String toMiniString() {
        return String.valueOf(value);
    }
}
