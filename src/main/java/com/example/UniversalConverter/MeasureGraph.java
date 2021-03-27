package com.example.UniversalConverter;

import java.math.BigDecimal;
import java.util.*;

public class MeasureGraph {

    private final Set<Node> nodes;
    private final Node rootNode;

    private MeasureGraph(){
        rootNode = null;
        nodes = null;
    }

    public MeasureGraph(Node rootNode) {
        nodes = new HashSet<>();
        this.rootNode = rootNode;
        nodes.add(rootNode);
        //bindNode(dimensionlessUnitName, rootUnit, BigDecimal.ONE);
    }

    Node findNode(String unitName){
        System.out.println(unitName);
        Optional<Node> result = nodes.stream().filter(e -> e.getUnitName().equals(unitName)).findFirst();
        return result.orElse(null);
    }

    public void bindNode(Node existingNode, Node newNode, BigDecimal rate) {
        nodes.add(newNode);
        existingNode.addEdge(newNode, rate);
    }

    public boolean isRootNode(Node node){
        return node.equals(rootNode);
    }

    public void bindGraph(Node existingNode, Node newNode, BigDecimal rate, MeasureGraph newGraph){
        nodes.addAll(newGraph.getNodes());
        existingNode.addEdge(newNode, rate);
    }

    public Set<Node> getNodes() {
        return nodes;
    }

    @Override
    public String toString() {
        return "MeasureGraph" + hashCode() + "\n";
    }
}
