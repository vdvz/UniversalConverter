package com.example.UniversalConverter.Converter;

import com.example.UniversalConverter.RequestRepresentation.Expression;
import com.example.UniversalConverter.RequestRepresentation.MeasureGroup;
import com.example.UniversalConverter.RequestRepresentation.Unit;
import com.example.UniversalConverter.RulesRepresentation.Graph.MeasureGraph;
import com.example.UniversalConverter.RulesRepresentation.Graph.Node;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Выполняет преобразование Expression'a с накоплением коэффициента K выражения. Использует алгоритм
 * обхода графа в ширину.
 */
public class UniversalExpressionConverter implements ExpressionConverterI {

  private static final Logger logger = LogManager.getLogger(UniversalExpressionConverter.class);
  private static final int REQUIRED_SIGNIFICANT_DIGITS = 15;
  private final RoundingMode roundPolitic;
  private final int maxScale;
  private final int increaseFactorForScale;

  public UniversalExpressionConverter() {
    maxScale = 16;
    increaseFactorForScale = 10;
    roundPolitic = RoundingMode.HALF_DOWN;
  }

  public UniversalExpressionConverter(int maxScale, int increaseFactorForScale,
      RoundingMode roundPolitic) {
    this.maxScale = maxScale;
    this.increaseFactorForScale = increaseFactorForScale;
    this.roundPolitic = roundPolitic;
  }

  @Override
  public BigDecimal convert(Expression from, Expression to) {
    ConversionRate conversionRate = new ConversionRate(BigDecimal.ONE, BigDecimal.ONE);
    List<MeasureGroup> toMeasureGroups = to.getMeasures();

    ConversionRate fromReductionRate = new ConversionRate();
    ConversionRate toReductionRate = new ConversionRate();

    logger.info("Преобразовываем выражения к общему множителю");
    Map<MeasureGroup, Unit> unitsToReduce = new HashMap<>();

    from.getMeasures().forEach(measureGroup -> {
      Unit unitToReduce;
      if ((unitToReduce = unitsToReduce.get(measureGroup)) == null) {
        unitToReduce = new Unit(measureGroup.next().getName());
      }
      fromReductionRate.multiply(reductionGroup(measureGroup, unitToReduce));
    });

    to.getMeasures().forEach(measureGroup -> {
      Unit unitToReduce;
      if ((unitToReduce = unitsToReduce.get(measureGroup)) == null) {
        unitToReduce = new Unit(measureGroup.next().getName());
      }
      toReductionRate.multiply(reductionGroup(measureGroup, unitToReduce));
    });

    conversionRate = conversionRate.multiply(fromReductionRate).multiply(toReductionRate.invert());

    for (MeasureGroup fromGroup : from.getMeasures()) {
      int index = toMeasureGroups.indexOf(fromGroup);
      if (index == -1) {
        continue;
      }
      MeasureGroup toGroup = toMeasureGroups.get(index);

      logger.debug("Преобразовываем величины");
      ConversionRate conversionRateForGroup = convertGroup(fromGroup, toGroup);

      conversionRate.multiply(conversionRateForGroup);
    }

    return setRequiredSignificantDigits(conversionRate.getNumerator(), conversionRate.getDivisor());
  }

  /**
   * Преобразовывает все Unit'ы в группе к общему множителю
   *
   * @param group    группа для которой выполняютмя преобразования
   * @param fromUnit множитель
   * @return коэффициент преобразования
   */
  private ConversionRate reductionGroup(MeasureGroup group, Unit fromUnit) {
    ConversionRate conversionRateForGroup = new ConversionRate();
    MeasureGraph conversionGraph = group.getGraph();
    logger.debug("Выполняем сокращение группы " + group.toString());
    logger.debug("Приводим к элементу " + fromUnit.getName());

    /*Поиском в ширину ищем коэфициент преобразования (K) из Node с именем fromUnit,
    до Node's с именами такими, что в группе существует unit c таким же именем.
    т.е node.getUnitName().equals(unit.getName()). Тогда чтобы преобразовать Unit из группы к Unit'у с именем вершины
    мы должны инвертировать полученый коэффициент преобразования*/
    Node fromNode = conversionGraph.getNodeByName(fromUnit.getName());
    while (group.size() > 1) {
      logger.debug("Group size " + group.size());
      Queue<MultiplicationConversionRates> neighbors = new LinkedList<>();

      fromNode.getNeighbors().forEach((node, conversionRate) -> {
        neighbors.add(new MultiplicationConversionRates(node, new ConversionRate(conversionRate)));
      });

      /*во избежание зацикливания алгоритма в уже посещенные вершины не заходим*/
      Set<Node> visitedNode = new HashSet<>();
      while (!neighbors.isEmpty()) {
        MultiplicationConversionRates mlUnit = neighbors.remove();
        Node currentNode = mlUnit.getNode();
        visitedNode.add(currentNode);

        logger.debug("Проверяем принадлежит ли unit группе: " + currentNode.getUnitName());

        Unit toUnit = group.getUnitByName(currentNode.getUnitName());
        /*если unit к которому преобразовываем эквивалентен текущему unit'у, то пропускаем его*/
        if (!Objects.equals(toUnit, null) && !fromUnit.equals(toUnit)) {
          int toPower = toUnit.getPower();
          logger.debug("Выполняем перевод из " + toUnit.getName() + " в " + fromUnit.getName());
          logger.debug("Степень преобразования: " + toPower);
          ConversionRate currentConversionRate = new ConversionRate(mlUnit.getConversionRate());

          /*Накапливаем инвертированный коэффициент(т.к обратный путь) с соответсвующей степенью */
          conversionRateForGroup = conversionRateForGroup
              .multiply(currentConversionRate.pow(toPower).invert());

          /*Убираем из группы преобразованный Unit, и добавляем в группу Unit к которому преобразовывали
          с соответсвующей степенью*/
          group.addUnit(fromUnit, toPower);
          group.addUnit(toUnit, (-1) * toPower);
          logger.debug("Текущая группа: " + group.toString());
          logger.debug("Общий кф преобразования " + conversionRateForGroup);
          break;
        } else {
          /*Добавляем всех соседей текущего Node*/
          currentNode.getNeighbors().forEach((node, transitionK) -> {
            if (!visitedNode.contains(node)) {
              ConversionRate curConversionRate = new ConversionRate(mlUnit.getConversionRate());
              neighbors.add(
                  new MultiplicationConversionRates(node, curConversionRate.multiply(transitionK)));
            }
          });
        }
      }
    }

    return conversionRateForGroup;
  }

