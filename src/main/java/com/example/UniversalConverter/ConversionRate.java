package com.example.UniversalConverter;

import java.math.BigDecimal;
import java.util.Objects;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Коэффициент преобразования. Для сохранения точности копим отдельно числитель и знаменатель,
 * тогда на примерах (1/3)*3 точность будет сохраняться
 */
public class ConversionRate {
    private static final Logger logger = LogManager.getLogger(ConversionRate.class);

    private BigDecimal numerator;
    private BigDecimal divisor;

    public ConversionRate(){
        this.numerator = BigDecimal.ONE;
        this.divisor = BigDecimal.ONE;
    }

    public ConversionRate(final ConversionRate conversionRate){
        this.numerator = conversionRate.numerator;
        this.divisor = conversionRate.divisor;
    }

    public ConversionRate(final BigDecimal numerator, final BigDecimal divisor){
        this.numerator = numerator;
        this.divisor = divisor;
    }

    /**
     * @return возвращаемый числитель
     */
    public BigDecimal getNumerator() {
        return numerator;
    }

    /**
     * @return возвращаемый знаменатель
     */
    public BigDecimal getDivisor() {
        return divisor;
    }

    /**
     * Возводит ConversionRate в степень n
     * @param n степень
     * @return ConversionRate^n
     */
    public ConversionRate pow(int n){
        if(n < 0){
            invert();
            n = Math.abs(n);
        }
        numerator = numerator.pow(n);
        divisor = divisor.pow(n);

        return this;
    }

    /**
     * Инвертирует ConversionRate, т.е a -> 1/a
     * @return инвертированный ConversionRate
     */
    public ConversionRate invert(){
        var sub = numerator;
        numerator = divisor;
        divisor = sub;
        return this;
    }

    /**
     * Перемножает this * multiplicand
     * @param multiplicand множитель для перемножения
     * @return this * multiplicand
     */
    public ConversionRate multiply(final ConversionRate multiplicand){
        logger.debug("Multiply this " + this + " on " + multiplicand);
        this.numerator = numerator.multiply(multiplicand.numerator);
        this.divisor = divisor.multiply(multiplicand.divisor);
        return this;
    }

    @Override
    public String toString() {
        return "ConversionRate{" +
            "numerator=" + numerator +
            ", divisor=" + divisor +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ConversionRate that = (ConversionRate) o;
        return numerator.compareTo(that.numerator) == 0 &&
            divisor.compareTo(that.divisor) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(numerator, divisor);
    }
}
