package com.example.UniversalConverter.Converter;

import com.example.UniversalConverter.RulesRepresentation.Graph.Node;

/**
 * Этот класс описывает шаг алгоритма. Node - вершина в которой мы находимся. ConversionRate -
 * коэффициент преобразования к данной вершине
 */
public class MultiplicationConversionRates {

  private final Node node;
  private final ConversionRate conversionRate;

  public MultiplicationConversionRates(Node node, ConversionRate conversionRate) {
    this.node = node;
    this.conversionRate = conversionRate;
  }

  public ConversionRate getConversionRate() {
    return conversionRate;
  }

  public Node getNode() {
    return node;
  }
}
