package com.example.UniversalConverter;

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

    private final Set<Node> nodes;
    private final Node rootNode;

    public MeasureGraph(Node rootNode) {
        nodes = new HashSet<>();
        this.rootNode = rootNode;
        nodes.add(rootNode);
    }

    Node findNode(String unitName) {
        Optional<Node> result = nodes.stream().filter(e -> e.getUnitName().equals(unitName))
                .findFirst();
        return result.orElse(null);
    }

    public void bindNode(Node fromNode, Node toNode, BigDecimal rate) {
        nodes.add(fromNode);
        nodes.add(toNode);
        fromNode.addEdge(toNode, rate);
    }

    public boolean isRootNode(Node node) {
        return node.equals(rootNode);
    }

    public void bindGraph(Node fromNode, Node toNode, BigDecimal rate, MeasureGraph newGraph) {
        nodes.addAll(newGraph.nodes);
        fromNode.addEdge(toNode, rate);
    }

    public Node getNodeByName(String name) {
        return nodes.stream().filter(e -> e.getUnitName().equals(name)).findFirst().orElse(null);
    }

    @Override
    public String toString() {
        return "MeasureGraph " + hashCode() + "\n";
    }

}
