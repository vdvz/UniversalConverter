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
        MeasureGraph graph = group.getGraph();

        while(group.size() > 1){
            Unit currentUnit = group.poll();
            Queue<MultiplicationUnit> queue = new LinkedList<>();
            graph.getNeighbors(currentUnit).forEach((node, bigDecimal) -> {
                queue.add(new MultiplicationUnit(node, bigDecimal));
            });

            while(!queue.isEmpty()) {
                MultiplicationUnit mlUnit = queue.remove();
                Node el = mlUnit.getNode();

                //check if node exists in the group, if not push all neighbors except the neighbor from which we came
                //otherwise resultK*=multiplyUnit and exit from cycle
                if (group.contains(el.getUnit())) {
                    int power = mlUnit.getNode().getUnit().getPower();
                    if(power < 0){
                        BigDecimal k = mlUnit.getK();
                        int kScale = resultK.scale();
                        if( kScale >= maxScale){
                            resultK = resultK.divide(k, roundPolitic);
                        } else {
                            resultK = resultK.divide(k, kScale + deltaScale, roundPolitic);
                        }
                    }else{
                        resultK = resultK.multiply(mlUnit.getK());
                    }
                    break;
                } else {
                    //make step
                    graph.getNeighbors(mlUnit.getNode().getUnit()).forEach((node, bigDecimal) -> {
                        if(!node.equals(mlUnit.getNode())) {
                            queue.add(new MultiplicationUnit(node, mlUnit.getK().multiply(bigDecimal)));
                        }
                    });
                }
            }
        }
        return resultK;
    }

}
