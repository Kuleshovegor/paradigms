package expression.generic.calculate;

public class DoubleCalculate implements Calculate<Double> {

    @Override
    public Double add(Double firstArgument, Double secondArgument) {
        return firstArgument + secondArgument;
    }

    @Override
    public Double subtraction(Double firstArgument, Double secondArgument) {
        return firstArgument - secondArgument;
    }

    @Override
    public Double multiply(Double firstArgument, Double secondArgument) {
        return firstArgument * secondArgument;
    }

    @Override
    public Double divide(Double firstArgument, Double secondArgument) {
        return firstArgument / secondArgument;
    }

    @Override
    public Double min(Double firstArgument, Double secondArgument) {
        return Math.min(firstArgument, secondArgument);
    }

    @Override
    public Double max(Double firstArgument, Double secondArgument) {
        return Math.max(firstArgument, secondArgument);
    }

    @Override
    public Double negate(Double argument) {
        return -argument;
    }

    @Override
    public Double count(Double argument) {
        return (double) Long.bitCount(Double.doubleToLongBits(argument));
    }
}
