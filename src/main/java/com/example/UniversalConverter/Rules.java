package com.example.UniversalConverter;


import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Rules {

    private final Map<Unit, MeasureGraph> KnownUnits;

    public Collection<MeasureGraph> getKnownUnits() {
        return  KnownUnits.values();
    }

    private Rules() {
        KnownUnits = new HashMap<>();
    }

    Rules(Map<Unit, MeasureGraph> _knownUnits) {
        KnownUnits = _knownUnits;
    }

    public boolean isKnownUnit(Unit unit) {
        return KnownUnits.containsKey(unit);
    }

    public MeasureGraph getGraph(Unit unit){
        return KnownUnits.get(unit);
    }

}