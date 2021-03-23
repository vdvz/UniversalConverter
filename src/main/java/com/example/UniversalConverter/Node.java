package com.example.UniversalConverter;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class Node {

    private final Unit unit;

    Map<Node, BigDecimal> neighbours = new HashMap<>();

    public Node(Unit unit) {
        this.unit = unit;
    }

    public Unit getUnit() {
        return unit;
    }

    public void addEdge(Node node, BigDecimal rate) {
        neighbours.put(node, rate);
    }

    public Map<Node, BigDecimal> getNeighbors() {
        return neighbours;
    }

}
