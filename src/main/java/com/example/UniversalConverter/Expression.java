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

    private static final Logger logger = LogManager.getLogger(ServiceRunner.class);

    private BigDecimal k = BigDecimal.ONE;
    final private List<MeasureGroup> measures;

    Expression(List<MeasureGroup> measureGroups) {
        measures = measureGroups;
    }

    /**
     * Инвертирует данный Expression, т.е K * MeasureGroups -> K^(-1) * MeasureGroups^(-1)
     *
     * @return инвертированное выражение
     */
    public Expression invert() {
        k = BigDecimal.ONE.divide(k, RoundingMode.HALF_DOWN);
        measures.forEach(MeasureGroup::invert);
        return this;
    }


    /**
     * Перемножает this Expression с Expression e
     *
     * @param e выражение для перемножения
     * @return this*e
     */
    public Expression multiply(Expression e) {
        List<MeasureGroup> measureGroups = new ArrayList<>(measures);
        logger.debug(measureGroups.toString());
        for (MeasureGroup gr : e.getMeasures()) {
            int index = measureGroups.indexOf(gr);
            if (index != -1) {
                try {
                    measureGroups.add(gr.multiply(measureGroups.remove(index)));
                } catch (IncorrectDimensionException ignored) {
                }
            } else {
                measureGroups.add(gr);
            }
        }
        return new Expression(measureGroups);
    }

    /**
     * @return Возвращает группы данного Expression'a
     */
    public List<MeasureGroup> getMeasures() {
        return measures;
    }

    /**
     * @return Коэффициент данного выражения
     */
    public BigDecimal getK() {
        return k;
    }

    /**
     * Устанавливает коэффициент данного выражения
     *
     * @param k коэффициент
     */
    public void setK(BigDecimal k) {
        this.k = k;
    }

    /**
     * Преобразовать выражение - найти коэффициент K такой, что K*[MeasureGroups] = 1
     *
     * @return true если выражение можно преобразовать, false инчае
     */
    public boolean isConversionAvailable(final Expression expression) {
        if (this == expression) return true;
        if (expression == null || getClass() != expression.getClass()) return false;

        return measures.stream().allMatch(e -> {
            MeasureGroup measureGroup = expression.measures.get(measures.indexOf(e));
            return e.isConvertible(measureGroup);
        });
    }

    @Override
    public String toString() {
        return "Expression " +
                " k= " + k + " count of measureGroups " + measures.size() +
                " measures [" + measures.stream().map(MeasureGroup::toString).collect(Collectors.joining())
                +
                ']';
    }
}
