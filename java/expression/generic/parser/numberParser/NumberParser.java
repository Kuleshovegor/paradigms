package expression.generic.parser.numberParser;

public interface NumberParser<T> {
    T parse(String string);
}
