package expression.generic.parser.numberParser;

public class DoubleParser implements NumberParser<Double> {
    @Override
    public Double parse(String string) {
        return Double.parseDouble(string);
    }
}
