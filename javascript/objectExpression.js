"use strict";

function println() {
    console.log(Array.prototype.map.call(arguments, String).join(' '));
}

const ZERO = new Const(0);
const ONE = new Const(1);

function Operation(...args) {
    this.args = args;
    this.containVariable = this.args.reduce(((pref, value) => pref || value.containVariable), false);
}

Operation.prototype.evaluate = function (...argsVariable) {
    let stack = this.args.map(value => value.evaluate(...argsVariable)).reverse();
    if (this.arity === 1) {
        return this.func(stack[0]);
    }
    if (this.arity === 0) {
        return 0;
    }
    while (stack.length >= this.arity) {
        let argument = [];
        for (let i = 0; i < this.arity; i++) {
            argument.push(stack.pop());
        }
        stack.push(this.func.apply(this, argument));
    }
    return stack[0];
};

Operation.prototype.toString = function () {
    return this.args.map((value => value.toString())).join(" ") + " " + this.symbol;
};

Operation.prototype.prefix = function () {
    return "(" + this.symbol + " " + this.args.map(value => value.prefix()).join(" ") + ")";
};

Operation.prototype.postfix = function () {
    return "(" + this.args.map(value => value.postfix()).join(" ") + " " + this.symbol + ")";
};

Operation.prototype.equals = function (expression) {
    if (this.constructor === expression.constructor && this.args.length === expression.args.length) {
        return this.args.reduce((pref, value, ind) => pref && (value.equals(expression.args[ind])),  true);
    }
    return false;
};

Operation.prototype.simplify = function () {
    if (this.containVariable === false) {
        return new Const(this.evaluate());
    }
    const args = this.args.map(value => value.simplify());
    const result = new this.constructor(...args);
    if (result.containVariable) {
        return result;
    }
    return new Const(result.evaluate());
};

function Add(firstArgument, secondArgument) {
    Operation.call(this, firstArgument, secondArgument);
}
Add.prototype = Object.create(Operation.prototype);
Add.prototype.constructor = Add;
Add.prototype.arity = 2;
Add.prototype.symbol = "+";
Add.prototype.func = (left, right) => left + right;
Add.prototype.diff = function (nameVariable) {
    return new Add(this.args[0].diff(nameVariable), this.args[1].diff(nameVariable));
};
Add.prototype.simplify = function () {
    const res = Operation.prototype.simplify.apply(this);
    if (res.constructor === Add) {
        if (res.args[0].equals(ZERO)) {
            return res.args[1];
        } else if (res.args[1].equals(ZERO)) {
            return res.args[0];
        }
    }
    return res;
};

function Subtract(firstArgument, secondArgument) {
    Operation.call(this, firstArgument, secondArgument);
}
Subtract.prototype = Object.create(Operation.prototype);
Subtract.prototype.constructor = Subtract;
Subtract.prototype.arity = 2;
Subtract.prototype.symbol = "-";
Subtract.prototype.func = (left, right) => left - right;
Subtract.prototype.diff = function (nameVariable) {
    return new Subtract(this.args[0].diff(nameVariable), this.args[1].diff(nameVariable));
};
Subtract.prototype.simplify = function () {
    const res = Operation.prototype.simplify.apply(this);
    if (res.constructor === Subtract) {
        if (res.args[0].equals(ZERO)) {
            return new Negate(res.args[1]);
        } else if (res.args[1].equals(ZERO)) {
            return res.args[0];
        }
    }
    return res;
};

