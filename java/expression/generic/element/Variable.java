package expression.generic.element;

import expression.generic.CommonExpression;
import expression.generic.MyExpression;
import expression.generic.calculate.Calculate;

import java.util.Objects;

public class Variable<T> implements MyExpression<T>, CommonExpression<T> {
    private String nameVariable;
    private final static int levelPriority = 4;

    public Variable(String nameVariable){
        if (nameVariable != "x" && nameVariable != "y" && nameVariable != "z") {
            throw new IllegalArgumentException("Error names variable. Expected \"x\", \"y\" or \"z\", found: " + nameVariable);
        }
        this.nameVariable = nameVariable;
    }

    @Override
    public boolean isOperation() {
        return false;
    }

    @Override
    public int getLevelPriority() {
        return levelPriority;
    }

    public String getNameVariable() {
        return nameVariable;
    }
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        return getClass() == obj.getClass() && nameVariable.equals(((Variable<T>) obj).getNameVariable());
    }

    @Override
    public int hashCode() {
        return Objects.hash(nameVariable);
    }

    @Override
    public T evaluate(Calculate<T> calculate, final T x, final T y, final T z) {
        if (nameVariable.equals("x")) {
            return x;
        } else if (nameVariable.equals("y")) {
            return y;
        } else {
            return z;
        }
    }

    @Override
    public String toString() {
        return nameVariable;
    }

    @Override
    public String toMiniString() {
        return nameVariable;
    }
}
