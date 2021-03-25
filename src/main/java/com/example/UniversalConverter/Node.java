package com.example.UniversalConverter;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class Node {

    private final String unitName;

    Map<Node, BigDecimal> neighbours = new HashMap<>();

    public Node(String unitName) {
        this.unitName = unitName;
    }

    public void addEdge(Node node, BigDecimal rate) {
        neighbours.put(node, rate);
    }

    public Map<Node, BigDecimal> getNeighbors() {
        return neighbours;
    }

    public String getUnitName() {
        return unitName;
    }
}