function Multiply(firstArgument, secondArgument) {
    Operation.call(this, firstArgument, secondArgument);
}
Multiply.prototype = Object.create(Operation.prototype);
Multiply.prototype.constructor = Multiply;
Multiply.prototype.arity = 2;
Multiply.prototype.symbol = "*";
Multiply.prototype.func = (left, right) => left * right;
Multiply.prototype.diff = function(nameVariable) {
    return new Add(new Multiply(this.args[0].diff(nameVariable), this.args[1]), new Multiply(this.args[0], this.args[1].diff(nameVariable)));
};
Multiply.prototype.simplify = function () {
    const res = Operation.prototype.simplify.apply(this);
    if (res.constructor === Multiply) {
        if (res.args[0].equals(ZERO) || res.args[1].equals(ZERO)) {
            return new Const(0);
        } else if (res.args[0].equals(ONE)) {
            return res.args[1];
        } else if (res.args[1].equals(ONE)) {
            return res.args[0];
        }
    }
    return res;
};

function Divide(firstArgument, secondArgument) {
    Operation.call(this,  firstArgument, secondArgument);
}
Divide.prototype = Object.create(Operation.prototype);
Divide.prototype.constructor = Divide;
Divide.prototype.arity = 2;
Divide.prototype.symbol = "/";
Divide.prototype.func = (left, right) => left / right;
Divide.prototype.diff = function(nameVariable) {
    return new Divide(new Subtract(new Multiply(this.args[0].diff(nameVariable), this.args[1]), new Multiply(this.args[0], this.args[1].diff(nameVariable))), new Multiply(this.args[1], this.args[1]));
};

Divide.prototype.simplify = function () {
    const res = Operation.prototype.simplify.apply(this);
    if (res.constructor === Divide) {
        if (res.args[0].equals(ZERO)) {
            return new Const(0);
        } else if (res.args[1].equals(ONE)) {
            return res.args[0];
        }
    }
    return res;
};

function Sum(...args) {
    this.arity = args.length;
    Operation.call(this, ...args);
}
Sum.prototype = Object.create(Operation.prototype);
Sum.prototype.constructor = Sum;
Sum.prototype.arity = Number.POSITIVE_INFINITY;
Sum.prototype.symbol = "sum";
Sum.prototype.func = (...arg) => arg.reduce(((pref, value) => value + pref), 0);
Sum.prototype.diff = function (nameVariable) {
    return new Sum(...this.args.map(value => value.diff(nameVariable)));
};

function Avg(...args) {
    this.arity = args.length;
    Operation.call(this, ...args);
}

Avg.prototype = Object.create(Operation.prototype);
Avg.prototype.constructor = Avg;
Avg.prototype.arity = Number.POSITIVE_INFINITY;
Avg.prototype.symbol = "avg";
Avg.prototype.func = (...arg) => arg.reduce(((pref, value) => value + pref), 0) / arg.length;
Avg.prototype.diff = function (nameVariable) {
    return new Avg(...this.args.map(value => value.diff(nameVariable)));
};

function Negate(argument) {
    Operation.call(this, argument);
}

Negate.prototype = Object.create(Operation.prototype);
Negate.prototype.constructor = Negate;
Negate.prototype.arity = 1;
Negate.prototype.symbol = "negate";
Negate.prototype.func = (x) => -x;
Negate.prototype.diff = function (nameVariable) {
    return new Negate(this.args[0].diff(nameVariable));
};

function Sinh(argument) {
    Operation.call(this, argument);
}

Sinh.prototype = Object.create(Operation.prototype);
Sinh.prototype.constructor = Sinh;
Sinh.prototype.arity = 1;
Sinh.prototype.symbol = "sinh";
Sinh.prototype.func = (x) => Math.sinh(x);
Sinh.prototype.diff = function (nameVariable) {
    return new Multiply(new Cosh(this.args[0]), this.args[0].diff(nameVariable));
};

function Cosh(argument) {
    Operation.call(this, argument);
}

Cosh.prototype = Object.create(Operation.prototype);
Cosh.prototype.constructor = Cosh;
Cosh.prototype.arity = 1;
Cosh.prototype.symbol = "cosh";
Cosh.prototype.func = (x) => Math.cosh(x);
Cosh.prototype.diff = function (nameVariable) {
    return new Multiply(new Sinh(this.args[0]), this.args[0].diff(nameVariable));
};

