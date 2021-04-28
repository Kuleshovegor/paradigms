package expression.generic.parser;

import expression.generic.CommonExpression;
import expression.generic.element.Const;
import expression.generic.element.Variable;
import expression.generic.element.operation.*;
import expression.generic.exceptions.parserException.*;
import expression.generic.parser.numberParser.NumberParser;

public class ExpressionParser<T> implements Parser<T> {
    @Override
    public CommonExpression<T> parse(NumberParser<T> numberParser, final String source) throws ParserException {
        return parse(numberParser, new StringSource(source));
    }

    public CommonExpression<T> parse(NumberParser<T> numberParser, ExpressionSource source) throws ParserException {
        return new PrivateParser<T>(numberParser, source).parseExpression();
    }
    private static class PrivateParser<T> extends BaseParser {
        private NumberParser<T> numberParser;
        private int MAX_LVL = 5;

        public PrivateParser(NumberParser<T> numberParser, ExpressionSource source) {
            super(source);
            this.numberParser = numberParser;
            nextChar();
        }

        public CommonExpression<T> parseExpression() throws ParserException {
            final CommonExpression<T> result = parseExpressionLvl(MAX_LVL);
            if (test('\0')) {
                return result;
            }
            if (test(')')) {
                throw new NoOpeningParenthesisException("Pos: " + getPos() + ". There is not open parenthesis in begin expression.");
            }
            StringBuilder sb = new StringBuilder();
            while (hasNextChar()){
                sb.append(getChar());
            }
            throw new UnexpectedEndException("End of expression expected. Found: \"" + sb +"\"");
        }

        private CommonExpression<T> parseExpressionLvl(int lvl) throws ParserException {
            if (lvl == 1) {
                return parseElement();
            }
            else {
                skipWhitespace();
                CommonExpression<T> firstArgument = parseExpressionLvl(lvl - 1);
                while (true) {
                    skipWhitespace();
                    String sym = getSym(lvl);
                    if (sym == null) {
                        return firstArgument;
                    }
                    skipWhitespace();
                    CommonExpression<T> secondArgument = parseExpressionLvl(lvl - 1);
                    firstArgument = getOperation(firstArgument, secondArgument, sym);
                }
            }
        }

        private String getSym(int lvl) {
            if (lvl == 3 && test("*") ) {
                return "*";
            } else if (lvl == 3 && test("/")) {
                return "/";
            } else if (lvl == 4 && test("+")) {
                return "+";
            } else if (lvl == 4 && test("-")) {
                return "-";
            } else if (lvl == 5 && test("min")) {
                return "min";
            } else if (lvl == 5 && test("max")) {
                return "max";
            }else {
                return null;
            }
        }

        private CommonExpression<T> getOperation(CommonExpression<T> firstArgument, CommonExpression<T> secondArgument, String sym) {
            switch (sym) {
                case "*":
                    return new Multiply<>(firstArgument, secondArgument);
                case "/":
                    return new Divide<>(firstArgument, secondArgument);
                case "+":
                    return new Add<>(firstArgument, secondArgument);
                case "-":
                    return new Subtract<>(firstArgument, secondArgument);
                case "min":
                    return new Min<>(firstArgument, secondArgument);
                case "max":
                    return new Max<>(firstArgument, secondArgument);
                default:
                    throw new IllegalArgumentException("Error symbol expression: '" + sym + "'.");
            }
        }


        private CommonExpression<T> parseElement() throws ParserException {
            skipWhitespace();
            if (test('x')) {
                return new Variable<>("x");
            } else if (test('y')) {
                return new Variable<>("y");
            } else if (test('z')) {
                return new Variable<>("z");
            } else if (test('-')) {
                skipWhitespace();
                if (between('1', '9')) {
                    return new Const<>(parseNegateNumber());
                }
                else {
                    return new Negate<>(parseElement());
                }
            } else if(test("count")) {
                return new Count<>(parseElement());
            } else if (test('(')) {
                skipWhitespace();
                CommonExpression<T> expression = parseExpressionLvl(MAX_LVL);
                skipWhitespace();
                if (test(')')) {
                    return expression;
                } else {
                    throw new NoClosingParenthesisException("Pos: " + getPos() + ". There is not close parenthesis.");
                }
            } else if (between('0','9')){
                return new Const<>(parseNumber());
            } else {
                throw new UnexpectedSymbolException("Pos:" + getPos() + ". Symbol = '" + ch + "'");
            }
        }

        private void copyDigits(final StringBuilder sb) {
            while (between('0', '9')) {
                sb.append(ch);
                nextChar();
            }
        }

        private T parseNumber() throws ParserException {
            return parseNumber("");
        }
        private T parseNegateNumber() throws ParserException {
            return parseNumber("-");
        }

        private T parseNumber(String str) throws ParserException {
            final StringBuilder sb = new StringBuilder(str);
            copyDigits(sb);
            if (ch == '.') {
                sb.append('.');
                nextChar();
            }
            copyDigits(sb);
            try {
                return numberParser.parse(sb.toString());
            } catch (NumberFormatException e) {
                throw new InvalidNumberException("Pos: " + getPos() + ". Invalid number: " + sb);
            }
        }

        private void skipWhitespace() {
            while (test(' ') || test('\r') || test('\n') || test('\t'));
        }

    }
}
