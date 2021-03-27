package com.example.UniversalConverter;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.http.HttpResponse;

@RestController
public class ConversionController {

    @PostMapping("/convert")
    ResponseEntity<String> getConversionRate(@RequestBody ConversionRequest request){
        System.out.println(request.getFrom());
        System.out.println(request.getTo());
        return new ResponseEntity<>("answer", HttpStatus.OK);
    }


}