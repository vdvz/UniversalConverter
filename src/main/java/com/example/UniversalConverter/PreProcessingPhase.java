package com.example.UniversalConverter;

import com.example.UniversalConverter.Exceptions.IncorrectDimensionException;

public class PreProcessingPhase {

    public static void checkDimension(final Expression from, final Expression to)
            throws IncorrectDimensionException {
        if (!from.isConversionAvailable(to)) {
            throw new IncorrectDimensionException();
        }
    }

    public static Expression combine(Expression firstExpression, Expression secondExpression) {
        return secondExpression.multiply(firstExpression.invert());
    }

    public static Expression preprocessing(Expression fromExpression, Expression toExpression)
            throws IncorrectDimensionException {
        checkDimension(fromExpression, toExpression);
        return null;
    }

}
