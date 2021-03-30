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
                String attachableNodeName = values[0];
                String nodeNameToAttach = values[1];
                BigDecimal rate = new BigDecimal(values[2]);
                if (rate.compareTo(BigDecimal.ZERO) == 0) {
                    logger.info(
                            "Коэффициент преобразования из \"" + attachableNodeName + "\" в \""
                                + nodeNameToAttach + "\" равен 0. Такое правило не будет учтено.");
                    continue;
                }

                MeasureGraph existingGraph;
                if ((existingGraph = knownUnits.get(attachableNodeName)) != null) {
                    MeasureGraph graphToAttach;
                    if((graphToAttach = knownUnits.get(nodeNameToAttach)) != null){
                        Node nodeToAttach = graphToAttach.getNodeByName(nodeNameToAttach);
                        Node attachableNode = existingGraph.getNodeByName(attachableNodeName);
                        graphToAttach.bindGraph(attachableNode, nodeToAttach, rate, existingGraph);
                        MeasureGraph finalExistingGraph = existingGraph;
                        knownUnits.forEach((unit, measureGraph) -> {
                            if (measureGraph.equals(finalExistingGraph)) {
                                knownUnits.replace(unit, graphToAttach);
                            }
                        });
                    }else{
                        Node nodeToAttach = new Node(nodeNameToAttach);
                        Node attachableNode = existingGraph.getNodeByName(attachableNodeName);
                        existingGraph.bindNode(attachableNode, nodeToAttach, rate);
                        knownUnits.put(nodeNameToAttach, existingGraph);
                    }

                } else if ((existingGraph = knownUnits.get(nodeNameToAttach)) != null) {
                    MeasureGraph attachableGraph;
                    if((attachableGraph = knownUnits.get(attachableNodeName)) != null){
                        Node nodeToAttach = existingGraph.getNodeByName(nodeNameToAttach);
                        Node attachableNode = attachableGraph.getNodeByName(attachableNodeName);
                        existingGraph.bindGraph(attachableNode, nodeToAttach, rate, attachableGraph);
                        MeasureGraph finalExistingGraph = existingGraph;// make effectively final
                        knownUnits.forEach((unit, measureGraph) -> {
                            if (measureGraph.equals(attachableGraph)) {
                                knownUnits.replace(unit, finalExistingGraph);
                            }
                        });
                    }else{
                        Node nodeToAttach = existingGraph.getNodeByName(nodeNameToAttach);
                        Node attachableNode = new Node(attachableNodeName);;
                        existingGraph.bindNode(attachableNode, nodeToAttach, rate);
                        knownUnits.put(attachableNodeName, existingGraph);
                    }
                } else {
                    Node attachableNode = new Node(attachableNodeName);
                    Node nodeToAttach = new Node(nodeNameToAttach);
                    existingGraph = new MeasureGraph(nodeToAttach);
                    knownUnits.put(nodeNameToAttach, existingGraph);
                    existingGraph.bindNode(attachableNode, nodeToAttach, rate);
                    knownUnits.put(attachableNodeName, existingGraph);
                }
            }

        } catch (CsvValidationException e) {
            e.printStackTrace();
        }
        rules = new Rules(knownUnits);
        logger.info("Новые правила созданы");
        return rules;
    }

}
