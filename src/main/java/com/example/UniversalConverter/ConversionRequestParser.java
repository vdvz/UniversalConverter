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
            throws InvalidStringForParsing, UnknownNameOfUnitException {
        //preParsingChecks(sourceStr);

        Map<MeasureGraph, MeasureGroup> knownGroups = new HashMap<>();
        List<MeasureGroup> expressionGroups = new ArrayList<>();
        String[] sub = sourceStr.split("/");

        String numeratorStr = sub[0];
        List<Unit> expressionUnits = Arrays.stream(numeratorStr.split("\\*")).map(String::trim)
                .filter(e -> !e.equals("1")).map(Unit::new).collect(Collectors.toList());

        if (sub.length == 2) {
            String denominatorStr = sub[1];
            Arrays.stream(denominatorStr.split("\\*")).map(String::trim).filter(e -> !e.equals("1"))
                    .map(e -> new Unit(e, -1)).forEach(expressionUnits::add);
        }

        for (Unit unit : expressionUnits) {
            //Check if unit exists and if so then get graphForCurrentUnit
            MeasureGraph graphForCurrentUnit = rules.getGraph(unit.getName());
            if (graphForCurrentUnit == null) {
                throw new UnknownNameOfUnitException();
            }
            MeasureGroup currentGroup;
            if ((currentGroup = knownGroups.get(graphForCurrentUnit)) != null) {
                currentGroup.addUnit(unit, unit.getPower());
            } else {
                currentGroup = new MeasureGroup(graphForCurrentUnit);
                currentGroup.addUnit(unit, unit.getPower());
                knownGroups.put(graphForCurrentUnit, currentGroup);
                expressionGroups.add(currentGroup);
            }
        }

        return new Expression(expressionGroups);
    }


}
