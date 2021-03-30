package com.example.UniversalConverter;

public class MultiplicationUnit {

    private final Node node;
    private final ConversionRate conversionRate;

    public MultiplicationUnit(Node _node, ConversionRate k) {
        node = _node;
        conversionRate = k;
    }

    public ConversionRate getConversionRate() {
        return conversionRate;
    }

    public Node getNode() {
        return node;
    }
}
