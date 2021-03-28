package com.example.UniversalConverter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.*;


public class ProcessingPhase {
    private static final Logger logger = LogManager.getLogger(ProcessingPhase.class);

    private final RoundingMode roundPolitic;
    private final int maxScale;
    private final int deltaScale;

    public ProcessingPhase(){
        maxScale = 90;
        deltaScale = 15;
        roundPolitic = RoundingMode.HALF_DOWN;
    }

    public ProcessingPhase(int maxScale, int deltaScale, RoundingMode roundPolitic){
        this.maxScale = maxScale;
        this.deltaScale = deltaScale;
        this.roundPolitic = roundPolitic;
    }

    private BigDecimal divisorK = BigDecimal.ONE;
    private BigDecimal numeratorK = BigDecimal.ONE;

    public void convert(Expression e){
        BigDecimal K = e.getK();
        for (MeasureGroup gr: e.getMeasures()) {
            System.out.println(gr.toString());
            convertGroup(gr);
        }
        e.setK(numeratorK.divide(divisorK, RoundingMode.HALF_DOWN));
    }

    private void convertGroup(MeasureGroup currentGroup){

        MeasureGraph conversionGraph = currentGroup.getGraph();
        while(!currentGroup.isEmpty()){
            Queue<MultiplicationUnit> neighbors = new LinkedList<>();
            Unit fromUnit = currentGroup.getNext();

            logger.info("Получили Unit из группы: " + fromUnit.getName());
            Node fromNode = conversionGraph.findNode(fromUnit.getName());

            if(conversionGraph.isRootNode(fromNode)){
                logger.info("Текущая вершина корневая");
                currentGroup.addUnit(fromUnit);
                continue;
            }

            fromNode.getNeighbors().forEach((node, conversionRate) -> {
                    neighbors.add(new MultiplicationUnit(node, conversionRate));
            });

            while(!neighbors.isEmpty()) {
                MultiplicationUnit mlUnit = neighbors.remove();
                Node currentNode = mlUnit.getNode();
                BigDecimal currentK = mlUnit.getK();
                logger.info("Сосед: " + currentNode.getUnitName());

                //check if node exists in the currentGroup, if not push all neighbors except the neighbor fromUnit which we came
                //or if node is a rootNode which means non-dimension node(each conversionGraph has non-dimension node and it is always a root)
                //otherwise resultK*=multiplyUnit and exit fromUnit cycle
                Unit toUnit = currentGroup.getUnitByName(currentNode.getUnitName());

                if (!Objects.equals(toUnit,null)) {
                    int fromPower = fromUnit.getPower();
                    int toPower = toUnit.getPower();
                    int sign = Integer.signum(toUnit.getPower());
                    int power = sign * Math.abs(fromUnit.getPower());

                    if(fromPower < 0 && toPower > 0){
                        var sub = divisorK;
                        divisorK = numeratorK;
                        numeratorK = sub;
                        logger.info("Инвертируем итоговый КФ");


                        numeratorK = numeratorK.multiply(currentK.pow(Math.abs(fromPower)));
                        logger.info("Копим числитель " + numeratorK);
                    }
                    if(fromPower < 0 && toPower < 0){
                        divisorK = divisorK.multiply(currentK.pow(Math.abs(fromPower)));
                        logger.info("Копим знаменатель " + divisorK);
                    }
                    if(fromPower > 0 && toPower > 0){
                        numeratorK = numeratorK.multiply(currentK.pow(fromPower));
                        logger.info("Копим числитель " + numeratorK);
                    }
                    if(fromPower > 0 && toPower < 0){
                        divisorK = divisorK.multiply(currentK.pow(Math.abs(fromPower)));
                        logger.info("Копим знаменатель " + divisorK);
                    }

                    currentGroup.addUnit(toUnit, fromUnit.getPower());//т.к в группе уже есть элемент toUnit, то степени просто сложатся

                    break;
                } else {
                    logger.info("Выполняем шаг и добавляем новых соседей");
                    currentNode.getNeighbors().forEach((node, transitionK) -> {
                        neighbors.add(new MultiplicationUnit(node, mlUnit.getK().multiply(transitionK)));
                    });
                }

                if(neighbors.isEmpty() && !currentGroup.isEmpty()){
                    logger.info("Преобразование в корневую вершину и добавляем ее в группу");
                    int power = fromUnit.getPower();
                    if(power < 0){
                        divisorK = divisorK.multiply(currentK.pow(Math.abs(power)));
                        logger.info("Копим знаменатель " + divisorK);
                    }else{
                        numeratorK = numeratorK.multiply(currentK.pow(power));
                        logger.info("Копим числитель " + numeratorK);
                    }
                    currentGroup.addUnit(new Unit(currentNode.getUnitName(), power));
                    logger.info("Текущий вид группы " + currentGroup.toString());
                }
            }

        }

    }

}
