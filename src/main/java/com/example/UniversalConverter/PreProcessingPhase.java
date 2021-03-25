package com.example.UniversalConverter;

public class PreProcessingPhase {

    void checkDimension(final Expression leftHand, final Expression rightHand) throws IncorrectDimensionException {
        if(!leftHand.equals(rightHand)){
            throw new IncorrectDimensionException();
        }
    }

    Expression combine(Expression firstExpression, Expression secondExpression) throws IncorrectDimensionException {
        return secondExpression.multiply(firstExpression.invert());
    }

    public Expression step(Expression leftHand, Expression rightHand) throws IncorrectDimensionException {
        checkDimension(leftHand, rightHand);
        return combine(leftHand, rightHand);
    }

}
