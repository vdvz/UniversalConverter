package com.example.UniversalConverter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * MeasureGraph описывает известные преобразования заданные Rules для одной из мер.
 * Например: Граф содержащий {км, см, м} описывает известные преобразования для меры длины;
 *           Граф содержащий {день, декада, год} описывает известные преобразования для меры времени;
 *           Где день, декада, год, км, см, м - Node'ы с соответсвующими названиями.
 */
public class MeasureGraph {

    private static final Logger logger = LogManager.getLogger(MeasureGraph.class);

    private final Set<Node> nodes;

    public MeasureGraph(Node node) {
        nodes = new HashSet<>();
        nodes.add(node);
    }

    Node findNode(String unitName) {
        Optional<Node> result = nodes.stream().filter(e -> e.getUnitName().equals(unitName))
                .findFirst();
        return result.orElse(null);
    }

    public void bindNode(Node fromNode, Node toNode, BigDecimal rate) {
        logger.debug("Bind node " + fromNode.getUnitName() + " to " + toNode.getUnitName() + " rate " + rate);
        nodes.add(fromNode);
        nodes.add(toNode);

        fromNode.addEdge(toNode, new ConversionRate(rate, BigDecimal.ONE));
        toNode.addEdge(fromNode, new ConversionRate(BigDecimal.ONE, rate));
    }

    public void bindGraph(Node fromNode, Node toNode, BigDecimal rate, MeasureGraph newGraph) {
        nodes.addAll(newGraph.nodes);
        logger.debug("Vertix from " + fromNode.getUnitName() + " to node " + toNode.getUnitName() + " " + rate);

        fromNode.addEdge(toNode, new ConversionRate(rate, BigDecimal.ONE));
        toNode.addEdge(fromNode, new ConversionRate(BigDecimal.ONE, rate));
    }

    public Node getNodeByName(String name) {
        return nodes.stream().filter(e -> e.getUnitName().equals(name)).findFirst().orElse(null);
    }

    @Override
    public String toString() {
        return "MeasureGraph " + hashCode() + "\n";
    }

}
