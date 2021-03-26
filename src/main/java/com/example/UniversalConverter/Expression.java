package com.example.UniversalConverter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * Этот класс описывает выражение K*[1st group of measure]*[2nd group of measure]..[n-th group of measure]
 */
public class Expression {

    private BigDecimal k;
    final private List<MeasureGroup> measures;

    Expression(List<MeasureGroup> measureGroups){
        measures = measureGroups;
    }

    public Expression invert(){
        measures.forEach(MeasureGroup::invert);
        return this;
    }

    public Expression multiply(Expression e) throws IncorrectDimensionException {
        for (MeasureGroup measureGroup: e.getMeasures()) {
            int index = measures.indexOf(measureGroup);
            if(index != -1){
                measures.add(measureGroup.multiply(measures.remove(index)));
            }else{
                throw new IncorrectDimensionException();
            }
        }
        return this;
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

    public boolean isConversionAvailable(final Expression expression){
        if (this == expression) return true;
        if (expression == null || getClass() != expression.getClass()) return false;

        return measures.stream().allMatch(e -> {
            MeasureGroup measureGroup = expression.measures.get(measures.indexOf(e));
            return e.isConvertible(measureGroup);
        });
    }
}
