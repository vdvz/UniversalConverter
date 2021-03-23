package com.example.UniversalConverter;

import com.opencsv.exceptions.CsvValidationException;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;


public class RulesCreator {

    public static Rules createRules(String pathToResourceWithRules) throws IOException {
        System.out.println(pathToResourceWithRules);
        Map<Unit, MeasureGraph> knownUnits = new HashMap<>();
        try (ConversionRulesReader reader = new ConversionRulesReader(pathToResourceWithRules)) {
            String[] lines;

            while ((lines = reader.getNextValues()) != null) {
                Unit firstUnit = new Unit(lines[0]);
                Unit secondUnit =  new Unit(lines[1]);
                BigDecimal rate = new BigDecimal(lines[2]);
                MeasureGraph graph = null;
                if ((graph = knownUnits.get(firstUnit)) != null) {
                    if (knownUnits.containsKey(secondUnit)) {
                        continue;
                        // TODO throw exception
                    } else {
                        graph.bindUnit(firstUnit, secondUnit, rate);
                        knownUnits.put(secondUnit, graph);
                    }
                } else if ((graph = knownUnits.get(secondUnit)) != null) {
                    graph.bindUnit(secondUnit, firstUnit, rate);
                    knownUnits.put(firstUnit, graph);
                } else {
                    graph = new MeasureGraph(firstUnit);
                    knownUnits.put(firstUnit, graph);
                    graph.bindUnit(firstUnit, secondUnit, rate);
                    knownUnits.put(secondUnit, graph);
                }
            }

        } catch (CsvValidationException e) {
            e.printStackTrace();
        }
        return new Rules(knownUnits);
    }
}
