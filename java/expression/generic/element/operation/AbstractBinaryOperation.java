package expression.generic.element.operation;

import expression.generic.CommonExpression;
import expression.generic.MyExpression;

import java.util.Objects;

public abstract class AbstractBinaryOperation<T> implements MyExpression<T>, CommonExpression<T> {
    protected final MyExpression<T> firstArgument;
    protected final MyExpression<T> secondArgument;
    private final String symbolOfOperation;
    private final int levelPriority;

    public AbstractBinaryOperation(final MyExpression<T> firstArgument, final MyExpression<T> secondArgument, final String symbolOfOperation, final int levelPriority) {
        this.firstArgument = firstArgument;
        this.secondArgument = secondArgument;
        this.symbolOfOperation = symbolOfOperation;
        this.levelPriority = levelPriority;
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstArgument, secondArgument, symbolOfOperation, levelPriority);
    }

    @Override
    public boolean isOperation() {
        return true;
    }

    @Override
    public int getLevelPriority(){
        return levelPriority;
    }


    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        return this.getClass() == obj.getClass() && firstArgument.equals(((AbstractBinaryOperation) obj).firstArgument) &&
                secondArgument.equals(((AbstractBinaryOperation) obj).secondArgument);
    }

    @Override
    public String toString() {
        return "(" + firstArgument.toString() + " " + symbolOfOperation + " " + secondArgument.toString() + ")";
    }

    @Override
    public String toMiniString() {
        StringBuilder builder = new StringBuilder();
        if (firstArgument.getLevelPriority() < this.getLevelPriority()) {
            builder.append("(").append(firstArgument.toMiniString()).append(")");
        } else {
            builder.append(firstArgument.toMiniString());
        }
        builder.append(" ").append(symbolOfOperation).append(" ");
        if (secondArgument.getLevelPriority() < this.getLevelPriority() && secondArgument.isOperation() || !(isAssociative()) && secondArgument.isOperation() ||
                getClass() == Multiply.class && secondArgument.getClass() == Divide.class) {
            builder.append("(").append(secondArgument.toMiniString()).append(")");
        } else  {
            builder.append(secondArgument.toMiniString());
        }
        return builder.toString();
    }

    public abstract boolean isAssociative();
}
