package com.example.UniversalConverter;

import com.example.UniversalConverter.Exceptions.IncorrectDimensionException;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * MeasureGroup группирует Unit'ы относящиеся(Node с таким же именем содержится в графе) к одному графу
 * Например: {км, м, см} - группа построенная на графе(MeasureGraph) мер длины, где км, м, см - Unit'ы;
 *           {ч, мин} - группа построенная на графе(MeasureGraph) мер времени, где ч, мин - Unit'ы;
 */
public class MeasureGroup {

    final private MeasureGraph graph;

    private final LinkedList<Unit> units = new LinkedList<>();

    public MeasureGroup(MeasureGraph graph) {
        this.graph = graph;
    }

    public MeasureGraph getGraph() {
        return graph;
    }

    /**
     * Добавляет новый unit в group'у
     *
     * @param unit добавляемый unit
     */
    public void addUnit(Unit unit) {
        addUnit(unit, unit.getPower());
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

    /**
     * Возвращает группу которая является произведением this и group.
     *
     * @param group - перемножаемая группа
     * @return group - явялющаяся произведением this на group
     * @throws IncorrectDimensionException - если !this.equals(group)
     */
    public MeasureGroup multiply(MeasureGroup group) throws IncorrectDimensionException {
        if (this.equals(group)) {
            units.forEach(e -> group.addUnit(e, e.getPower()));
            return group;
        } else {
            throw new IncorrectDimensionException();
        }
    }

    /**
     * Инвертирует группу, т.е a, b - unit'ы в текущей group'e, a -> b = a^(-1).
     */
    public void invert() {
        units.forEach(e -> e.setPower(e.getPower() * (-1)));
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
     * Проверяет возможно ли выполнить преобразования в Group'е
     *
     * @return true если преобразование возможно, false инчае
     */
    public boolean isConvertible() {
        return this.units.stream().mapToLong(Unit::getPower).sum() == 0;
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

    public boolean isEmpty() {
        return units.isEmpty();
    }

    @Override
    public String toString() {
        return "MeasureGroup "
                + "graph = " + graph.hashCode() +
                " entities: [ " + units.stream()
                .map(e -> "name: " + e.getName() + " power " + e.getPower() + ", ")
                .collect(Collectors.joining()) + " ]";
    }

  public int size() {
        return units.size();
  }

    public boolean isConvertible(final MeasureGroup measureGroup) {
        if(measureGroup == null) return false;
        return measureGroup.units.stream().mapToLong(Unit::getPower).sum() == this.units.stream().mapToLong(Unit::getPower).sum();
    }
}