function Const(value) {
    this.value = value;
}

Const.prototype.containVariable = false;
Const.prototype.evaluate = function () {
    return this.value;
};

Const.prototype.toString = function () {
    return String(this.value);
};

Const.prototype.prefix = function() {
    return String(this.value);
};

Const.prototype.postfix = function() {
    return String(this.value);
};

Const.prototype.diff = function () {
    return new Const(0);
};

Const.prototype.simplify = function () {
    return new Const(this.value);
};

Const.prototype.equals = function (expression) {
    if (expression.constructor === Const){
        return this.value === expression.value;
    }
    return false;
};

const NAME_VARIABLE_TO_NUMBER_IN_ARGUMENTS = {
    "x" : 0,
    "y" : 1,
    "z" : 2
};

function Variable(name) {
    this.name = name;
}

Variable.prototype.containVariable = true;

Variable.prototype.evaluate = function () {
    return arguments[NAME_VARIABLE_TO_NUMBER_IN_ARGUMENTS[this.name]];
};

Variable.prototype.toString = function () {
    return this.name;
};

Variable.prototype.prefix = function () {
    return this.name;
};

Variable.prototype.postfix = function () {
    return this.name;
};

Variable.prototype.diff = function (nameVariable) {
    if (nameVariable === this.name){
        return new Const(1);
    } else {
        return new Const(0);
    }
};

Variable.prototype.simplify = function () {
    return new Variable(this.name);
};

Variable.prototype.equals = function (expression) {
    if (expression.constructor === Variable){
        return this.name === expression.name;
    }
    return false;
};

const TYPE_PARSING = {
    PREFIX : 0,
    POSTFIX : 1,
    RPN : 2
};

