package com.example.UniversalConverter.Parser;

import com.example.UniversalConverter.Exceptions.UnknownNameOfUnitException;
import com.example.UniversalConverter.RequestRepresentation.Expression;
import com.example.UniversalConverter.RulesRepresentation.Rules;

public interface ParserI {

  Expression parseStringToExpression(String sourceStr, Rules rules)
      throws UnknownNameOfUnitException;
}
