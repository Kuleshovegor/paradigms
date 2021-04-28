package expression.generic.calculate;

public interface Calculate<T> {
    T add(T firstArgument, T secondArgument);
    T subtraction(T firstArgument, T secondArgument);
    T multiply(T firstArgument, T secondArgument);
    T divide(T firstArgument, T secondArgument);
    T min(T firstArgument, T secondArgument);
    T max(T firstArgument, T secondArgument);
    T negate(T argument);
    T count(T argument);
}
