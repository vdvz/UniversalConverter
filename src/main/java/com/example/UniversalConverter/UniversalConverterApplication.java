package com.example.UniversalConverter;


import com.example.UniversalConverter.RulesRepresentation.RulesManager;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class UniversalConverterApplication {

  @Bean
  public ObjectMapper tolerantObjectMapper() {
    final JsonFactory jsonFactory = new JsonFactory();
    jsonFactory.enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES);
    return new ObjectMapper(jsonFactory);
  }

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
