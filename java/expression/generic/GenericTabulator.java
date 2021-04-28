package expression.generic;

import expression.generic.calculate.BigIntegerCalculate;
import expression.generic.calculate.Calculate;
import expression.generic.calculate.DoubleCalculate;
import expression.generic.calculate.IntegerCalculate;
import expression.generic.parser.*;
import expression.generic.parser.numberParser.BigIntegerParser;
import expression.generic.parser.numberParser.DoubleParser;
import expression.generic.parser.numberParser.IntegerParser;
import expression.generic.parser.numberParser.NumberParser;

import java.math.BigInteger;

public class GenericTabulator implements Tabulator {
    @Override
    public Object[][][] tabulate(String mode, String expression, int x1, int x2, int y1, int y2, int z1, int z2) throws Exception {
        Object[][][] result = new Object[x2 - x1 + 1][y2 - y1 + 1][z2 - z1 + 1];
        Calculate calculate;
        NumberParser numberParser;
        TripleExpression TripExpression;
        switch (mode) {
            case "i":
                calculate = new IntegerCalculate();
                numberParser = new IntegerParser();
                break;
            case "d":
                calculate = new DoubleCalculate();
                numberParser = new DoubleParser();
                break;
            case "bi":
                calculate = new BigIntegerCalculate();
                numberParser = new BigIntegerParser();
                break;
            default:
                throw new IllegalArgumentException("Illegal mode:" + mode);
        }
        TripExpression = new ExpressionParser<BigInteger>().parse(numberParser, expression);
        for (int i = 0; i <= x2 - x1; i++) {
            for (int j = 0; j <= y2 - y1; j++) {
                for (int k = 0; k <= z2 - z1; k++) {
                    try {
                        result[i][j][k] = TripExpression.evaluate(calculate, numberParser.parse(String.valueOf(x1 + i)), numberParser.parse(String.valueOf(y1 + j)), numberParser.parse(String.valueOf(z1 + k)));
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                }
            }
        }
        return result;
    }
}
