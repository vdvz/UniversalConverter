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
        String dimensionlessUnitName = "";
        rootNode = new Node(dimensionlessUnitName);
        nodes.add(rootNode);
        bindNode(dimensionlessUnitName, rootUnit, BigDecimal.ONE);
    }

    Node findNode(String unitName){
        Optional<Node> result = nodes.stream().filter(e -> e.getUnitName().equals(unitName)).findFirst();
        if(result.isEmpty()){
            throw new NoSuchElementException();
        }
        return result.get();
    }

    public void bindNode(String existingUnit, String newUnit, BigDecimal rate) {
        Node existingNode = findNode(existingUnit);
        Node newNode = new Node(newUnit);
        nodes.add(newNode);
        existingNode.addEdge(newNode, rate);
    }



    public boolean isRootNode(Node node){
        return node.equals(rootNode);
    }

    public void connectUnits(Unit unit1, Unit unit2, BigDecimal rate){
        Node firstExistingNode = findNode(unit1);
        Node secondExistingNode = findNode(unit2);
        firstExistingNode.addEdge(secondExistingNode, rate);
    }

    public void bindGraph(Unit existingUnit, Unit newUnit, BigDecimal rate, MeasureGraph newGraph){
        nodes.addAll(newGraph.getNodes());
        connectUnits(existingUnit, newUnit, rate);
    }

    public Set<Node> getNodes() {
        return nodes;
    }

    Map<Node, BigDecimal> getNeighbors(Unit unit){
        return findNode(unit).getNeighbors();
    }

}
