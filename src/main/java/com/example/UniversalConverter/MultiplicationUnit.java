package com.example.UniversalConverter;

import java.math.BigDecimal;
import java.util.Objects;

public class MultiplicationUnit {

    private Node node;
    private BigDecimal K;

    public MultiplicationUnit(Node _node, BigDecimal k){
        node = _node;
        K = k;
    }

    public MultiplicationUnit(Node _node){
        node = _node;
        K = BigDecimal.ONE;
    }

    public BigDecimal getK() {
        return K;
    }

    public void setK(BigDecimal k) {
        K = k;
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }
}
