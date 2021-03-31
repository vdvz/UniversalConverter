package com.example.UniversalConverter.Converter;

import com.example.UniversalConverter.RequestRepresentation.Expression;
import java.math.BigDecimal;

public interface ExpressionConverterI {

  BigDecimal convert(Expression from, Expression to);

}
