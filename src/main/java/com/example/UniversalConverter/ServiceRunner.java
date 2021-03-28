package com.example.UniversalConverter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Scope;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component("ServiceRunner")
@Order(1)
public class ServiceRunner implements CommandLineRunner {
    private static final Logger logger = LogManager.getLogger(ServiceRunner.class);

    private String filePath;

    @Override
    public void run(String... args) throws Exception {
        logger.info("Starting Universal Converter service ...");
        filePath = args[0];
        System.out.println("FP: " + filePath);
        logger.info("path is " + args[0]);

    }

    public String getFilePath() {
        System.out.println("FPGGG: " + filePath);
        return filePath;

    }

}
