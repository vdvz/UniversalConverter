package com.example.UniversalConverter;

import com.example.UniversalConverter.Exceptions.IncorrectDimensionException;
import com.example.UniversalConverter.Exceptions.InvalidStringForParsing;
import com.example.UniversalConverter.Exceptions.NoAvailableRulesException;
import com.example.UniversalConverter.Exceptions.UnknownNameOfUnitException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConversionHandler {

    private static final Logger logger = LogManager.getLogger(ConversionHandler.class);

    private final ConversionRequestParser parser = new ConversionRequestParser();

    private Rules rules;

    {
        try {
            rules = RulesManager.getRules();
        } catch (NoAvailableRulesException e) {
            logger.error("Не существует созданных правил. Дальнейшая работа сервиса невозможна.", e);
            System.exit(-1);
        }
    }

    public void handleRequest(ConversionRequest request)
            throws IncorrectDimensionException, UnknownNameOfUnitException {
        Expression from = parser.parseStringToExpression(request.getFrom(), rules);
        Expression to = parser.parseStringToExpression(request.getTo(), rules);

        PreProcessingPhase.preprocessing(from, to);

        ExpressionConverter_I converter = new ExpressionConverter();
        converter.convert(from, to);
    }
}
