"use strict";

const operation = (...argsExpression) => ((f) => ((...argsVariable) => (argsExpression.map((value) => (value.apply(this, argsVariable))).reduce(f))));



const add = (...args) => operation.apply(this, args)((left, right) => left + right);
const multiply = (...args) => operation.apply(this, args)((left, right) => left * right);
const divide = (...args) => operation.apply(this, args)((left, right) => left / right);
const subtract = (...args) => operation.apply(this, args)((left, right) => left - right);
const negate = (argument) => (...args) => -argument.apply(this, args);
const sin = (argument) => (...args) => Math.sin(argument.apply(this, args));
const cos = (argument) => (...args) => Math.cos(argument.apply(this, args));
const cnst = (value) => (() => value);

const NAME_VARIABLE_TO_NUMBER_IN_ARGUMENTS = {
    "x" : 0,
    "y" : 1,
    "z" : 2
};

const variable = (name) => ((...args) => args[NAME_VARIABLE_TO_NUMBER_IN_ARGUMENTS[name]]);

const pi = cnst(Math.PI);
const e = cnst(Math.E);

function parse(str) {
    const STRING_TO_BINARY_OPERATION = {
        "+" : add,
        "-" : subtract,
        "*" : multiply,
        "/" : divide
    };

    const STRING_TO_UNARY_OPERATION = {
        "sin" : sin,
        "cos" : cos,
        "negate" : negate,
    };

    const STRING_TO_CONST = {
        "e" : e,
        "pi" : pi,
    };

    let stackOperation = [];

    str.split(' ').map(function (token) {
        if (token in STRING_TO_BINARY_OPERATION) {
            const right = stackOperation.pop();
            const left = stackOperation.pop();
            stackOperation.push(STRING_TO_BINARY_OPERATION[token](left, right));
        }
        else if (token in STRING_TO_UNARY_OPERATION) {
            stackOperation.push(STRING_TO_UNARY_OPERATION[token](stackOperation.pop()));
        }
        else if (token in STRING_TO_CONST) {
            stackOperation.push(STRING_TO_CONST[token]);
        }
        else if (token in NAME_VARIABLE_TO_NUMBER_IN_ARGUMENTS) {
            stackOperation.push(variable(token));
        } else if (token.length > 0) {
            stackOperation.push(cnst(parseFloat(token)));
        }
    });
    return stackOperation.pop();
}

