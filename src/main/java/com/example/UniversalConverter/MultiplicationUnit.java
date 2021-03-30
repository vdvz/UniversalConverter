package com.example.UniversalConverter;

import java.math.BigDecimal;

public class MultiplicationUnit {

    private Node node;
    private ConversionRate conversionRate;

    public MultiplicationUnit(Node _node, ConversionRate k) {
        node = _node;
        conversionRate = k;
    }

    public MultiplicationUnit(Node _node) {
        node = _node;
        conversionRate = new ConversionRate(BigDecimal.ONE, BigDecimal.ONE);
    }

    public ConversionRate getConversionRate() {
        return conversionRate;
    }

    public void setConversionRate(ConversionRate k) {
        conversionRate = k;
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }
}
