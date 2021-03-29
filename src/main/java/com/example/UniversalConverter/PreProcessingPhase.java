package com.example.UniversalConverter;

import com.example.UniversalConverter.Exceptions.IncorrectDimensionException;

public class PreProcessingPhase {

    public static void checkDimension(final Expression expression)
            throws IncorrectDimensionException {
        if (!expression.isConversionAvailable()) {
            throw new IncorrectDimensionException();
        }
    }

    public static Expression combine(Expression firstExpression, Expression secondExpression) {
        return secondExpression.multiply(firstExpression.invert());
    }

    public static Expression preprocessing(Expression fromExpression, Expression toExpression)
            throws IncorrectDimensionException {
        Expression expression = combine(fromExpression, toExpression);
        checkDimension(expression);
        return expression;
    }

}
