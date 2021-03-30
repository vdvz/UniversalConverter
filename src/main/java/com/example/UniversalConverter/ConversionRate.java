package com.example.UniversalConverter;

import java.math.BigDecimal;
import java.util.Objects;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

    public BigDecimal getNumerator() {
        return numerator;
    }

    public BigDecimal getDivisor() {
        return divisor;
    }

    public ConversionRate pow(int n){
        if(n < 0){
            invert();
            n = Math.abs(n);
        }
        numerator = numerator.pow(n);
        divisor = divisor.pow(n);

        return this;
    }

    public ConversionRate invert(){
        var sub = numerator;
        numerator = divisor;
        divisor = sub;
        return this;
    }

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
        return Objects.equals(numerator, that.numerator) && Objects
            .equals(divisor, that.divisor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numerator, divisor);
    }
}
