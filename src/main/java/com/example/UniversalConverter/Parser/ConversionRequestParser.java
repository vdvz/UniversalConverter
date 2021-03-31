package com.example.UniversalConverter.Parser;

import com.example.UniversalConverter.Exceptions.UnknownNameOfUnitException;
import com.example.UniversalConverter.RequestRepresentation.Expression;
import com.example.UniversalConverter.RequestRepresentation.MeasureGroup;
import com.example.UniversalConverter.RequestRepresentation.Unit;
import com.example.UniversalConverter.RulesRepresentation.Graph.MeasureGraph;
import com.example.UniversalConverter.RulesRepresentation.Rules;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ConversionRequestParser implements ParserI {

  @Override
  public Expression parseStringToExpression(String sourceStr, Rules rules)
      throws UnknownNameOfUnitException {

    Map<MeasureGraph, MeasureGroup> knownGroups = new HashMap<>();
    List<MeasureGroup> expressionGroups = new ArrayList<>();
    String[] sub = sourceStr.split("/");

    /*создаем unit'ы с именами из запроса и степенью 1 и -1 для числителя и знаменателя соответсвенно*/
    String numeratorStr = sub[0];
    List<Unit> expressionUnits = Arrays.stream(numeratorStr.split("\\*")).map(String::trim)
        .filter(e -> !e.equals("1")).map(Unit::new).collect(Collectors.toList());

    if (sub.length == 2) {
      String denominatorStr = sub[1];
      Arrays.stream(denominatorStr.split("\\*")).map(String::trim).filter(e -> !e.equals("1"))
          .map(e -> new Unit(e, -1)).forEach(expressionUnits::add);
    }

    /*На основании принадлежности имени Unit'a к графу формируем группы*/
    for (Unit unit : expressionUnits) {
      /*пустые строки (безразмерные величины) игнорируем. у них нет существует правил конвертации.*/
      if (unit.getName().equals("")) {
        continue;
      }
      /*Ищем граф, к которому принадлежит данный unit*/
      MeasureGraph graphForCurrentUnit = rules.getGraph(unit.getName());
      if (graphForCurrentUnit == null) {
        throw new UnknownNameOfUnitException();
      }
      MeasureGroup currentGroup;
      /*Ищем группу, которая строилась на основании graphForCurrentUnit*/
      if ((currentGroup = knownGroups.get(graphForCurrentUnit)) != null) {
        /*если нашли, то добавляем к ней unit*/
        currentGroup.addUnit(unit, unit.getPower());
      } else {
        /*если нет создаем новую группу на основании графа, добавляем ее в известные, и в нее кладем unit*/
        currentGroup = new MeasureGroup(graphForCurrentUnit);
        currentGroup.addUnit(unit, unit.getPower());
        knownGroups.put(graphForCurrentUnit, currentGroup);
        expressionGroups.add(currentGroup);
      }
    }

    return new Expression(expressionGroups);
  }


}
