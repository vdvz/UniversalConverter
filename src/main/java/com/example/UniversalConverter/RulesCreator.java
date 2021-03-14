package com.example.UniversalConverter;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class RulesCreator {

    @Bean(name = "Rules")
    @Scope("singleton")
    public static Rules createRules(@Value("${file_path}") String pathToResourceWithRules) throws IOException {
        System.out.println(pathToResourceWithRules);
        Map<Unit, MeasureGraph> knownUnits = new HashMap<>();

        try (CSVReader reader = new CSVReader(new FileReader(pathToResourceWithRules))) {

            String[] lines;

            while ((lines = reader.readNext()) != null) {
                Unit firstUnit = new Unit(lines[0]);
                Unit secondUnit = new Unit(lines[1]);
                int rate = Integer.parseInt(lines[2]);
                MeasureGraph graph = null;
                if ((graph = knownUnits.get(firstUnit)) != null) {
                    if (knownUnits.containsKey(secondUnit)) {
                        continue;
                        // TODO throw exception
                    } else {
                        graph.bind(firstUnit, secondUnit, rate);
                        knownUnits.put(secondUnit, graph);
                    }
                } else if ((graph = knownUnits.get(secondUnit)) != null) {
                    graph.bind(secondUnit, firstUnit, rate);
                    knownUnits.put(firstUnit, graph);
                } else {
                    graph = new MeasureGraph(firstUnit);
                    knownUnits.put(firstUnit, graph);
                    graph.bind(firstUnit, secondUnit, rate);
                    knownUnits.put(secondUnit, graph);
                }
            }

        } catch (CsvValidationException e) {
            e.printStackTrace();
        }
        return new Rules(knownUnits);
    }
}
