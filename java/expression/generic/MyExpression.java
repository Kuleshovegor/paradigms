package expression.generic;

public interface MyExpression<T> extends TripleExpression<T> {
    int getLevelPriority();
    boolean isOperation();
}
