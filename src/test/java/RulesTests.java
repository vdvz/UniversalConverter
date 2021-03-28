import com.example.UniversalConverter.*;
import com.example.UniversalConverter.Exceptions.IncorrectDimensionException;
import com.example.UniversalConverter.Exceptions.InvalidStringForParsing;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;


public class RulesTests {
    static Rules rules = null;
    static String rulePath = "C:\\Users\\Vadim\\Desktop\\UniversalConverter\\src\\main\\resources\\conversion_rules";

    static ConversionRequestParser parser = new ConversionRequestParser();
    PreProcessingPhase preProcessingPhase = new PreProcessingPhase();
    ProcessingPhase processingPhase = new ProcessingPhase();

    @BeforeAll
    public static void initForTest(){
        try {
            rules = RulesCreator.createRules(rulePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("building graph tests")
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


        String req = "км/м";
        String req1 = "см";
        String req2 = "ч";
        ConversionRequestParser parser = new ConversionRequestParser();
        Expression expression = parser.parseStringToExpression(req, rules);
        Expression expression1 = parser.parseStringToExpression(req1, rules);
        Expression expression2 = parser.parseStringToExpression(req2, rules);

        PreProcessingPhase preProcessing = new PreProcessingPhase();



        System.out.println("Measures");
        List<MeasureGroup> measures = expression.getMeasures();
        measures.forEach(System.out::println);


        assertTrue(measures.contains(new MeasureGroup(rules.getGraph("см"))));
        //assertFalse(measures.contains(new MeasureGroup(rules.getGraph(new Unit("ч")))));

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

    }

    @Test
    @DisplayName("test for measure group")
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

    /*For test
    String from = "м";
    String to = "км*с/ч";
    */
    static class TestRequest{
        final ConversionRequest request;
        final BigDecimal answer;

        TestRequest(ConversionRequest request, BigDecimal answer){
            this.request = request;
            this.answer = answer;
        }
    }

    public static Stream<TestRequest> sourcesForCorrectTests() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return Stream.of(
                new TestRequest(mapper.readValue("{\"from\" : \"км\", \"to\" : \"м\"}", ConversionRequest.class), new BigDecimal("1000")),
                new TestRequest(mapper.readValue("{\"from\" : \"1/м\", \"to\" : \"1/км\"}", ConversionRequest.class), new BigDecimal("1000")),
                new TestRequest(mapper.readValue("{\"from\" : \"1/км\", \"to\" : \"1/м\"}", ConversionRequest.class), new BigDecimal("0.001")),
                new TestRequest(mapper.readValue("{\"from\" : \"м\", \"to\" : \"км\"}", ConversionRequest.class), new BigDecimal("0.001")),
                new TestRequest(mapper.readValue("{\"from\" : \"ч\", \"to\" : \"мин\"}", ConversionRequest.class), new BigDecimal("60"))
        );
    }

    public static Stream<ConversionRequest> sourcesForIncorrectTests(){
        ObjectMapper mapper = new ObjectMapper();
        return Stream.of(

        );
    }

    @ParameterizedTest(name = "simple tests with right answer")
    @MethodSource("sourcesForCorrectTests")
    public void simpleRightTests(TestRequest testRequest){
        ConversionRequest request = testRequest.request;
        BigDecimal answer = testRequest.answer;
        Expression from = null;
        Expression to = null;
        try {
            from = parser.parseStringToExpression(request.getFrom(), rules);
            to = parser.parseStringToExpression(request.getTo(), rules);
        } catch (InvalidStringForParsing invalidStringForParsing) {
            invalidStringForParsing.printStackTrace();
        }

        Expression finalEx = null;
        assert from != null;
        assert to != null;
        finalEx = PreProcessingPhase.combine(from, to);

        assert finalEx != null;
        if(finalEx.isConversionAvailable()){
            processingPhase.convert(finalEx);
        }

        assertEquals(answer, finalEx.getK());
    }


    @ParameterizedTest(name = "simple tests with exception")
    @MethodSource("sourcesForIncorrectTests")
    public void simpleTestsWithExceptions(){
    }

}
