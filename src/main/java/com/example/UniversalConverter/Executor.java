package com.example.UniversalConverter;

public class Executor {

    ConversionRequestParser parser = new ConversionRequestParser();
    PreProcessingPhase preProcessingPhase = new PreProcessingPhase();

    Rules rules = new Rules(null);


    public void handleRequest(ConversionRequest request){
        Expression from = parser.parseStringToExpression(request.getFrom(), rules);
        Expression to = parser.parseStringToExpression(request.getTo(), rules);

        try {
            preProcessingPhase.checkDimension(from, to);
        } catch (IncorrectDimensionException e) {
            e.printStackTrace();
        }
        Expression expressionToConverter = null;
        try {
            expressionToConverter = preProcessingPhase.combine(from, to);
        } catch (IncorrectDimensionException e) {
            e.printStackTrace();
        }



    }



}
