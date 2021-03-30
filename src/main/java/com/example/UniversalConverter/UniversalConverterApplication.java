package com.example.UniversalConverter;


import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UniversalConverterApplication {
    private static final Logger logger = LogManager.getLogger(UniversalConverterApplication.class);

    public static void main(String[] args) {
        try {
            RulesManager.createRules(args[0]);
        } catch (IOException e) {
            logger.error("Ошибка при создании правил конвертации. Дальнейшая работа невозможна.", e);
            System.exit(-1);
        }

        logger.info("Запуск сервиса конвертации...");
        SpringApplication.run(UniversalConverterApplication.class, args);
    }

}
