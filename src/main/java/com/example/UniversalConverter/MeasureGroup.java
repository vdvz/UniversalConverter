package com.example.UniversalConverter;

import com.example.UniversalConverter.Exceptions.IncorrectDimensionException;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * MeasureGroup группирует Unit'ы относящиеся к одному графу (Node с таким же именем содержится в MeasureGraph'e).
 * Например: {км, м, см} - группа построенная на графе мер длины, где км, м, см - Unit'ы, принадлежащие группе;
 *           {ч, мин} - группа построенная на графе мер времени, где ч, мин - Unit'ы, принадлежащие группе;
 */
public class MeasureGroup implements Iterator<Unit>{

    final private MeasureGraph graph;

    private final LinkedList<Unit> units = new LinkedList<>();

    public MeasureGroup(MeasureGraph graph) {
        this.graph = graph;
    }

    public MeasureGraph getGraph() {
        return graph;
    }

    /**
     * Добавление нового Unit'а в группу. Если в группе существует такой Unit, то их степени
     * складываются.
     *
     * @param addedUnit - добавляемый Unit
     * @param power     - степень добавляемого Unit'а
     */
    public void addUnit(Unit addedUnit, int power) {
        int index;
        if ((index = units.indexOf(addedUnit)) != -1) {
            Unit existingUnit = units.get(index);
            if ((existingUnit.getPower() + power) != 0) {
                existingUnit.setPower(existingUnit.getPower() + power);
            } else {
                units.remove(index);
            }
        } else {
            addedUnit.setPower(power);
            units.add(addedUnit);
        }
    }

    public Unit getUnitByName(String unitName) {
        Optional<Unit> result = units.stream().filter(e -> e.getName().equals(unitName)).findFirst();
        return result.orElse(null);
    }

    public boolean isEmpty() {
        return units.isEmpty();
    }

    public int size() {
        return units.size();
    }

    public int power(){
        return units.stream().mapToInt(Unit::getPower).sum();
    }

    public boolean isConvertible(final MeasureGroup measureGroup) {
        if(measureGroup == null) return false;
        return this.power() == measureGroup.power();
    }

    /**
     * Получает из Group'ы следующий Unit
     *
     * @return возвращаемый Unit
     */
    public Unit getNext() {
        return units.remove();
    }

    /**
     * Проверяет эквивалентен ли Object o текущей Group'e. Group'ы эквивалентны если имеют один
     * MeasureGraph
     *
     * @param o проверяемый объект
     * @return true если объекты эквиваленты, false иначе
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MeasureGroup that = (MeasureGroup) o;
        return Objects.equals(graph, that.graph);
    }

    @Override
    public int hashCode() {
        return Objects.hash(graph);
    }

    @Override
    public String toString() {
        return "MeasureGroup "
                + "graph = " + graph.hashCode() +
                " entities: [ " + units.stream()
                .map(e -> "name: " + e.getName() + " power " + e.getPower() + ", ")
                .collect(Collectors.joining()) + " ]";
    }

    @Override
    public boolean hasNext() {
        return units.iterator().hasNext();
    }

    @Override
    public Unit next() {
        return units.iterator().next();
    }

}