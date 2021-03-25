import com.example.UniversalConverter.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class RulesTests {

    @Test
    public void some(){
        String a = "sdsadsa";
        System.out.println((int) Arrays.stream(a.split("\\\\")).count());
        //.forEach(System.out::println);

    }


    @Before
    public void initGraph(){

    }

    @Test
    public void graphTest(){

    }

    @Test
    public void parsingTest(){
        //Expression e = ConversionRequestParser.parseStringToExpression("см*мм*км/кг");

    }

    @Test
    public void bigDec(){
        BigDecimal bd = new BigDecimal(60);
        System.out.println(BigDecimal.ONE.divide(bd, RoundingMode.HALF_DOWN));
        System.out.println(BigDecimal.ONE.divide(bd, 15, RoundingMode.HALF_UP));

    }

    @Test
    public void graphCreationTest(){
        String path = "C:\\Users\\Vadim\\Desktop\\UniversalConverter\\src\\main\\resources\\conversion_rules";
        Rules rules = null;
        try {
            rules = RulesCreator.createRules(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert rules != null;
        System.out.println("Graphs");
        rules.getKnownUnits().stream().distinct().forEach(System.out::println);
        assertTrue(rules.isKnownUnit(new Unit("м")));
        assertFalse(rules.isKnownUnit(new Unit("дм")));


        String req = "км/м";
        String req1 = "см";
        String req2 = "ч";
        Expression expression = ConversionRequestParser.parseStringToExpression(req, rules);
        Expression expression1 = ConversionRequestParser.parseStringToExpression(req1, rules);
        Expression expression2 = ConversionRequestParser.parseStringToExpression(req2, rules);

        PreProcessingPhase preProcessing = new PreProcessingPhase();

        Expression expr;
        try {
            expr = preProcessing.step(expression, expression1);
            expr = preProcessing.step(expression, expression2);
        } catch (IncorrectDimensionException e) {
            e.printStackTrace();
        }


        System.out.println("Measures");
        List<MeasureGroup> measures = expression.getMeasures();
        measures.forEach(System.out::println);


        assertTrue(measures.contains(new MeasureGroup(rules.getGraph(new Unit("см")))));
        //assertFalse(measures.contains(new MeasureGroup(rules.getGraph(new Unit("ч")))));

    }



}