  private ConversionRate convertGroup(MeasureGroup fromGroup, MeasureGroup toGroup) {
    logger.debug("Конвертируем группу" + fromGroup + " к " + toGroup);

    ConversionRate conversionRateForGroup = new ConversionRate(BigDecimal.ONE, BigDecimal.ONE);
    MeasureGraph conversionGraph = fromGroup.getGraph();
    while (!fromGroup.isEmpty()) {
      Queue<MultiplicationConversionRates> neighbors = new LinkedList<>();
      Unit fromUnit = fromGroup.getNext();

      if (toGroup.getUnitByName(fromUnit.getName()) != null) {
        break;
      }

      Set<Node> visitedNode = new HashSet<>();

      logger.debug("Получили Unit из группы: " + fromUnit.getName());
      Node fromNode = conversionGraph.getNodeByName(fromUnit.getName());

      fromNode.getNeighbors().forEach((node, conversionRate) -> {
        neighbors.add(new MultiplicationConversionRates(node, new ConversionRate(conversionRate)));
      });

      visitedNode.add(fromNode);

      while (!neighbors.isEmpty()) {
        MultiplicationConversionRates mlUnit = neighbors.remove();
        Node currentNode = mlUnit.getNode();
        visitedNode.add(currentNode);

        logger.debug("Текущая нода " + currentNode.getUnitName());

        /* Проверяем есть ли в группе элемент с таким же названием как у текущей ноды */
        int fromPower = fromUnit.getPower();
        Unit toUnit = toGroup.getUnitByName(currentNode.getUnitName());
        if (!Objects.equals(toUnit, null) && fromPower == toUnit.getPower()) {
          ConversionRate currentConversionRate = mlUnit.getConversionRate();
          conversionRateForGroup = conversionRateForGroup.multiply(currentConversionRate.pow(fromPower));

          logger.debug("Текущий кф преобразования " + conversionRateForGroup);
          toGroup.addUnit(toUnit, (-1) * fromPower);
          logger.debug("Текущая группа " + toGroup);
          break;
        } else {
          /*Добавляем всех соседей текущей ноды*/
          currentNode.getNeighbors().forEach((node, transitionK) -> {
            if (!visitedNode.contains(node)) {

                var newTransitionK = new ConversionRate(mlUnit.getConversionRate())
                  .multiply(transitionK);

              neighbors.add(new MultiplicationConversionRates(node, new ConversionRate(newTransitionK)));

            }
          });
        }
      }
    }
    return conversionRateForGroup;
  }

  private BigDecimal setRequiredSignificantDigits(BigDecimal numerator, BigDecimal divisor) {
    int targetScale = maxScale;
    BigDecimal answer;
    int prevCountOfSignificantDigits = 0;
    int currentCountOfSignificantDigits;
    while ((currentCountOfSignificantDigits = countSignificantDigits(
        answer = numerator.divide(divisor, targetScale, roundPolitic)))
        < REQUIRED_SIGNIFICANT_DIGITS
        && currentCountOfSignificantDigits != prevCountOfSignificantDigits) {

      prevCountOfSignificantDigits = currentCountOfSignificantDigits;
      targetScale *= increaseFactorForScale;
    }
    return answer;
  }

  private int countSignificantDigits(BigDecimal value) {
    value = value.stripTrailingZeros();
    return value.scale() < 0 ? value.precision() - value.scale() : value.precision();
  }

}
