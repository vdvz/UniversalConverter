package com.example.UniversalConverter;

import java.math.BigDecimal;

public class ConversionRate {

    private BigDecimal numerator;
    private BigDecimal divisor;

    ConversionRate(BigDecimal numerator, BigDecimal divisor){
        this.numerator = numerator;
        this.divisor = divisor;
    }

    public BigDecimal getNumerator() {
        return numerator;
    }

    public void setDivisor(BigDecimal divisor) {
        this.divisor = divisor;
    }

    public void setNumerator(BigDecimal numerator) {
        this.numerator = numerator;
    }

    public BigDecimal getDivisor() {
        return divisor;
    }
}
