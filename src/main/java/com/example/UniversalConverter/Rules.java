package com.example.UniversalConverter;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public class Rules {

    private final Map<String, MeasureGraph> KnownUnits;

    public Rules(Map<String, MeasureGraph> _knownUnits) {
        KnownUnits = _knownUnits;
    }

    public MeasureGraph getGraph(String measureName) {
        return KnownUnits.get(measureName);
    }

    @Override
    public String toString() {
        return "Rules [" + KnownUnits.keySet().stream().map(e -> e + KnownUnits.get(e).toString())
                .collect(Collectors.joining()) + ']';
    }
}