package com.example.UniversalConverter;

import com.example.UniversalConverter.Exceptions.IncorrectDimensionException;
import com.example.UniversalConverter.Exceptions.InvalidStringForParsing;
import com.example.UniversalConverter.Exceptions.UnknownNameOfUnitException;
import java.math.BigDecimal;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class ConversionController {

    private final static Logger logger = LogManager.getLogger(ConversionController.class);

    private static final int COUNT_DIGITS_IN_RESPONSE = 15;

    ConversionHandler conversionHandler = new ConversionHandler();

    @PostMapping("/convert")
    ResponseEntity<String> getConversionRate(@RequestBody ConversionRequest request)
        throws IncorrectDimensionException, InvalidStringForParsing, UnknownNameOfUnitException {
        logger.info(
                "Get request for conversion. Need to convert from " + request.getFrom() + " to " + request
                        .getTo());

        conversionHandler.handleRequest(request);

        return new ResponseEntity<>("answer", HttpStatus.OK);
    }

    /**
     * Обрабатывает результат конвертации, наклаывая ограничения по отправляемым символам
     * COUNT_DIGITS_IN_RESPONSE = 15
     * @param value Результат конвертации
     * @return Строка ответа
     */
    private String prepareResponse(BigDecimal value){
        var responseStr = value.toString().stripTrailing();
        return responseStr.length() > COUNT_DIGITS_IN_RESPONSE ? responseStr : responseStr.substring(0, COUNT_DIGITS_IN_RESPONSE);
    }

}