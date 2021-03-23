package com.example.UniversalConverter;

import java.util.*;

public class MeasureGroup{

    final private MeasureGraph graph;

    public MeasureGraph getGraph() {
        return graph;
    }

    LinkedList<Unit> units = new LinkedList<>();

    public MeasureGroup(MeasureGraph graph) {
        this.graph = graph;
    }

    public void addUnit(Unit unit, int power){
        if(units.contains(unit)){
            Unit exst_unit = units.get(units.indexOf(unit));
            exst_unit.setPower(exst_unit.getPower() + power);
        } else {
            units.add(unit);
        }
    }

    Unit poll(){
        return units.poll();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MeasureGroup that = (MeasureGroup) o;
        return Objects.equals(graph, that.graph);
    }

    @Override
    public int hashCode() {
        return Objects.hash(graph);
    }

    public boolean contains(Unit unit) {
        return units.contains(unit);
    }

    public int size(){
        return units.size();
    }
}
