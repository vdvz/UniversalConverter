import com.example.UniversalConverter.*;
import com.example.UniversalConverter.Exceptions.IncorrectDimensionException;
import com.example.UniversalConverter.Exceptions.InvalidStringForParsing;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
    public void graphCreationTest() throws InvalidStringForParsing {
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
        assertTrue(rules.isKnownNode(new Node("м")));
        assertFalse(rules.isKnownNode(new Node("дм")));


        String req = "км/м";
        String req1 = "см";
        String req2 = "ч";
        ConversionRequestParser parser = new ConversionRequestParser();
        Expression expression = parser.parseStringToExpression(req, rules);
        Expression expression1 = parser.parseStringToExpression(req1, rules);
        Expression expression2 = parser.parseStringToExpression(req2, rules);

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


        assertTrue(measures.contains(new MeasureGroup(rules.getGraph("см"))));
        //assertFalse(measures.contains(new MeasureGroup(rules.getGraph(new Unit("ч")))));

    }


    Rules rules = null;
    String path = "C:\\Users\\Vadim\\Desktop\\UniversalConverter\\src\\main\\resources\\conversion_rules";

    ConversionRequestParser parser;
    PreProcessingPhase preProcessingPhase = new PreProcessingPhase();
    ProcessingPhase processingPhase = new ProcessingPhase();
    @Before
    public void initForTest(){
        try {
            rules = RulesCreator.createRules(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        parser = new ConversionRequestParser();
    }

    @Test
    public void checkRules(){
        System.out.println(rules);
    }

    @Test
    public void checkDimension(){
        String from = "км / м";
        String to = " / мм";

        Expression expression = null;
        try {
            expression = parser.parseStringToExpression(from, rules);
        } catch (InvalidStringForParsing invalidStringForParsing) {
            invalidStringForParsing.printStackTrace();
        }
        Expression expression1 = null;
        try {
            expression1 = parser.parseStringToExpression(to, rules);
        } catch (InvalidStringForParsing invalidStringForParsing) {
            invalidStringForParsing.printStackTrace();
        }


        System.out.println(expression.toString());
        System.out.println(expression1.toString());
        System.out.println(expression.isConversionAvailable(expression1));
    }

    @Test
    public void process(){
        String from = "км / м";
        String to = " ";

        Expression expression = null;
        try {
            expression = parser.parseStringToExpression(from, rules);
        } catch (InvalidStringForParsing invalidStringForParsing) {
            invalidStringForParsing.printStackTrace();
        }
        Expression expression1 = null;
        try {
            expression1 = parser.parseStringToExpression(to, rules);
        } catch (InvalidStringForParsing invalidStringForParsing) {
            invalidStringForParsing.printStackTrace();
        }

        Expression ex = null;
        try {
            ex = preProcessingPhase.combine(expression, expression1);
        } catch (IncorrectDimensionException e) {
            e.printStackTrace();
        }

        assert ex != null;


        System.out.println(rules);
        System.out.println("Get nodes");
        System.out.println("Known units " + rules.getKnownUnits());
        rules.getKnownUnits().stream().map(MeasureGraph::getNodes).collect(Collectors.toSet()).forEach(e -> e.forEach(el -> {
                System.out.println("Name " + el.getUnitName() + " count neighbors " + el.getNeighbors().size());
                el.getNeighbors().forEach((ee, aa) -> System.out.println(ee.getUnitName()));
            }
        ));

        System.out.println("Start convert");
        processingPhase.convert(ex);

        System.out.println(ex.getK());
    }

    @Test
    public void testMeasureGroup(){
        MeasureGraph graph = new MeasureGraph(new Node("test"));
        MeasureGroup group = new MeasureGroup(graph);
        MeasureGroup group1 = new MeasureGroup(graph);

        Assert.assertEquals(group1, group);

        Unit unit = new Unit("testUnit");
        group.addUnit(unit, 2);
        assertTrue(group.contains(unit));

        Unit unit1 = new Unit("testUnit");
        group.addUnit(unit1, -3);

        assertEquals(group.getUnitByName("testUnit").getPower(), -1);

        Unit unit2 = new Unit("testUnit");
        group.addUnit(unit1, 1);

        assertTrue(group.isEmpty());

    }


}
