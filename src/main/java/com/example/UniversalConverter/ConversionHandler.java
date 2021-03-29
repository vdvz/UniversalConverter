package com.example.UniversalConverter;

import com.example.UniversalConverter.Exceptions.IncorrectDimensionException;
import com.example.UniversalConverter.Exceptions.InvalidStringForParsing;
import com.example.UniversalConverter.Exceptions.UnknownNameOfUnitException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class ConversionHandler {

    private static final Logger logger = LogManager.getLogger(ConversionHandler.class);

    private final ConversionRequestParser parser = new ConversionRequestParser();

    private Rules rules;

    {
        try {
            //"C:\\Users\\Vadim\\Desktop\\UniversalConverter\\src\\main\\resources\\conversion_rules"
            rules = RulesManager.createRules();
        } catch (IOException e) {
            logger.error("Invalid path to a file with rules");
            System.exit(-1);
        }
    }

    public void handleRequest(ConversionRequest request)
            throws InvalidStringForParsing, IncorrectDimensionException, UnknownNameOfUnitException {
        Expression from = parser.parseStringToExpression(request.getFrom(), rules);
        Expression to = parser.parseStringToExpression(request.getTo(), rules);

        Expression expressionToConvert = PreProcessingPhase.preprocessing(from, to);

        ExpressionConverter_I converter = new ExpressionConverter();
        converter.convert(expressionToConvert);


    }
}
