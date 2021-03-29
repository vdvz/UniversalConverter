package com.example.UniversalConverter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component("ServiceRunner")
public class ServiceRunner implements CommandLineRunner {

    private static final Logger logger = LogManager.getLogger(ServiceRunner.class);

    @Override
    public void run(String... args) throws Exception {
        logger.info("Starting Universal Converter service ...");
        logger.info("A path to source with rules is: " + args[0]);
        RulesManager.setPathToResourceWithRules(args[0]);
    }

}
