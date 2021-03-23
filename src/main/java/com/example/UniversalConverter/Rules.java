package com.example.UniversalConverter;


import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

public class Rules {

    private final Map<Unit, MeasureGraph> KnownUnits;

    private Rules() {
        KnownUnits = new HashMap<>();
    }

    Rules(Map<Unit, MeasureGraph> _knownUnits) {
        KnownUnits = _knownUnits;
    }

    public boolean isKnownUnit(Unit unit) {
        return KnownUnits.containsKey(unit);
    }

    MeasureGraph getGraph(Unit unit){
        return KnownUnits.get(unit);
    }

}