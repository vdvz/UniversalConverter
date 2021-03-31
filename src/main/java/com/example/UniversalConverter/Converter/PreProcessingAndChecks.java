package com.example.UniversalConverter.Converter;

import com.example.UniversalConverter.Exceptions.IncorrectDimensionException;
import com.example.UniversalConverter.RequestRepresentation.Expression;

public class PreProcessingAndChecks {

  public static void checkDimension(final Expression from, final Expression to)
      throws IncorrectDimensionException {
    if (!from.isConversionAvailable(to) || !to.isConversionAvailable(from)) {
      throw new IncorrectDimensionException();
    }
  }

  public void preprocessing(Expression fromExpression, Expression toExpression)
      throws IncorrectDimensionException {

    checkDimension(fromExpression, toExpression);

  }

}
