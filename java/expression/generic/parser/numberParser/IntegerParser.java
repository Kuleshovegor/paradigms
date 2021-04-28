package expression.generic.parser.numberParser;

public class IntegerParser implements NumberParser<Integer> {
    @Override
    public Integer parse(String string) {
        return Integer.parseInt(string);
    }
}
