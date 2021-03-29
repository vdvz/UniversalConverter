package com.example.UniversalConverter;

import org.apache.commons.collections4.map.HashedMap;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Описывает узел графа.
 */
public class Node {

    private final String unitName;

    Map<Node, BigDecimal> neighbours = new HashMap<>();

    public Node(String unitName) {
        this.unitName = unitName;
    }

    /**
     * @param node Вершина-сосед
     * @param rate Коэффициент преобразования
     */
    public void addEdge(Node node, BigDecimal rate) {
        neighbours.put(node, rate);
    }

    /**
     * @return Возвращает Map<Node, BigDecimal>, где Node - вершина-сосед,
     * BigDecimal - коэффициент преобразования к соседу
     */
    public Map<Node, BigDecimal> getNeighbors() {
        return new HashedMap<>(neighbours);
    }

    /**
     * @return Имя вершины
     */
    public String getUnitName() {
        return unitName;
    }
}
