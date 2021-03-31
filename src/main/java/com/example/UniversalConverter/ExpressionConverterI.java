package com.example.UniversalConverter;

import java.math.BigDecimal;

interface ExpressionConverterI {

  BigDecimal convert(Expression from, Expression to);

}
