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

    public MeasureGraph(Unit rootUnit) {
        nodes = new HashSet<>();
        Unit dimensionlessUnit = new Unit("");
        rootNode = new Node(dimensionlessUnit);
        nodes.add(rootNode);
        bindUnit(dimensionlessUnit, rootUnit, BigDecimal.ONE);
    }

    Node findNode(Unit unit){
        Optional<Node> result = nodes.stream().filter(e -> e.getUnit().equals(unit)).findFirst();
        if(result.isEmpty()){
            throw new NoSuchElementException();
        }
        return result.get();
    }

    public void bindUnit(Unit existingUnit, Unit newUnit, BigDecimal rate) {
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
