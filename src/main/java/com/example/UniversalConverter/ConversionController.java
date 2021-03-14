package com.example.UniversalConverter;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConversionController {

    @PostMapping("/convert")
    int getConversionRate(@RequestBody ConversionRequest request){
        System.out.println(request.getFrom());
        System.out.println(request.getTo());
        return 0;
    }


}