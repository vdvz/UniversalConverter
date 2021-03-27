package com.example.UniversalConverter;

import com.example.UniversalConverter.Exceptions.IncorrectDimensionException;

import java.util.*;
import java.util.stream.Collectors;

public class MeasureGroup{

    final private MeasureGraph graph;

    public MeasureGraph getGraph() {
        return graph;
    }

    private final LinkedList<Unit> units = new LinkedList<>();

    public MeasureGroup(MeasureGraph graph) {
        this.graph = graph;
    }

    public void addUnit(Unit unit, int power){
        int index = units.indexOf(unit);
        if(index != -1){
            Unit existing_unit = units.get(index);
            if((existing_unit.getPower() + power)!= 0 ){
                existing_unit.setPower(existing_unit.getPower() + power);
            } else {
                units.remove(index);
            }
        } else {
            unit.setPower(power);
            units.add(unit);
        }
    }

    public Unit getUnitByName(String unitName){
        System.out.println("Here111 " + units);
        units.forEach(e-> System.out.println(e.getName() + " POWER " + e.getPower()));
        Optional<Unit> result = units.stream().filter(e->e.getName().equals(unitName)).findFirst();
        return result.orElse(null);
    }

    public MeasureGroup multiply(MeasureGroup measureGroup) throws IncorrectDimensionException {
        if(this.equals(measureGroup)){
            units.forEach(e -> measureGroup.addUnit(e, e.getPower()));
            return measureGroup;
        } else throw new IncorrectDimensionException();
    }

    public void invert(){
        units.forEach(e -> e.setPower(e.getPower()*(-1)));
    }

    Unit getNext(){
        return units.remove();
    }

    public boolean isConvertible(final MeasureGroup toGroup){
        if(toGroup == null) return false;
        long firstDim = this.units.stream().mapToLong(Unit::getPower).sum();
        long secondDim = toGroup.units.stream().mapToLong(Unit::getPower).sum();
        return firstDim == secondDim;
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

    public boolean isEmpty() {
        return units.isEmpty();
    }

    @Override
    public String toString() {
        return "MeasureGroup "
                + "graph = " + graph +
                " entities: " + units.stream().map(e -> "name: " + e.getName() + ", ").collect(Collectors.joining()) + " ]";
    }

    public void addUnit(Unit from) {
        addUnit(from, from.getPower());
    }
}