package com.example.UniversalConverter;

import java.util.Objects;
import org.apache.commons.collections4.map.HashedMap;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Описывает узел графа.
 */
public class Node {

    private final String unitName;

    Map<Node, ConversionRate> neighbours = new HashMap<>();

    public Node(String unitName) {
        this.unitName = unitName;
    }

    /**
     * @param node Вершина-сосед
     * @param rate Коэффициент преобразования
     */
    public void addEdge(Node node, ConversionRate rate) {
        neighbours.put(node, rate);
    }

    /**
     * @return Возвращает Map<Node, ConversionRate>, где Node - вершина-сосед,
     * ConversionRate - коэффициент преобразования к соседу
     */
    public Map<Node, ConversionRate> getNeighbors() {
        return new HashedMap<>(neighbours);
    }

    /**
     * @return Имя вершины
     */
    public String getUnitName() {
        return unitName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Node node = (Node) o;
        return Objects.equals(unitName, node.unitName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(unitName);
    }
}
