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
    }

    Node findNode(String unitName){
        Optional<Node> result = nodes.stream().filter(e -> e.getUnitName().equals(unitName)).findFirst();
        return result.orElse(null);
    }

    public void bindNode(Node fromNode, Node toNode, BigDecimal rate) {
        nodes.add(fromNode);
        nodes.add(toNode);
        fromNode.addEdge(toNode, rate);
    }

    public boolean isRootNode(Node node){
        return node.equals(rootNode);
    }

    public void bindGraph(Node fromNode, Node toNode, BigDecimal rate, MeasureGraph newGraph){
        nodes.addAll(newGraph.nodes);
        nodes.forEach(e-> System.out.println(e.getUnitName()));
        fromNode.addEdge(toNode, rate);
    }

    public Set<Node> getNodes() {
        return nodes;
    }

    public Node getNodeByName(String name) {
        return nodes.stream().filter(e->e.getUnitName().equals(name)).findFirst().orElse(null);
    }

    @Override
    public String toString() {
        return "MeasureGraph " + hashCode() + "\n";
    }

}
