package com.example.UniversalConverter;

import java.util.List;
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

        List<MeasureGroup> toMeasureGroups = to.getMeasures();
        for (MeasureGroup fromGroup : from.getMeasures()) {
            MeasureGroup toGroup = toMeasureGroups.get(toMeasureGroups.indexOf(fromGroup));
            ConversionRate fromConversionRate = reductionGroup(fromGroup);
            ConversionRate toConversionRate = reductionGroup(toGroup);
            numeratorK = numeratorK.multiply(fromConversionRate.getDivisor()).multiply(toConversionRate.getNumerator());
            divisorK = divisorK.multiply(toConversionRate.getDivisor()).multiply(fromConversionRate.getNumerator());
            logger.debug("Start converting group " + fromGroup.toString() + " to group " + toGroup.toString());
            convertGroup(fromGroup, toGroup);
        }
        logger.debug("Текущий числитель: " + numeratorK);
        logger.debug("Текущий знаменатель: " + divisorK);
        return numeratorK.divide(divisorK, maxScale, roundPolitic);
    }


    private ConversionRate reductionGroup(MeasureGroup group){
        ConversionRate conversionRateForGroup = new ConversionRate(BigDecimal.ONE, BigDecimal.ONE);
        MeasureGraph conversionGraph = group.getGraph();
        logger.debug("Сокращение группы " + group.toString());
        int k = 0;
        while (group.size() != 1) {
            if(k>6) break;
            k++;
            Queue<MultiplicationUnit> neighbors = new LinkedList<>();
            Unit fromUnit = group.getNext();

            logger.debug("Получили Unit из группы: " + fromUnit.getName());
            Node fromNode = conversionGraph.findNode(fromUnit.getName());

            fromNode.getNeighbors().forEach((node, conversionRate) -> {
                neighbors.add(new MultiplicationUnit(node, conversionRate));
            });

            /*Проходим по всем нодам достяжимым из fromNode*/
            var a = 0;
            while (!neighbors.isEmpty()) {
                if(a>6) System.exit(-1);
                a++;
                MultiplicationUnit mlUnit = neighbors.remove();
                Node currentNode = mlUnit.getNode();
                ConversionRate currentConversionRate = mlUnit.getConversionRate();
                logger.debug("Сосед: " + currentNode.getUnitName());

                /* Проверяем есть ли в группе элемент с таким же названием как у текущей ноды */
                Unit toUnit = group.getUnitByName(currentNode.getUnitName());
                if (!Objects.equals(toUnit, null)) {
                    int fromPower = fromUnit.getPower();
                    BigDecimal newNumerator;
                    BigDecimal newDivisor;
                    logger.debug("Такой сосед существует. Юнит перевода из" + fromUnit +" в Unit " + toUnit);
                    if(fromPower > 0){
                        newNumerator = conversionRateForGroup.getNumerator().multiply(currentConversionRate.getNumerator().pow(fromPower));
                        newDivisor = conversionRateForGroup.getDivisor().multiply(currentConversionRate.getDivisor().pow(fromPower));
                    } else {
                        newNumerator = conversionRateForGroup.getNumerator().multiply(currentConversionRate.getDivisor().pow(Math.abs(fromPower)));
                        newDivisor = conversionRateForGroup.getDivisor().multiply(currentConversionRate.getNumerator().pow(Math.abs(fromPower)));
                    }

                    logger.info("Коэффициент сокращения числитель: " + newNumerator);
                    logger.info("Коэффициент сокращения знаменатель " + newDivisor);

                    conversionRateForGroup.setNumerator(newNumerator);
                    conversionRateForGroup.setDivisor(newDivisor);

                    group.addUnit(toUnit, fromUnit.getPower());
                    logger.debug("Текущая группа" + group.toString());
                    break;
                } else {
                    /*Добавляем всех соседей текущей ноды*/
                    logger.debug("Выполняем шаг и добавляем новых соседей");
                    currentNode.getNeighbors().forEach((node, transitionK) -> {
                        var newNumerator = mlUnit.getConversionRate().getNumerator().multiply(transitionK.getNumerator());
                        var newDivisor = mlUnit.getConversionRate().getDivisor().multiply(transitionK.getDivisor());
                        neighbors.add(new MultiplicationUnit(node, new ConversionRate(newNumerator, newDivisor)));
                    });
                }
            }
        }

        return conversionRateForGroup;
    }

    private void convertGroup(MeasureGroup fromGroup, MeasureGroup toGroup) {
        MeasureGraph conversionGraph = fromGroup.getGraph();
        int k = 0;
        while (!fromGroup.isEmpty()) {
            if(k>6) break;
            k++;
            Queue<MultiplicationUnit> neighbors = new LinkedList<>();
            Unit fromUnit = fromGroup.getNext();

            logger.debug("Получили Unit из группы: " + fromUnit.getName());
            Node fromNode = conversionGraph.findNode(fromUnit.getName());

            fromNode.getNeighbors().forEach((node, conversionRate) -> {
                neighbors.add(new MultiplicationUnit(node, conversionRate));
            });

            /*Проходим по всем нодам достяжимым из fromNode*/
            while (!neighbors.isEmpty()) {
                MultiplicationUnit mlUnit = neighbors.remove();
                Node currentNode = mlUnit.getNode();
                ConversionRate currentConversionRate = mlUnit.getConversionRate();
                logger.debug("Сосед: " + currentNode.getUnitName());

                /* Проверяем есть ли в группе элемент с таким же названием как у текущей ноды */
                Unit toUnit = toGroup.getUnitByName(currentNode.getUnitName());
                int fromPower = fromUnit.getPower();
                if (!Objects.equals(toUnit, null) && fromPower == toUnit.getPower()){

                    if(fromPower < 0 ){
                        divisorK = divisorK.multiply(currentConversionRate.getNumerator().pow(Math.abs(fromPower)));
                        numeratorK = numeratorK.multiply(currentConversionRate.getDivisor().pow(Math.abs(fromPower)));
                    }
                    if(fromPower > 0) {
                        numeratorK = numeratorK.multiply(currentConversionRate.getNumerator().pow(fromPower));
                        divisorK = divisorK.multiply(currentConversionRate.getDivisor().pow(fromPower));
                    }

                    toGroup.addUnit(toUnit, fromPower);
                    logger.debug("Текущая группа из которой преобразовывают: " + fromGroup);
                    logger.debug("Текущая группа в которую преобразовывают: " + toGroup);
                    break;
                } else {
                    /*Добавляем всех соседей текущей ноды*/
                    logger.debug("Выполняем шаг и добавляем новых соседей");
                    currentNode.getNeighbors().forEach((node, transitionK) -> {
                        var newNumerator = mlUnit.getConversionRate().getNumerator().multiply(transitionK.getNumerator());
                        var newDivisor = mlUnit.getConversionRate().getDivisor().multiply(transitionK.getDivisor());
                        neighbors.add(new MultiplicationUnit(node, new ConversionRate(newNumerator, newDivisor)));
                    });
                }
            }
        }
    }
}
