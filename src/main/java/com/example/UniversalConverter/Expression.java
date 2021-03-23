package com.example.UniversalConverter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;


/*Этот класс описывает выражение K*[1st group of measure]*[2nd group of measure]..[n-th group of measure] */
public class Expression {

    private BigDecimal k;
    final private List<MeasureGroup> measures;//2

    Expression(List<MeasureGroup> measureGroups){//2
        measures = measureGroups;
    }

    public Expression invert(){
        return null;
    }

    public Expression multiply(Expression e){
        return e;
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
}
