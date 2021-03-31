package com.example.UniversalConverter.RulesRepresentation;

import com.example.UniversalConverter.Exceptions.NoAvailableRulesException;
import com.example.UniversalConverter.RulesReaders.CsvConversionRulesReader;
import com.example.UniversalConverter.RulesReaders.ResourceReaderI;
import com.example.UniversalConverter.RulesRepresentation.Graph.MeasureGraph;
import com.example.UniversalConverter.RulesRepresentation.Graph.Node;
import com.opencsv.exceptions.CsvValidationException;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.NoSuchFileException;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class RulesManager {

  private static final Logger logger = LogManager.getLogger(RulesManager.class);

  private static Rules rules;

  /**
   * @return Возвращает уже созданные Rules
   * @throws NoAvailableRulesException - если Rules не были созданы
   */
  public static Rules getRules() throws NoAvailableRulesException {
    if (rules != null) {
      return rules;
    }
    throw new NoAvailableRulesException();
  }

  /**
   * Создает правила конвертации из ресурса pathToResourceWithRules
   *
   * @param pathToResourceWithRules ресурс с правилами
   * @return Rules - правила конвертации.
   * @throws IOException В случае невозможности создать Rules.
   */
  public static Rules createRules(String pathToResourceWithRules) throws IOException {
    if (pathToResourceWithRules == null) {
      throw new NoSuchFileException("Путь до ресурса с правилами не задан");
    }

    logger.info("Создание правил. Путь до ресурса с правилами: " + pathToResourceWithRules);

    Map<String, MeasureGraph> knownNodes = new HashMap<>();
    try (ResourceReaderI reader = new CsvConversionRulesReader(pathToResourceWithRules)) {
      /*Получаем новые значения*/
      String[] values;
      while ((values = reader.getNextValues()) != null) {
        String sourceNodeName = values[0];
        String targetNodeName = values[1];
        BigDecimal rate = new BigDecimal(values[2]).stripTrailingZeros();
        if (rate.compareTo(BigDecimal.ZERO) == 0) {
          logger.info(
              "Коэффициент преобразования из \"" + sourceNodeName + "\" в \""
                  + targetNodeName + "\" равен 0. Такое правило не будет учтено.");
          continue;
        }

        /*Вершина которая называется sourceNodeName - вершина которая будет присоединена
         *Вершина которая называется targetNodeName - вершина к которой будет выполнено присоединение*/
        MeasureGraph existingGraph;
        if ((existingGraph = knownNodes.get(sourceNodeName)) != null) {
          MeasureGraph graphToAttach;
          if ((graphToAttach = knownNodes.get(targetNodeName)) != null) {
            Node nodeToAttach = graphToAttach.getNodeByName(targetNodeName);
            Node attachableNode = existingGraph.getNodeByName(sourceNodeName);
            graphToAttach.bindGraph(attachableNode, nodeToAttach, rate, existingGraph);
            MeasureGraph finalExistingGraph = existingGraph;
            knownNodes.forEach((unit, measureGraph) -> {
              if (measureGraph.equals(finalExistingGraph)) {
                knownNodes.replace(unit, graphToAttach);
              }
            });
          } else {
                        /*Создаем вершину с именем targetNodeName и присоединяем
                        к вершине sourceNodeName в уже существующем графе*/
            Node nodeToAttach = new Node(targetNodeName);
            Node attachableNode = existingGraph.getNodeByName(sourceNodeName);
            existingGraph.bindNode(attachableNode, nodeToAttach, rate);
            knownNodes.put(targetNodeName, existingGraph);
          }
        } else if ((existingGraph = knownNodes.get(targetNodeName)) != null) {
          MeasureGraph attachableGraph;
          if ((attachableGraph = knownNodes.get(sourceNodeName)) != null) {
            Node nodeToAttach = existingGraph.getNodeByName(targetNodeName);
            Node attachableNode = attachableGraph.getNodeByName(sourceNodeName);
            existingGraph.bindGraph(attachableNode, nodeToAttach, rate, attachableGraph);
            MeasureGraph finalExistingGraph = existingGraph;// make effectively final
            knownNodes.forEach((unit, measureGraph) -> {
              if (measureGraph.equals(attachableGraph)) {
                knownNodes.replace(unit, finalExistingGraph);
              }
            });
          } else {
            Node nodeToAttach = existingGraph.getNodeByName(targetNodeName);
            Node attachableNode = new Node(sourceNodeName);
            existingGraph.bindNode(attachableNode, nodeToAttach, rate);
            knownNodes.put(sourceNodeName, existingGraph);
          }
        } else {
          Node attachableNode = new Node(sourceNodeName);
          Node nodeToAttach = new Node(targetNodeName);
          existingGraph = new MeasureGraph(nodeToAttach);
          knownNodes.put(targetNodeName, existingGraph);
          existingGraph.bindNode(attachableNode, nodeToAttach, rate);
          knownNodes.put(sourceNodeName, existingGraph);
        }
      }

    } catch (CsvValidationException e) {
      e.printStackTrace();
    }

    rules = new Rules(knownNodes);
    logger.info("Новые правила созданы");
    return rules;
  }

}
