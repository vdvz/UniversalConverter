package com.example.UniversalConverter;


import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Rules {

    private final Map<String, MeasureGraph> KnownUnits;

    public Collection<MeasureGraph> getKnownUnits() {
        return  KnownUnits.values();
    }

    private Rules() {
        KnownUnits = new HashMap<>();
    }

    Rules(Map<String, MeasureGraph> _knownUnits) {
        KnownUnits = _knownUnits;
    }

    public boolean isKnownNode(Node node) {
        return KnownUnits.containsKey(node);
    }

    public MeasureGraph getGraph(String measureName){
        return KnownUnits.get(measureName);
    }

}