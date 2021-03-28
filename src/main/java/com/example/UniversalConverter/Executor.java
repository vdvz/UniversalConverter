package com.example.UniversalConverter;

import com.example.UniversalConverter.Exceptions.IncorrectDimensionException;
import com.example.UniversalConverter.Exceptions.InvalidStringForParsing;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;

public class Executor {
    private static final Logger logger = LogManager.getLogger(Executor.class);

    private final ConversionRequestParser parser = new ConversionRequestParser();
    private final ProcessingPhase processingPhase = new ProcessingPhase();

    @Value("#{ServiceRunner.filePath}")
    String path;

    private Rules rules;

    {
        try {
            System.out.println("PATH " + path);
            rules = RulesCreator.createRules("C:\\Users\\Vadim\\Desktop\\UniversalConverter\\src\\main\\resources\\conversion_rules");
        } catch (IOException e) {
            logger.error("Invalid path to a file with rules");
            System.exit(-1);
        }
    }

    public void handleRequest(ConversionRequest request) throws InvalidStringForParsing, IncorrectDimensionException {
        Expression from = parser.parseStringToExpression(request.getFrom(), rules);
        Expression to = parser.parseStringToExpression(request.getTo(), rules);

        Expression expressionToConvert = PreProcessingPhase.preprocessing(from, to);

        processingPhase.convert(expressionToConvert);
    }



}
