package expression.generic.parser;

import expression.generic.exceptions.parserException.ParserException;

public class StringSource implements ExpressionSource {
    private final String data;
    private int pos;

    public StringSource(final String data) {
        this.data = data;
    }

    @Override
    public boolean hasNext() {
        return pos < data.length();
    }

    @Override
    public char next() {
        return data.charAt(pos++);
    }

   @Override
   public int getPos() {
        return pos - 1;
   }

    @Override
    public boolean test(String string) {
        if (string.length() > data.length() - pos) {
            return false;
        }
        for (int ind = 0; ind < string.length(); ind++) {
            if (string.charAt(ind) != data.charAt(ind + pos - 1)) {
                return false;
            }
        }
        pos += string.length() - 1;
        return true;
    }

    @Override
    public ParserException error(final String message) {
        return new ParserException(pos + ": " + message);
    }
}
