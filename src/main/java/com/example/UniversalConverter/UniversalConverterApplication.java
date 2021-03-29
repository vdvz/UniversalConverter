package com.example.UniversalConverter;


import java.io.IOException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UniversalConverterApplication {

    public static void main(String[] args) {
        try {
            RulesManager.createRules(args[0]);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        SpringApplication.run(UniversalConverterApplication.class, args);
    }

}
