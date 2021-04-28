package expression.generic.calculate;

import expression.generic.exceptions.evaluateException.DivisionByZero;
import expression.generic.exceptions.evaluateException.OverflowException;

public class IntegerCalculate implements Calculate<Integer> {
    public static boolean checkAddOverflow(Integer first, Integer second) {
        return (first > 0 && second > 0 && first > Integer.MAX_VALUE - second) ||
                (first < 0 && second < 0 && first < Integer.MIN_VALUE - second);
    }

    public static boolean checkSubOverflow(Integer first, Integer second) {
        return (first < 0 && second > 0 && first < Integer.MIN_VALUE + second)
                || first >= 0 && second < 0 && first > Integer.MAX_VALUE + second;
    }

    public static boolean checkMulOverflow(Integer first, Integer second) {
        return (first > 0 && (second > Integer.MAX_VALUE / first || second < Integer.MIN_VALUE / first)) ||
                (first == -1 && second == Integer.MIN_VALUE) ||
                (first == Integer.MIN_VALUE && second == -1) ||
                (first < -1 && (second < Integer.MAX_VALUE / first ||  second > Integer.MIN_VALUE / first));
    }

    public static boolean checkDivOverflow(Integer first, Integer second) {
        return first == Integer.MIN_VALUE && second == -1;
    }

    public static boolean checkDivisionByZero(Integer first, Integer second) {
        return second == 0;
    }

    public static boolean checkNegOverflow(int value) {
        return value == Integer.MIN_VALUE;
    }


    @Override
    public Integer add(Integer firstArgument, Integer secondArgument) {
        if (checkAddOverflow(firstArgument, secondArgument)) {
            throw new OverflowException("Integer overflow when adding." + firstArgument + " + " + secondArgument);
        }
        return firstArgument + secondArgument;
    }

    @Override
    public Integer subtraction(Integer firstArgument, Integer secondArgument) {
        if (checkSubOverflow(firstArgument, secondArgument)) {
            throw new OverflowException("Integer overflow by subtracting." + firstArgument + " - " + secondArgument);
        }
        return firstArgument - secondArgument;
    }

    @Override
    public Integer multiply(Integer firstArgument, Integer secondArgument) {
        if (checkMulOverflow(firstArgument, secondArgument)) {
            throw new OverflowException("Overflow Integer when multiplying." + firstArgument + " * " + secondArgument);
        }
        return firstArgument * secondArgument;
    }

    @Override
    public Integer divide(Integer firstArgument, Integer secondArgument) {
        if (checkDivisionByZero(firstArgument, secondArgument)) {
            throw new DivisionByZero("Division by zero." + firstArgument + " / " + secondArgument);
        }
        if (checkDivOverflow(firstArgument, secondArgument)) {
            throw new OverflowException("Integer overflow when dividing." + firstArgument + " / " + secondArgument);
        }
        return firstArgument /secondArgument;
    }

    @Override
    public Integer min(Integer firstArgument, Integer secondArgument) {
        return Math.min(firstArgument, secondArgument);
    }

    @Override
    public Integer max(Integer firstArgument, Integer secondArgument) {
        return Math.max(firstArgument, secondArgument);
    }

    @Override
    public Integer negate(Integer argument) {
        if (checkNegOverflow(argument)) {
            throw new OverflowException("Integer overflow on negative " + "-(" + argument + ")");
        }
        return -argument;
    }

    @Override
    public Integer count(Integer argument) {
        return Integer.bitCount(argument);
    }
}
