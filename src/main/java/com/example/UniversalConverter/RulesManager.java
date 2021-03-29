package com.example.UniversalConverter;

import com.example.UniversalConverter.Exceptions.NoAvailableRulesException;
import com.opencsv.exceptions.CsvValidationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.NoSuchFileException;
import java.util.HashMap;
import java.util.Map;


public class RulesManager {

    private static final Logger logger = LogManager.getLogger(RulesManager.class);

    private static Rules rules;

    public static Rules getRules() throws NoAvailableRulesException {
        if (rules != null) {
            return rules;
        }
        throw new NoAvailableRulesException();
    }


    public static Rules createRules(String pathToResourceWithRules) throws IOException {
        if (pathToResourceWithRules == null) {
            throw new NoSuchFileException("Путь до ресурса с правилами не задан");
        }

        logger.info("Создание правил. Путь до ресурса с правилами: " + pathToResourceWithRules);

        Map<String, MeasureGraph> knownUnits = new HashMap<>();
        try (ResourceReader_I reader = new ConversionRulesReader(pathToResourceWithRules)) {
            String[] values;
            while ((values = reader.getNextValues()) != null) {
                String firstNodeName = values[0];
                String secondNodeName = values[1];
                BigDecimal rate = new BigDecimal(values[2]);
                if (rate.compareTo(BigDecimal.ZERO) == 0) {
                    logger.info(
                            "Коэффициент преобразования из \"" + firstNodeName + "\" в \""
                                + secondNodeName + "\" равен 0. Такое правило не будет учтено.");
                    continue;
                }

                MeasureGraph existingGraph;
                if ((existingGraph = knownUnits.get(secondNodeName)) != null) {
                    attachNodeToExistingGraph(knownUnits, firstNodeName, secondNodeName, existingGraph, rate);
                } else if ((existingGraph = knownUnits.get(firstNodeName)) != null) {
                    attachNodeToExistingGraph(knownUnits, secondNodeName, firstNodeName, existingGraph, rate);
                } else {
                    Node attachableNode = new Node(firstNodeName);
                    Node nodeToAttach = new Node(secondNodeName);
                    existingGraph = new MeasureGraph(nodeToAttach);
                    knownUnits.put(secondNodeName, existingGraph);
                    existingGraph.bindNode(attachableNode, nodeToAttach, rate);
                    knownUnits.put(firstNodeName, existingGraph);
                }
            }

        } catch (CsvValidationException e) {
            e.printStackTrace();
        }
        rules = new Rules(knownUnits);
        logger.info("Новые правила созданы");
        return rules;
    }

    private static void attachNodeToExistingGraph(Map<String, MeasureGraph> knownUnits,
                                                  String attachableNodeName, String nodeNameToAttach, MeasureGraph existingGraph,
                                                  BigDecimal rate) {
        Node nodeToAttache = existingGraph.findNode(nodeNameToAttach);

        MeasureGraph attachableGraph;
        if ((attachableGraph = knownUnits.get(attachableNodeName)) != null) {
            Node attachableNode = attachableGraph.getNodeByName(attachableNodeName);
            existingGraph.bindGraph(attachableNode, nodeToAttache, rate, attachableGraph);
            knownUnits.forEach((unit, measureGraph) -> {
                if (measureGraph.equals(attachableGraph)) {
                    knownUnits.replace(unit, existingGraph);
                }
            });
        } else {
            existingGraph.bindNode(new Node(attachableNodeName), nodeToAttache, rate);
            knownUnits.put(attachableNodeName, existingGraph);
        }
    }

}
