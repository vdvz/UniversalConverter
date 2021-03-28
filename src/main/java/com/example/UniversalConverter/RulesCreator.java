package com.example.UniversalConverter;

import com.opencsv.exceptions.CsvValidationException;
import org.springframework.context.annotation.Bean;

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
                String firstNodeName = lines[0];
                String secondNodeName =  lines[1];
                BigDecimal rate = new BigDecimal(lines[2]);
                if(rate.compareTo(BigDecimal.ZERO) == 0) continue;
                MeasureGraph existingGraph;
                MeasureGraph attachableGraph;
                if ((existingGraph = knownUnits.get(secondNodeName)) != null) {
                    if ((attachableGraph = knownUnits.get(firstNodeName)) != null) {
                        Node firstNode = attachableGraph.getNodeByName(firstNodeName);
                        Node secondNode = existingGraph.findNode(secondNodeName);
                        existingGraph.bindGraph(firstNode, secondNode, rate, attachableGraph);
                        MeasureGraph finalGraph = existingGraph;
                        knownUnits.forEach((unit, measureGraph) -> {
                            if(measureGraph.equals(attachableGraph)){
                                knownUnits.replace(unit, finalGraph);
                            }
                        });
                    } else {
                        existingGraph.bindNode(new Node(firstNodeName), existingGraph.getNodeByName(secondNodeName), rate);
                        knownUnits.put(firstNodeName, existingGraph);
                    }
                } else if ((existingGraph = knownUnits.get(firstNodeName)) != null) {
                    if ((attachableGraph = knownUnits.get(secondNodeName)) != null) {
                        Node firstNode = attachableGraph.getNodeByName(secondNodeName);
                        Node secondNode = existingGraph.findNode(firstNodeName);

                        existingGraph.bindGraph(firstNode, secondNode, rate, attachableGraph);
                        MeasureGraph finalGraph = existingGraph;
                        knownUnits.forEach((unit, measureGraph) -> {
                            if(measureGraph.equals(attachableGraph)){
                                knownUnits.replace(unit, finalGraph);
                            }
                        });
                    } else {
                        existingGraph.bindNode(new Node(secondNodeName), existingGraph.getNodeByName(firstNodeName), rate);
                        knownUnits.put(secondNodeName, existingGraph);
                    }

                } else {
                    Node secondNode = new Node(secondNodeName);
                    Node firstNode = new Node(firstNodeName);
                    existingGraph = new MeasureGraph(secondNode);
                    knownUnits.put(secondNodeName, existingGraph);
                    existingGraph.bindNode(firstNode, secondNode, rate);
                    knownUnits.put(firstNodeName, existingGraph);
                }
            }

        } catch (CsvValidationException e) {
            e.printStackTrace();
        }
        return new Rules(knownUnits);
    }


}
