package com.example.UniversalConverter;

public class PreProcessingPhase {

    void checkDimension(final Expression leftHand, final Expression rightHand) throws IncorrectDimensionException {
        if(!leftHand.equals(rightHand)){
            throw new IncorrectDimensionException();
        }
    }

    Expression combine(Expression firstExpression, Expression secondExpression){
        return secondExpression.multiply(firstExpression.invert());
    }

    void firstStep(Expression leftHand, Expression rightHand){
    }

}
