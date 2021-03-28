package com.example.UniversalConverter;

import com.example.UniversalConverter.Exceptions.IncorrectDimensionException;
import com.example.UniversalConverter.Exceptions.InvalidStringForParsing;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@Order(2)
public class ConversionController {
    private final static Logger logger = LogManager.getLogger(ConversionController.class);
    Executor executor = new Executor();


    @PostMapping("/convert")
    ResponseEntity<String> getConversionRate(@RequestBody ConversionRequest request){
        logger.info("Get request for conversion. Need to convert from " + request.getFrom() + " to " + request.getTo());

        try {
            executor.handleRequest(request);
        } catch (InvalidStringForParsing | IncorrectDimensionException invalidStringForParsing) {
            invalidStringForParsing.printStackTrace();
        }


        return new ResponseEntity<>("answer", HttpStatus.OK);
    }


}