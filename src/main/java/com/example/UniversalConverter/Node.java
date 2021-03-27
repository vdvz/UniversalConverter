package com.example.UniversalConverter;

import org.apache.commons.collections4.map.HashedMap;

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
        System.out.println("Hash is:" + hashCode() + "Name :" + unitName);
        System.out.println("Put from " + unitName + " to node name " + node.unitName + " neighbours" + neighbours.toString());
    }

    public Map<Node, BigDecimal> getNeighbors() {
        return new HashedMap<>(neighbours);
    }

    public String getUnitName() {
        return unitName;
    }
}
