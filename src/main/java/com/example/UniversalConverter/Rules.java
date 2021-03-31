package com.example.UniversalConverter;


import java.util.Map;
import java.util.stream.Collectors;

/**
 * Rules содержит все известные меры, и соответсвующий каждой MeasureGraph,
 * который описывает доступные преобразования.
 */
public class Rules {

    private final Map<String, MeasureGraph> KnownUnits;

    public Rules(Map<String, MeasureGraph> knownUnits) {
        KnownUnits = knownUnits;
    }

    /**
     * По названию меры получает MeasureGraph
     * @param measureName Название величины
     * @return MeasureGraph в котором данная мера находится, null если такой меры не существует
     */
    public MeasureGraph getGraph(String measureName) {
        return KnownUnits.get(measureName);
    }

    @Override
    public String toString() {
        return "Rules [" + KnownUnits.keySet().stream().map(e -> e + KnownUnits.get(e).toString())
                .collect(Collectors.joining()) + ']';
    }
}