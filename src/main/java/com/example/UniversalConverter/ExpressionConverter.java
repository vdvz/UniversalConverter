package com.example.UniversalConverter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;


/**
 * Выполняет преобразование Expression'a с накоплением коэффициента K выражения.
 * Использует алгоритм обхода графа в ширину.
 */
public class ExpressionConverter implements ExpressionConverter_I {

    private static final Logger logger = LogManager.getLogger(ExpressionConverter.class);
    private final RoundingMode roundPolitic;
    private final int maxScale;
    private BigDecimal divisorK = BigDecimal.ONE;
    private BigDecimal numeratorK = BigDecimal.ONE;

    public ExpressionConverter() {
        maxScale = 15;
        roundPolitic = RoundingMode.HALF_DOWN;
    }

    public ExpressionConverter(int maxScale, RoundingMode roundPolitic) {
        this.maxScale = maxScale;
        this.roundPolitic = roundPolitic;
    }

    @Override
    public BigDecimal convert(Expression from, Expression to) {
        ConversionRate conversionRate = new ConversionRate(BigDecimal.ONE, BigDecimal.ONE);
        List<MeasureGroup> toMeasureGroups = to.getMeasures();
        for (MeasureGroup fromGroup : from.getMeasures()) {
            MeasureGroup toGroup = toMeasureGroups.get(toMeasureGroups.indexOf(fromGroup));
            Unit toCon = new Unit(fromGroup.next().getName());
            ConversionRate fromReductionRate = reductionGroup(fromGroup, toCon);
            logger.debug("WHAT " + toGroup);
            ConversionRate toReductionRate = reductionGroup(toGroup, toCon);

            conversionRate = conversionRate.multiply(fromReductionRate.invert()).multiply(toReductionRate);

            ConversionRate conversionRateForGroup = convertGroup(fromGroup, toGroup);

            conversionRate.multiply(conversionRateForGroup);
        }

        logger.debug("Conversion rate " + conversionRate + " actual numerator " + numeratorK + " ,divisor " + divisorK);
        return conversionRate.getNumerator().divide(conversionRate.getDivisor(), maxScale, roundPolitic);
    }


    private ConversionRate reductionGroup(MeasureGroup group, Unit fromUnit){
        ConversionRate conversionRateForGroup = new ConversionRate();
        MeasureGraph conversionGraph = group.getGraph();
        logger.debug("Сокращение группы " + group.toString());
        logger.debug("Приведение к " + fromUnit);

        Node fromNode = conversionGraph.findNode(fromUnit.getName());

        var k=0;
        while (group.size() != 1){
            if(k>4) break;
            k++;
            Queue<MultiplicationUnit> neighbors = new LinkedList<>();

            fromNode.getNeighbors().forEach((node, conversionRate) -> {
                neighbors.add(new MultiplicationUnit(node, new ConversionRate(conversionRate)));
            });

            Set<Node> visitedNode = new HashSet<>();
            while (!neighbors.isEmpty()) {
                MultiplicationUnit mlUnit = neighbors.remove();
                Node currentNode = mlUnit.getNode();
                visitedNode.add(currentNode);

                logger.debug("Текущий unit: " + currentNode.getUnitName());

                Unit toUnit = group.getUnitByName(currentNode.getUnitName());
                if (!Objects.equals(toUnit, null)) {
                    int toPower = toUnit.getPower();
                    ConversionRate currentConversionRate = new ConversionRate(mlUnit.getConversionRate());
                    logger.debug("Такой сосед существует. Юнит перевода из" + fromUnit +" в Unit " + toUnit);
                    logger.debug("Кф перехода: " + currentConversionRate);
                    logger.debug("Степень преобразования: " + toPower);

                    conversionRateForGroup = conversionRateForGroup.multiply(currentConversionRate.pow(toPower).invert());

                    logger.debug("Общий conversionrate " + conversionRateForGroup);
                    logger.debug("Текущая группа: " + group.toString());
                    logger.debug("Доавленеие в группу " + toUnit + " степень " + (-1) * toPower);
                    group.addUnit(fromUnit, toPower);
                    group.addUnit(toUnit,(-1) * toPower);
                    logger.debug("Текущая группа: " + group.toString());
                    break;
                } else {
                    /*Добавляем всех соседей текущей ноды*/
                    currentNode.getNeighbors().forEach((node, transitionK) -> {
                        if(!visitedNode.contains(node)){
                            ConversionRate curConversionRate = new ConversionRate(mlUnit.getConversionRate());
                            neighbors.add(new MultiplicationUnit(node, curConversionRate.multiply(transitionK)));
                        }
                    });
                }
            }
        }

        return conversionRateForGroup;
    }

    private ConversionRate convertGroup(MeasureGroup fromGroup, MeasureGroup toGroup) {
        ConversionRate conversionRateForGroup = new ConversionRate(BigDecimal.ONE, BigDecimal.ONE);
        MeasureGraph conversionGraph = fromGroup.getGraph();
        while (!fromGroup.isEmpty()) {
            Queue<MultiplicationUnit> neighbors = new LinkedList<>();
            Unit fromUnit = fromGroup.getNext();

            logger.debug("Получили Unit из группы: " + fromUnit.getName());
            Node fromNode = conversionGraph.findNode(fromUnit.getName());

            fromNode.getNeighbors().forEach((node, conversionRate) -> {
                    neighbors.add(new MultiplicationUnit(node, new ConversionRate(conversionRate)));
            });

            Set<Node> visitedNode = new HashSet<>();
            while (!neighbors.isEmpty()) {
                MultiplicationUnit mlUnit = neighbors.remove();
                Node currentNode = mlUnit.getNode();
                visitedNode.add(currentNode);

                logger.debug("Текущая нода " + currentNode.getUnitName());

                /* Проверяем есть ли в группе элемент с таким же названием как у текущей ноды */
                int fromPower = fromUnit.getPower();
                Unit toUnit = toGroup.getUnitByName(currentNode.getUnitName());
                if (!Objects.equals(toUnit, null) && fromPower == toUnit.getPower()){
                    ConversionRate currentConversionRate = mlUnit.getConversionRate();
                    logger.debug("Конвертируем из " + fromUnit + " в " + toUnit);
                    logger.debug("Карент конвершен рейт " + currentConversionRate);
                    conversionRateForGroup = conversionRateForGroup.multiply(currentConversionRate.pow(fromPower));
                    logger.debug("Текущий кф преобразования " + conversionRateForGroup);
                    toGroup.addUnit(toUnit, (-1)*fromPower);
                    logger.debug("Текущая группа " + toGroup);
                    break;
                } else {
                    /*Добавляем всех соседей текущей ноды*/
                    currentNode.getNeighbors().forEach((node, transitionK) -> {
                        if(!visitedNode.contains(node)){
                            logger.debug("Добавяляем новую ноду: " + node.getUnitName());
                            var newTransitionK = new ConversionRate(mlUnit.getConversionRate()).multiply(transitionK);
                            logger.debug("Conversion rate will be " + newTransitionK);
                            neighbors.add(new MultiplicationUnit(node, new ConversionRate(newTransitionK)));

                        }
                    });
                }
            }
        }
        return conversionRateForGroup;
    }
}
