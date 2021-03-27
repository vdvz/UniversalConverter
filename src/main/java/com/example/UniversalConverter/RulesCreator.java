package com.example.UniversalConverter;

import com.opencsv.exceptions.CsvValidationException;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;


public class RulesCreator {

    public static Rules createRules(String pathToResourceWithRules) throws IOException {
        System.out.println(pathToResourceWithRules);
        Map<String, MeasureGraph> knownUnits = new HashMap<>();
        try (ConversionRulesReader reader = new ConversionRulesReader(pathToResourceWithRules)) {
            String[] lines;

            while ((lines = reader.getNextValues()) != null) {
                Node firstNode = new Node(lines[0]);
                Node secondNode =  new Node(lines[1]);
                BigDecimal rate = new BigDecimal(lines[2]);
                MeasureGraph graph;
                if ((graph = knownUnits.get(firstNode.getUnitName())) != null) {
                    if (knownUnits.containsKey(secondNode.getUnitName())) {
                        MeasureGraph attachableGraph = knownUnits.get(secondNode.getUnitName());
                        graph.bindGraph(firstNode, secondNode, rate, attachableGraph);
                        MeasureGraph finalGraph = graph;
                        knownUnits.forEach((unit, measureGraph) -> {
                            if(measureGraph.equals(attachableGraph)){
                                knownUnits.replace(unit, finalGraph);
                            }
                        });
                    } else {
                        graph.bindNode(firstNode, secondNode, rate);
                        knownUnits.put(secondNode.getUnitName(), graph);
                    }
                } else if ((graph = knownUnits.get(secondNode.getUnitName())) != null) {
                    graph.bindNode(secondNode, firstNode, rate);
                    knownUnits.put(firstNode.getUnitName(), graph);
                } else {
                    graph = new MeasureGraph(firstNode);
                    knownUnits.put(firstNode.getUnitName(), graph);
                    graph.bindNode(firstNode, secondNode, rate);
                    knownUnits.put(secondNode.getUnitName(), graph);
                }
            }

        } catch (CsvValidationException e) {
            e.printStackTrace();
        }
        return new Rules(knownUnits);
    }
}
