package com.example.UniversalConverter.Controller;

import com.example.UniversalConverter.Converter.ExpressionConverterI;
import com.example.UniversalConverter.Converter.PreProcessingAndChecks;
import com.example.UniversalConverter.Converter.UniversalExpressionConverter;
import com.example.UniversalConverter.Exceptions.IncorrectDimensionException;
import com.example.UniversalConverter.Exceptions.NoAvailableRulesException;
import com.example.UniversalConverter.Exceptions.UnknownNameOfUnitException;
import com.example.UniversalConverter.Parser.ConversionRequestParser;
import com.example.UniversalConverter.RequestRepresentation.ConversionRequest;
import com.example.UniversalConverter.RequestRepresentation.Expression;
import com.example.UniversalConverter.RulesRepresentation.Rules;
import com.example.UniversalConverter.RulesRepresentation.RulesManager;
import java.math.BigDecimal;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class ConversionController {

  private final static Logger logger = LogManager.getLogger(ConversionController.class);

  private static final int COUNT_DIGITS_IN_RESPONSE = 15;
  private final ConversionRequestParser parser = new ConversionRequestParser();
  private final ExpressionConverterI converter = new UniversalExpressionConverter();
  private final PreProcessingAndChecks preprocessing = new PreProcessingAndChecks();
  private Rules rules;

  {
    try {
      rules = RulesManager.getRules();
    } catch (NoAvailableRulesException e) {
      e.printStackTrace();
    }
  }

  @PostMapping("/convert")
  ResponseEntity<String> getConversionRate(@RequestBody ConversionRequest request)
      throws IncorrectDimensionException, UnknownNameOfUnitException {
    logger.info(
        "Get request for conversion. Need to convert from " + request.getFrom() + " to " + request
            .getTo());

    Expression from = parser.parseStringToExpression(request.getFrom(), rules);
    Expression to = parser.parseStringToExpression(request.getTo(), rules);

    preprocessing.preprocessing(from, to);

    BigDecimal answer = converter.convert(from, to);

    return new ResponseEntity<>(prepareResponse(answer), HttpStatus.OK);
  }

  /**
   * Обрабатывает результат конвертации, наклаывая ограничения по значащим символам
   *
   * @param value Результат конвертации
   * @return Строка ответа
   */
  private String prepareResponse(BigDecimal value) {

    if (value.compareTo(BigDecimal.ONE) > 0) {
      var responseStr = value.stripTrailingZeros().toPlainString();
      return responseStr.length() < COUNT_DIGITS_IN_RESPONSE ? responseStr
          : responseStr.substring(0, COUNT_DIGITS_IN_RESPONSE);
    } else {
      int indexOfFirstNonZeroDigit = Math.abs(value.precision() - value.scale() - 1) + 1;
      var responseStr = value.stripTrailingZeros().toPlainString();
      if (responseStr.length() - indexOfFirstNonZeroDigit <= COUNT_DIGITS_IN_RESPONSE) {
        return responseStr;
      } else {
        return responseStr.substring(0, indexOfFirstNonZeroDigit + COUNT_DIGITS_IN_RESPONSE);
      }
    }
  }

}