package com.example.UniversalConverter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedList;
import java.util.Queue;


public class ProcessingPhase {

    private final RoundingMode roundPolitic;
    private final int maxScale;
    private final int deltaScale;

    ProcessingPhase(){
        maxScale = 90;
        deltaScale = 15;
        roundPolitic = RoundingMode.HALF_DOWN;
    }

    ProcessingPhase(int maxScale, int deltaScale, RoundingMode roundPolitic){
        this.maxScale = maxScale;
        this.deltaScale = deltaScale;
        this.roundPolitic = roundPolitic;
    }

    void convert(Expression e){
        BigDecimal K = e.getK();
        for (MeasureGroup gr: e.getMeasures()) {
            K = K.multiply(convertGroup(gr));
        }
        e.setK(K);
    }

    BigDecimal convertGroup(MeasureGroup group){
        BigDecimal resultK = BigDecimal.ONE;
        MeasureGraph conversionGraph = group.getGraph();

        while(!group.isEmpty()){
            Queue<MultiplicationUnit> neighbors = new LinkedList<>();
            Unit from = group.getNext();
            conversionGraph.findNode(from.getName()).getNeighbors().forEach((node, bigDecimal) -> {
                    neighbors.add(new MultiplicationUnit(node, bigDecimal));
            });

            while(!neighbors.isEmpty()) {
                MultiplicationUnit mlUnit = neighbors.remove();
                Node currentNode = mlUnit.getNode();

                //check if node exists in the group, if not push all neighbors except the neighbor from which we came
                //or if node is a rootNode which means non-dimension node(each conversionGraph has non-dimension node and it is always a root)
                //otherwise resultK*=multiplyUnit and exit from cycle
                Unit to = group.getUnitByName(currentNode.getUnitName());
                if (group.isValidConversion(from, to) || conversionGraph.isRootNode(currentNode)) {
                    int power = to.getPower();
                    BigDecimal currentK = mlUnit.getK();
                    if(power < 0){
                        //a^(-i) <=> 1/(a^i)
                        int kScale = Math.max(currentK.scale(), resultK.scale());
                        if( kScale >= maxScale && kScale!=0){//if not 1/6 == 0, and the scale is 0, but we expect 0.6..67
                            resultK = resultK.divide(currentK.pow(Math.abs(power)), roundPolitic);
                        } else {
                            resultK = resultK.divide(currentK.pow(Math.abs(power)), kScale + deltaScale, roundPolitic);
                        }
                    }else{
                        resultK = resultK.multiply(currentK.pow(power));
                    }
                    break;
                } else {
                    //make step
                    currentNode.getNeighbors().forEach((node, transitionK) -> {
                        neighbors.add(new MultiplicationUnit(node, mlUnit.getK().multiply(transitionK)));
                    });
                }
            }
        }
        return resultK;
    }

}
