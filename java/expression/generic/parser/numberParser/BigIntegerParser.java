package expression.generic.parser.numberParser;

import java.math.BigInteger;

public class BigIntegerParser implements NumberParser<BigInteger> {
    @Override
    public BigInteger parse(String string) {
        return new BigInteger(string);
    }
}
