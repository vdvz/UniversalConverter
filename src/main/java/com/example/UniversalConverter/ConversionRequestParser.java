package com.example.UniversalConverter;

import com.example.UniversalConverter.Exceptions.InvalidStringForParsing;
import com.example.UniversalConverter.Exceptions.UnknownNameOfUnitException;

import java.util.*;
import java.util.stream.Collectors;

public class ConversionRequestParser {

    public void preParsingChecks(String sourceStr) throws InvalidStringForParsing {
        if (sourceStr.indexOf('/') != -1) {
            var stream = Arrays.stream(sourceStr.split("/")).map(String::trim);
            if (stream.count() != 1 && stream.filter(e -> e.equals("")).count() == 1) {
                throw new InvalidStringForParsing();
            }
        }
    }

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
            if(unit.getName().equals("")){
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
