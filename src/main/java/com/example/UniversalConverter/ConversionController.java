package com.example.UniversalConverter;

import com.example.UniversalConverter.Exceptions.IncorrectDimensionException;
import com.example.UniversalConverter.Exceptions.InvalidStringForParsing;
import com.example.UniversalConverter.Exceptions.UnknownNameOfUnitException;
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
    ConversionHandler conversionHandler = new ConversionHandler();


    @PostMapping("/convert")
    ResponseEntity<String> getConversionRate(@RequestBody ConversionRequest request) {
        logger.info(
                "Get request for conversion. Need to convert from " + request.getFrom() + " to " + request
                        .getTo());

        try {
            conversionHandler.handleRequest(request);
        } catch (InvalidStringForParsing | IncorrectDimensionException | UnknownNameOfUnitException invalidStringForParsing) {
            invalidStringForParsing.printStackTrace();
        }

        return new ResponseEntity<>("answer", HttpStatus.OK);
    }


}