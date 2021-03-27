package com.example.UniversalConverter;

import com.example.UniversalConverter.Exceptions.IncorrectDimensionException;

public class PreProcessingPhase {

    public void checkDimension(final Expression leftHand, final Expression rightHand) throws IncorrectDimensionException {
        if(!leftHand.isConversionAvailable(rightHand)){
            throw new IncorrectDimensionException();
        }
    }

    public Expression combine(Expression firstExpression, Expression secondExpression) throws IncorrectDimensionException {
        return secondExpression.multiply(firstExpression.invert());
    }

    public Expression step(Expression leftHand, Expression rightHand) throws IncorrectDimensionException {
        checkDimension(leftHand, rightHand);
        return combine(leftHand, rightHand);
    }

}
