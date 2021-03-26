package com.example.UniversalConverter;

import java.util.*;
import java.util.stream.Collectors;

public class ConversionRequestParser {


    public Expression parseStringToExpression(String value_str, Rules rules){
        Map<MeasureGraph, MeasureGroup> map = new HashMap<>();
        List<MeasureGroup> groups = new ArrayList<>();
        String[] sub = value_str.split("/");

        String numerator_str = sub[0];
        List<Unit> numerator = Arrays.stream(numerator_str.split("\\*")).map(e -> new Unit(e.trim())).collect(Collectors.toList());

        if(sub.length==2){
            String denominator_str = sub[1];
            Arrays.stream(denominator_str.split("\\*")).map(e -> new Unit(e.trim(), -1)).forEach(numerator::add);
        }

        for (Unit unit: numerator) {
            //Check if unit exists and if so then get graph
            MeasureGraph graph = rules.getGraph(unit.getName());
            MeasureGroup group;
            if((group = map.get(graph))!=null){
                group.addUnit(unit, unit.getPower());
            } else {
                group = new MeasureGroup(graph);
                group.addUnit(unit, unit.getPower());
                map.put(graph, group);
                groups.add(group);
            }
        }

        return new Expression(groups);
    }



}
