package com.example.UniversalConverter;

import com.example.UniversalConverter.Exceptions.IncorrectDimensionException;
import org.apache.commons.collections4.ArrayStack;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Этот класс описывает выражение K*[1st group of measure]*[2nd group of measure]..[n-th group of measure]
 */
public class Expression {

    private BigDecimal k = BigDecimal.ONE;
    final private List<MeasureGroup> measures;

    Expression(List<MeasureGroup> measureGroups){
        measures = measureGroups;
    }

    public Expression invert(){
        measures.forEach(MeasureGroup::invert);
        return this;
    }

    public Expression multiply(Expression e)  {
        List<MeasureGroup> measureGroups = new ArrayList<>(measures);
        for (MeasureGroup gr: e.getMeasures()) {
            int index = measureGroups.indexOf(gr);
            if(index != -1) {
                try {
                    measureGroups.add(gr.multiply(measures.remove(index)));
                } catch (IncorrectDimensionException ignored) {
                }
            }else{
                measureGroups.add(gr);
            }
        }
        return new Expression(measureGroups);
    }

    public List<MeasureGroup> getMeasures() {
        return measures;
    }

    public BigDecimal getK() {
        return k;
    }

    public void setK(BigDecimal k) {
        this.k = k;
    }

    public boolean isConversionAvailable(){
        return measures.stream().allMatch(MeasureGroup::isConvertible);
    }

    @Override
    public String toString() {
        return "Expression " +
                " k= " + k + " count of measureGroups " + measures.size() +
                " measures [" + measures.stream().map(MeasureGroup::toString).collect(Collectors.joining()) +
                ']';
    }
}
