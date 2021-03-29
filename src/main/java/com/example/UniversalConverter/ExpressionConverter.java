package com.example.UniversalConverter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;


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
    public void convert(Expression expression) {
        for (MeasureGroup group : expression.getMeasures()) {
            logger.debug("Start converting group " + group.toString());
            convertGroup(group);
        }
        expression.setK(numeratorK.divide(divisorK, maxScale, roundPolitic));
    }

    private void convertGroup(MeasureGroup currentGroup) {
        MeasureGraph conversionGraph = currentGroup.getGraph();

        while (!currentGroup.isEmpty()) {
            Queue<MultiplicationUnit> neighbors = new LinkedList<>();
            Unit fromUnit = currentGroup.getNext();

            logger.debug("Получили Unit из группы: " + fromUnit.getName());
            Node fromNode = conversionGraph.findNode(fromUnit.getName());

            if (conversionGraph.isRootNode(fromNode)) {
                logger.debug("Текущая вершина корневая");
                currentGroup.addUnit(fromUnit);
                continue;
            }

            fromNode.getNeighbors().forEach((node, conversionRate) -> {
                neighbors.add(new MultiplicationUnit(node, conversionRate));
            });

            /*Проходим по всем нодам достяжимым из fromNode*/
            while (!neighbors.isEmpty()) {
                MultiplicationUnit mlUnit = neighbors.remove();
                Node currentNode = mlUnit.getNode();
                BigDecimal currentK = mlUnit.getK();
                logger.debug("Сосед: " + currentNode.getUnitName());

                /* Проверяем есть ли в группе элемент с таким же названием как у текущей ноды */
                Unit toUnit = currentGroup.getUnitByName(currentNode.getUnitName());
                if (!Objects.equals(toUnit, null)) {
                    /* Можно выполнить преобразование */
                    int fromPower = fromUnit.getPower();
                    int toPower = toUnit.getPower();

                    if (fromPower < 0 && toPower > 0) {
                        /*Если мы преобразуем из */
                        var sub = divisorK;
                        divisorK = numeratorK;
                        numeratorK = sub;
                        logger.debug("Инвертируем итоговый КФ");

                        numeratorK = numeratorK.multiply(currentK.pow(Math.abs(fromPower)));
                        logger.debug("Копим числитель " + numeratorK);
                    }
                    if (toPower < 0) {
                        divisorK = divisorK.multiply(currentK.pow(Math.abs(fromPower)));
                        logger.debug("Копим знаменатель " + divisorK);
                    }
                    if (fromPower > 0 && toPower > 0) {
                        numeratorK = numeratorK.multiply(currentK.pow(fromPower));
                        logger.debug("Копим числитель " + numeratorK);
                    }


                    /*т.к в группе уже есть элемент toUnit, то степени просто сложатся*/
                    currentGroup.addUnit(toUnit, fromUnit.getPower());

                    break;
                } else {
                    /*Добавляем всех соседей текущей ноды*/
                    logger.debug("Выполняем шаг и добавляем новых соседей");
                    currentNode.getNeighbors().forEach((node, transitionK) -> {
                        neighbors.add(new MultiplicationUnit(node, mlUnit.getK().multiply(transitionK)));
                    });
                }

                if (neighbors.isEmpty() && !currentGroup.isEmpty()) {
                    logger.debug("Добавляем элемент в конец группы");
                    currentGroup.addUnit(fromUnit);
                    logger.debug("Текущий вид группы " + currentGroup.toString());
                }
            }
        }
    }
}
