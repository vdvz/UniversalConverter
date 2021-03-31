package com.example.UniversalConverter;

import com.example.UniversalConverter.Exceptions.IncorrectDimensionException;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Этот класс описывает выражение K*[1st group of measure]*[2nd group of measure]..[n-th group of
 * measure]
 * K - коэффициент
 * [1..n groups of measure] - MeasureGroup
 */
public class Expression {

    private static final Logger logger = LogManager.getLogger(Expression.class);

    final private List<MeasureGroup> measures;

    Expression(List<MeasureGroup> measureGroups) {
        measures = measureGroups;
    }

    /**
     * Возвращает список групп данного выражения
     * @return MeasureGroup's
     */
    public List<MeasureGroup> getMeasures() {
        return measures;
    }

    /**
     * Преобразовать выражение - найти коэффициент K такой, что K*this = expression,
     * где K - коэффициент преобразования (ConversionRate)
     * @return true если выражение можно преобразовать к выражению expression, false инчае
     */
    public boolean isConversionAvailable(final Expression expression) {
        if (this == expression) return true;
        if (expression == null || getClass() != expression.getClass()) return false;

        return measures.stream().allMatch(e -> {
            int index = expression.measures.indexOf(e);
            if(index==-1){
                return e.power() == 0;
            }
            MeasureGroup measureGroup = expression.measures.get(index);
            return e.isConvertible(measureGroup);
        });
    }

    @Override
    public String toString() {
        return "Expression " + " count of measureGroups " + measures.size() +
                " measures [" + measures.stream().map(MeasureGroup::toString).collect(Collectors.joining())
                +
                ']';
    }
}
