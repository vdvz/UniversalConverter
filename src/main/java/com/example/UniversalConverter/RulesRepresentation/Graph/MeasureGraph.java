package com.example.UniversalConverter.RulesRepresentation.Graph;

import com.example.UniversalConverter.Converter.ConversionRate;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * MeasureGraph описывает известные преобразования для одной из мер. Например: Граф содержащий {км,
 * см, м} описывает известные преобразования для меры длины; Граф содержащий {день, декада, год}
 * описывает известные преобразования для меры времени; Где день, декада, год, км, см, м - Node'ы с
 * соответсвующими названиями.
 */
public class MeasureGraph {

  private final Set<Node> nodes;

  public MeasureGraph(Node node) {
    nodes = new HashSet<>();
    nodes.add(node);
  }

  /**
   * Присоединяет вершины fromNode к вершине toNode
   *
   * @param fromNode присоединяемая вершина
   * @param toNode   вершина с которой происходит соединение
   * @param rate     вес ребра, коэффициент преобразования fromNode к toNode
   */
  public void bindNode(Node fromNode, Node toNode, BigDecimal rate) {
    nodes.add(fromNode);
    nodes.add(toNode);

    fromNode.addEdge(toNode, new ConversionRate(rate, BigDecimal.ONE));
    toNode.addEdge(fromNode, new ConversionRate(BigDecimal.ONE, rate));
  }

  /**
   * Соединяет граф this и newGraph
   *
   * @param fromNode присоединяемая вершина
   * @param toNode   вершина с которой происходит соединение
   * @param rate     вес ребра, коэффициент преобразования
   * @param newGraph присоединяемый граф
   */
  public void bindGraph(Node fromNode, Node toNode, BigDecimal rate, MeasureGraph newGraph) {
    nodes.addAll(newGraph.nodes);

    fromNode.addEdge(toNode, new ConversionRate(rate, BigDecimal.ONE));
    toNode.addEdge(fromNode, new ConversionRate(BigDecimal.ONE, rate));
  }

  /**
   * Ищет и возвращает Node с именем nodeName.
   *
   * @param nodeName имя вершины
   * @return возвращаемая node, если такой вершины не существует, то null
   */
  public Node getNodeByName(String nodeName) {
    return nodes.stream().filter(e -> e.getUnitName().equals(nodeName)).findFirst().orElse(null);
  }

  @Override
  public String toString() {
    return "MeasureGraph " + hashCode() + "\n";
  }

}
