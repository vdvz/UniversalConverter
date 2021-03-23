package com.example.UniversalConverter;

import org.springframework.web.bind.annotation.*;

import java.net.http.HttpResponse;

@RestController
public class ConversionController {

    @PostMapping("/convert")
    @ResponseBody
    String getConversionRate(@RequestBody ConversionRequest request){
        System.out.println(request.getFrom());
        System.out.println(request.getTo());
        return "Ok";
    }


}