function Parser (typeParsing) {

    function ParseError(message) {
        this.message = message + " Index: " + index;
    }
    ParseError.prototype = Object.create(Error.prototype);
    ParseError.prototype.constructor = ParseError;
    ParseError.prototype.name = "ParserError";
    function ParseArgumentError(message) {
        ParseError.call(this, message);
    }
    ParseArgumentError.prototype = Object.create(ParseError.prototype);
    ParseArgumentError.prototype.constructor = ParseArgumentError;
    ParseArgumentError.prototype.name = "ParseArgumentError";
    function ParseOperationError(message) {
        ParseError.call(this, message);
    }
    ParseOperationError.prototype = Object.create(ParseError.prototype);
    ParseOperationError.prototype.constructor = ParseOperationError;
    ParseOperationError.prototype.name = "ParseOperationError";
    function MissingCloseBracesError(message) {
        ParseError.call(this, message);
    }
    MissingCloseBracesError.prototype = Object.create(ParseError.prototype);
    MissingCloseBracesError.prototype.constructor = MissingCloseBracesError;
    MissingCloseBracesError.prototype.name = "MissingCloseBracesError";
    function ParseEndError(message) {
        ParseError.call(this, message);
    }
    ParseEndError.prototype = Object.create(ParseError.prototype);
    ParseEndError.prototype.constructor = ParseEndError;
    ParseEndError.prototype.name = "ParseEndError";
    const STRING_TO_OPERATION = {
        "+" : Add,
        "-" : Subtract,
        "*" : Multiply,
        "/" : Divide,
        "sinh" : Sinh,
        "cosh" : Cosh,
        "negate" : Negate,
        "sum" : Sum,
        "avg" : Avg
    };

    let string;
    let index = 0;
    let chr;

    function nextChr() {
        index++;
        chr = string[index];
    }

    function skipWhitespace() {
        while (chr === ' ') nextChr();
    }

    function parseNumber() {
        let result = "";
        if (chr === '-') {
            result = "-";
            nextChr();
        }
        while ('0' <= chr && chr <= '9' || chr === '.') {
            result += chr;
            nextChr();
        }
        result = parseFloat(result);
        if (isNaN(result)) {
            throw new ParseArgumentError("Invalid number.");
        }
        return result;
    }

    function parseOperation() {
        for (const symbol in STRING_TO_OPERATION) {
            if (test(symbol)) {
                index += symbol.length;
                chr = string[index];
                return STRING_TO_OPERATION[symbol];
            }
        }
        throw new ParseOperationError("Wrong operation.");
    }

    function parseArgument() {
        if(test('(')) {
            let result;
            nextChr();
            skipWhitespace();
            result = parseExpression();
            skipWhitespace();
            if (!test(')')) {
                throw new MissingCloseBracesError("Expected \')\'");
            } else {
                nextChr();
                return result;
            }
        }
        for (const nameVariable in NAME_VARIABLE_TO_NUMBER_IN_ARGUMENTS) {
            if (test(nameVariable)) {
                index += nameVariable.length;
                chr = string[index];
                return new Variable(nameVariable);
            }
        }
        if ('0' <= chr && chr <= '9' || chr === '-') {
            return new Const(parseNumber());
        } else {
            throw new ParseArgumentError("Bad argument.");
        }
    }

    function parseArguments() {
        let result = [];
        let prefIndex;
        let goodArgument = true;
        while (index < string.length && goodArgument) {
            try {
                skipWhitespace();
                prefIndex = index;
                result.push(parseArgument());
            } catch (e) {
                index = prefIndex;
                chr = string[index];
                goodArgument = false;
            }
        }
        return result;
    }

    function parseExpression() {
        let operation;
        if (typeParsing === TYPE_PARSING.PREFIX){
            operation = parseOperation();
            if (chr !== ' ' && chr !== '(') {
                throw new ParseOperationError("Expected whitespace or open braces after symbol of operation.");
            }
            skipWhitespace();
        }
        const args = parseArguments();
        skipWhitespace();
        if (typeParsing === TYPE_PARSING.POSTFIX){
            operation = parseOperation();
        }
        if (operation.prototype.arity !== Number.POSITIVE_INFINITY && operation.prototype.arity !== args.length) {
            throw new ParseArgumentError("Wrong number arguments.");
        }
        return new operation(...args);
    }

    function test(operation) {
        if (index + operation.length > string.length) return false;
        for (let i = 0; i < operation.length; i++) {
            if (string[index + i] !== operation[i]) {
                return false;
            }
        }
        return true;
    }

    function parse(str) {
        let stackOperation = [];

        str.split(' ').map(function (token) {
            if (token in STRING_TO_OPERATION) {
                const opr = STRING_TO_OPERATION[token];
                if (opr.prototype.arity === 2) {
                    const right = stackOperation.pop();
                    const left = stackOperation.pop();
                    stackOperation.push(new opr(left, right));
                } else if (opr.prototype.arity === 1) {
                    stackOperation.push(new opr(stackOperation.pop()));
                }
            } else if (token in NAME_VARIABLE_TO_NUMBER_IN_ARGUMENTS) {
                stackOperation.push(new Variable(token));
            } else if (token.length > 0) {
                stackOperation.push(new Const(parseFloat(token)));
            }
        });
        return stackOperation.pop();
    }

    if (typeParsing === TYPE_PARSING.RPN){
        return parse;
    } else if (typeParsing === TYPE_PARSING.PREFIX || typeParsing === TYPE_PARSING.POSTFIX) {
        return function parser(str) {
            string = str;
            index = 0;
            chr = string[0];
            if (str.length === 0) {
                throw new ParseError("Empty input.");
            }
            skipWhitespace();
            const result = parseArgument();
            skipWhitespace();
            if (index !== string.length) {
                throw new ParseEndError("Expected end expression.");
            }
            return result;
        }
    } else {
        throw new ParseError("Wrong type parsing.");
    }
}

const parsePrefix = new Parser(TYPE_PARSING.PREFIX);
const parsePostfix = new Parser(TYPE_PARSING.POSTFIX);
const parse = new Parser(TYPE_PARSING.RPN);