package expression.generic.calculate;

import expression.generic.exceptions.evaluateException.DivisionByZero;

import java.math.BigInteger;

public class BigIntegerCalculate implements Calculate<BigInteger> {

    @Override
    public BigInteger add(BigInteger firstArgument, BigInteger secondArgument) {
        return firstArgument.add(secondArgument);
    }

    @Override
    public BigInteger subtraction(BigInteger firstArgument, BigInteger secondArgument) {
        return firstArgument.subtract(secondArgument);
    }

    @Override
    public BigInteger multiply(BigInteger firstArgument, BigInteger secondArgument) {
        return firstArgument.multiply(secondArgument);
    }

    @Override
    public BigInteger divide(BigInteger firstArgument, BigInteger secondArgument) {
        if (secondArgument.equals(BigInteger.ZERO)) {
            throw new DivisionByZero("Division by zero." + firstArgument + " / " + secondArgument);
        }
        return firstArgument.divide(secondArgument);
    }

    @Override
    public BigInteger min(BigInteger firstArgument, BigInteger secondArgument) {
        return firstArgument.min(secondArgument);
    }

    @Override
    public BigInteger max(BigInteger firstArgument, BigInteger secondArgument) {
        return firstArgument.max(secondArgument);
    }

    @Override
    public BigInteger negate(BigInteger argument) {
        return argument.negate();
    }

    @Override
    public BigInteger count(BigInteger argument) {
        return new BigInteger(String.valueOf(argument.bitCount()));
    }
}
