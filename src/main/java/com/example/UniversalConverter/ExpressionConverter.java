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



    class PowK{
        public void invert(){

        }

        private BigDecimal numerator = BigDecimal.ONE;

        private BigDecimal divisor = BigDecimal.ONE;

        public BigDecimal getNumerator() {
            return numerator;
        }

        public void setNumerator(BigDecimal numerator) {
            this.numerator = numerator;
        }

        public BigDecimal getDivisor() {
            return divisor;
        }

        public void setDivisor(BigDecimal divisor) {
            this.divisor = divisor;
        }
    }

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
            logger.debug("Start converting group " + fromGroup.toString() + " to group " + toGroup.toString());
            convertGroup(fromGroup, toGroup);
        }
        logger.debug("Текущий числитель: " + numeratorK);
        logger.debug("Текущий знаменатель: " + divisorK);
        return numeratorK.divide(divisorK, maxScale, roundPolitic);
    }

    private void convertGroup(MeasureGroup fromGroup, MeasureGroup toGroup) {
        BigDecimal localNumeratorK = BigDecimal.ONE;
        BigDecimal localDivisorK = BigDecimal.ONE;

        MeasureGraph conversionGraph = fromGroup.getGraph();
        int k = 0;
        while (!fromGroup.isEmpty()) {
            if(k>6) break;
            k++;
            Queue<MultiplicationUnit> neighbors = new LinkedList<>();
            Unit fromUnit = fromGroup.getNext();

            logger.debug("Получили Unit из группы: " + fromUnit.getName());
            Node fromNode = conversionGraph.findNode(fromUnit.getName());

            if (conversionGraph.isRootNode(fromNode)) {
                logger.debug("Текущая вершина корневая");
                fromGroup.addUnit(fromUnit);
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
                Unit toUnit = toGroup.getUnitByName(currentNode.getUnitName());
                int fromPower = fromUnit.getPower();
                if (!Objects.equals(toUnit, null) && fromPower == toUnit.getPower()){

                    if(fromPower < 0 ){
                        divisorK = divisorK.multiply(currentK.pow(Math.abs(fromPower)));
                    }
                    if(fromPower > 0) {
                        numeratorK = numeratorK.multiply(currentK.pow(fromPower));
                    }

                    toGroup.addUnit(toUnit, fromPower);
                    break;
                } else {
                    /*Добавляем всех соседей текущей ноды*/
                    logger.debug("Выполняем шаг и добавляем новых соседей");
                    currentNode.getNeighbors().forEach((node, transitionK) -> {
                        neighbors.add(new MultiplicationUnit(node, mlUnit.getK().multiply(transitionK)));
                    });
                }
                if (neighbors.isEmpty() && !currentGroup.isEmpty()) {
                    logger.info("Преобразование в корневую вершину и добавляем ее в группу");
                    int power = fromUnit.getPower();
                    if(power < 0){
                        localDivisorK = localDivisorK.multiply(currentK.pow(Math.abs(power)));
                        logger.info("Копим знаменатель " + localDivisorK);
                    }else{
                        localNumeratorK = localNumeratorK.multiply(currentK.pow(power));
                        logger.info("Копим числитель " + localNumeratorK);
                    }
                    currentGroup.addUnit(new Unit(currentNode.getUnitName(), power));
                    logger.info("Текущий вид группы " + currentGroup.toString());
                }
            }

        }
    }
}
