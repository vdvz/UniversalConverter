package com.example.UniversalConverter;

import com.example.UniversalConverter.Controller.ConversionController;
import com.example.UniversalConverter.Converter.ConversionRate;
import com.example.UniversalConverter.Converter.PreProcessingAndChecks;
import com.example.UniversalConverter.Converter.UniversalExpressionConverter;
import com.example.UniversalConverter.Exceptions.IncorrectDimensionException;
import com.example.UniversalConverter.Exceptions.UnknownNameOfUnitException;
import com.example.UniversalConverter.Parser.ConversionRequestParser;
import com.example.UniversalConverter.RequestRepresentation.Expression;
import com.example.UniversalConverter.RequestRepresentation.MeasureGroup;
import com.example.UniversalConverter.RequestRepresentation.Unit;
import com.example.UniversalConverter.RulesRepresentation.Graph.MeasureGraph;
import com.example.UniversalConverter.RulesRepresentation.Graph.Node;
import com.example.UniversalConverter.RulesRepresentation.Rules;
import com.example.UniversalConverter.RulesRepresentation.RulesManager;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.RoundingMode;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;


import static org.junit.jupiter.api.Assertions.*;


public class BaseTestsOnAlgorithm {
    static Rules rules = null;
    static String rulePath = "src/main/resources/conversion_rules";
    static ConversionRequestParser parser = new ConversionRequestParser();
    UniversalExpressionConverter converter;
    private static final int SCALE_FOR_TESTS = 16;
    PreProcessingAndChecks preprocessing = new PreProcessingAndChecks();

    @BeforeAll
    public static void initRules(){
        try {
            rules = RulesManager.createRules(rulePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @BeforeEach
    public void initConverter(){
        converter = new UniversalExpressionConverter();
    }

    @Test
    @DisplayName("building graph tests")
    public void graphCreationTest(){

        MeasureGraph lengthMeasuresGraph = rules.getGraph("????");

        class FinalNeighbors {
            Map<Node, ConversionRate> finalNeighbors;

            public Map<Node, ConversionRate> getFinalNeighbors() {
                return finalNeighbors;
            }

            public void setFinalNeighbors(Map<Node, ConversionRate> finalNeighbors) {
                this.finalNeighbors = finalNeighbors;
            }
        }

        FinalNeighbors effectivelyFinalNeighbors = new FinalNeighbors();
        Function<String, ConversionRate> getConversionRateToNeighborNamed = (name) -> effectivelyFinalNeighbors.getFinalNeighbors().entrySet().stream().filter((entry) -> entry.getKey().getUnitName().equals(name)).map(Map.Entry::getValue).findFirst().orElse(null);

        effectivelyFinalNeighbors.setFinalNeighbors(lengthMeasuresGraph.getNodeByName("????").getNeighbors());

        assertEquals(effectivelyFinalNeighbors.getFinalNeighbors().size(), 1);
        assertEquals(getConversionRateToNeighborNamed.apply("??"), new ConversionRate(new BigDecimal("1000"), BigDecimal.ONE));

        effectivelyFinalNeighbors.setFinalNeighbors(lengthMeasuresGraph.getNodeByName("??").getNeighbors());
        assertEquals(effectivelyFinalNeighbors.getFinalNeighbors().size(), 2);
        assertEquals(getConversionRateToNeighborNamed.apply("????"), new ConversionRate(new BigDecimal("1000"), BigDecimal.ONE).invert());
        assertEquals(getConversionRateToNeighborNamed.apply("????"), new ConversionRate(new BigDecimal("100"), BigDecimal.ONE));

        effectivelyFinalNeighbors.setFinalNeighbors(lengthMeasuresGraph.getNodeByName("????").getNeighbors());
        assertEquals(effectivelyFinalNeighbors.getFinalNeighbors().size(), 2);
        assertEquals(getConversionRateToNeighborNamed.apply("??"), new ConversionRate(new BigDecimal("100"), BigDecimal.ONE).invert());
        assertEquals(getConversionRateToNeighborNamed.apply("????"), new ConversionRate(new BigDecimal("10"), BigDecimal.ONE));

        effectivelyFinalNeighbors.setFinalNeighbors(lengthMeasuresGraph.getNodeByName("????").getNeighbors());
        assertEquals(effectivelyFinalNeighbors.getFinalNeighbors().size(), 1);
        assertEquals(getConversionRateToNeighborNamed.apply("????"), new ConversionRate(new BigDecimal("10"), BigDecimal.ONE).invert());

        MeasureGraph timeMeasuresGraph = rules.getGraph("??");

        effectivelyFinalNeighbors.setFinalNeighbors(timeMeasuresGraph.getNodeByName("??").getNeighbors());
        assertEquals(effectivelyFinalNeighbors.getFinalNeighbors().size(), 1);
        assertEquals(getConversionRateToNeighborNamed.apply("??????"), new ConversionRate(new BigDecimal("60"), BigDecimal.ONE));

        effectivelyFinalNeighbors.setFinalNeighbors(timeMeasuresGraph.getNodeByName("??????").getNeighbors());
        assertEquals(effectivelyFinalNeighbors.getFinalNeighbors().size(), 3);
        assertEquals(getConversionRateToNeighborNamed.apply("??"), new ConversionRate(new BigDecimal("60"), BigDecimal.ONE).invert());
        assertEquals(getConversionRateToNeighborNamed.apply("??"), new ConversionRate(new BigDecimal("1440"), BigDecimal.ONE).invert());
        assertEquals(getConversionRateToNeighborNamed.apply("??"), new ConversionRate(new BigDecimal("60"), BigDecimal.ONE));

        effectivelyFinalNeighbors.setFinalNeighbors(timeMeasuresGraph.getNodeByName("??").getNeighbors());
        assertEquals(effectivelyFinalNeighbors.getFinalNeighbors().size(), 1);
        assertEquals(getConversionRateToNeighborNamed.apply("??????"), new ConversionRate(new BigDecimal("60"), BigDecimal.ONE).invert());

        effectivelyFinalNeighbors.setFinalNeighbors(timeMeasuresGraph.getNodeByName("??").getNeighbors());
        assertEquals(effectivelyFinalNeighbors.getFinalNeighbors().size(), 1);
        assertEquals(getConversionRateToNeighborNamed.apply("??????"), new ConversionRate(new BigDecimal("1440"), BigDecimal.ONE));

    }

    @Test
    @DisplayName("check if parser works correct")
    public void checkParser() throws UnknownNameOfUnitException {
        String expression1 = "????*????";
        String expression2 = "????/????";
        String expression3 = "????*????/????";
        String expression4 = "????/????*????";

        MeasureGraph graph = rules.getGraph("????");

        Expression expression = parser.parseStringToExpression(expression1, rules);
        assertEquals(expression.getMeasures().size(), 1);
        var measures = expression.getMeasures();
        MeasureGroup group = measures.get(measures.indexOf(new MeasureGroup(graph)));
        Unit unit;
        unit = group.getUnitByName("????");
        assertNotNull(unit);
        assertEquals(unit.getPower(), 1);
        unit = group.getUnitByName("????");
        assertNotNull(unit);
        assertEquals(unit.getPower(), 1);

        expression = parser.parseStringToExpression(expression2, rules);
        assertEquals(expression.getMeasures().size(), 1);
        measures = expression.getMeasures();
        group = measures.get(measures.indexOf(new MeasureGroup(graph)));
        unit = group.getUnitByName("????");
        assertNotNull(unit);
        assertEquals(unit.getPower(), 1);
        unit = group.getUnitByName("????");
        assertNotNull(unit);
        assertEquals(unit.getPower(), -1);

        expression = parser.parseStringToExpression(expression3, rules);
        assertEquals(expression.getMeasures().size(), 1);
        measures = expression.getMeasures();
        group = measures.get(measures.indexOf(new MeasureGroup(graph)));
        unit = group.getUnitByName("????");
        assertNotNull(unit);
        assertEquals(unit.getPower(), 1);
        unit = group.getUnitByName("????");
        assertNotNull(unit);
        assertEquals(unit.getPower(), 1);
        unit = group.getUnitByName("????");
        assertNotNull(unit);
        assertEquals(unit.getPower(), -1);

        expression = parser.parseStringToExpression(expression4, rules);
        assertEquals(expression.getMeasures().size(), 1);
        measures = expression.getMeasures();
        group = measures.get(measures.indexOf(new MeasureGroup(graph)));
        unit = group.getUnitByName("????");
        assertNotNull(unit);
        assertEquals(unit.getPower(), -1);
        unit = group.getUnitByName("????");
        assertNotNull(unit);
        assertEquals(unit.getPower(), -1);
        unit = group.getUnitByName("????");
        assertNotNull(unit);
        assertEquals(unit.getPower(), 1);

    }

    @Test
    @DisplayName("test for measure group")
    public void testMeasureGroup(){
    }

    private BigDecimal algorithm(String fromStr, String toStr)
        throws UnknownNameOfUnitException, IncorrectDimensionException {

        Expression from = parser.parseStringToExpression(fromStr, rules);
        Expression to = parser.parseStringToExpression(toStr, rules);

        preprocessing.preprocessing(from, to);

        return converter.convert(from, to);
    }

    static class TestRequest{
        final ConversionRequest request;
        final BigDecimal answer;

        TestRequest(ConversionRequest request, BigDecimal answer){
            this.request = request;
            this.answer = answer;
        }
    }

    static class ConversionRequest{
        @JsonProperty("from")
        private String from;

        @JsonProperty("to")
        private String to;

        public String getFrom() {
            return from;
        }

        public String getTo() {
            return to;
        }
    }

    public static Stream<TestRequest> sourcesForCorrectTests() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return Stream.of(
                /*simple tests length*/
                new TestRequest(mapper.readValue("{\"from\" : \"????\", \"to\" : \"??\"}", ConversionRequest.class), new BigDecimal("1000")),
                new TestRequest(mapper.readValue("{\"from\" : \"1/??\", \"to\" : \"1/????\"}", ConversionRequest.class), new BigDecimal("1000")),
                new TestRequest(mapper.readValue("{\"from\" : \"1/????\", \"to\" : \"1/??\"}", ConversionRequest.class), new BigDecimal("0.001")),
                new TestRequest(mapper.readValue("{\"from\" : \"??\", \"to\" : \"????\"}", ConversionRequest.class), new BigDecimal("0.001")),
                /*simple tests time(different view of graph)*/
                new TestRequest(mapper.readValue("{\"from\" : \"??\", \"to\" : \"??\"}", ConversionRequest.class), new BigDecimal("24")),
                new TestRequest(mapper.readValue("{\"from\" : \"??\", \"to\" : \"??\"}", ConversionRequest.class), new BigDecimal("60").divide(new BigDecimal("1440"), SCALE_FOR_TESTS, RoundingMode.HALF_DOWN)),
                /*simple tests weight(different view of graph)*/
                new TestRequest(mapper.readValue("{\"from\" : \"????\", \"to\" : \"????\"}", ConversionRequest.class), new BigDecimal("1000")),
                new TestRequest(mapper.readValue("{\"from\" : \"1/????\", \"to\" : \"1/????\"}", ConversionRequest.class), new BigDecimal("0.001")),
                /*reduce left, right and convert*/
                new TestRequest(mapper.readValue("{\"from\" : \"??/????*????\", \"to\" : \"1/????\"}", ConversionRequest.class), new BigDecimal("0.00000001")),
                new TestRequest(mapper.readValue("{\"from\" : \"1/????\", \"to\" : \"??/????*????\"}", ConversionRequest.class), new BigDecimal("100000000")),
                /*reduce left and right and convert*/
                new TestRequest(mapper.readValue("{\"from\" : \"????*????*????/??\", \"to\" : \"????*????\"}", ConversionRequest.class), new BigDecimal("100000000000000")),
                new TestRequest(mapper.readValue("{\"from\" : \"????/????*????*????\", \"to\" : \"1/??*????\"}", ConversionRequest.class), new BigDecimal("0.00000000000001")),
                /*multi-measure test*/
                new TestRequest(mapper.readValue("{\"from\" : \"????*????*????/????*????\", \"to\" : \"??*????/????\"}", ConversionRequest.class), new BigDecimal("100000000000")),
                /*one of measure doesn't exists at one of side*/
                new TestRequest(mapper.readValue("{\"from\" : \"??\", \"to\" : \"????*??/??\"}", ConversionRequest.class), new BigDecimal("3.6")),
                new TestRequest(mapper.readValue("{\"from\" : \"????*??/??\", \"to\" : \"??\"}", ConversionRequest.class), new BigDecimal("10").divide(new BigDecimal("36"), SCALE_FOR_TESTS, RoundingMode.HALF_DOWN)),
                /*absurd*/
                new TestRequest(mapper.readValue("{\"from\" : \"????/??\", \"to\" : \"??/??\"}", ConversionRequest.class), new BigDecimal("10").divide(new BigDecimal("36"), SCALE_FOR_TESTS, RoundingMode.HALF_DOWN))


        );
    }


    public static Stream<TestRequest> sourcesForCorrectTestsWithDimensionless() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return Stream.of(
            new TestRequest(mapper.readValue("{\"from\" : \"\", \"to\" : \"\"}", ConversionRequest.class), new BigDecimal("1")),
            new TestRequest(mapper.readValue("{\"from\" : \"????/??\", \"to\" : \"\"}", ConversionRequest.class), new BigDecimal("1000")),
            new TestRequest(mapper.readValue("{\"from\" : \"\", \"to\" : \"????/??\"}", ConversionRequest.class), new BigDecimal("0.001")),
            new TestRequest(mapper.readValue("{\"from\" : \"??**????\", \"to\" : \"????*????/\"}", ConversionRequest.class), new BigDecimal("1000000000")),
            new TestRequest(mapper.readValue("{\"from\" : \"/????*????\", \"to\" : \"1/*??*????*\"}", ConversionRequest.class), new BigDecimal("1000000000")),
            new TestRequest(mapper.readValue("{\"from\" : \"/??*????\", \"to\" : \"/????*????\"}", ConversionRequest.class), new BigDecimal("0.000000001")),
            new TestRequest(mapper.readValue("{\"from\" : \"????*????/\", \"to\" : \"??*????*/\"}", ConversionRequest.class), new BigDecimal("0.000000001"))
        );
    }

    public static Stream<ConversionRequest> sourcesForIncorrectDimensionTests()
        throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return Stream.of(
            mapper.readValue("{\"from\" : \"1/????\", \"to\" : \"??\"}", ConversionRequest.class),
            mapper.readValue("{\"from\" : \"????*????\", \"to\" : \"??\"}", ConversionRequest.class),
            mapper.readValue("{\"from\" : \"????\", \"to\" : \"??*??\"}", ConversionRequest.class),
            mapper.readValue("{\"from\" : \"????*??/??\", \"to\" : \"??*??*??/??\"}", ConversionRequest.class),
            mapper.readValue("{\"from\" : \"??\", \"to\" : \"????*??*??/??\"}", ConversionRequest.class)
        );
    }

    private static Stream<ConversionRequest> sourcesForIncorrectNameOfUnitTests()
        throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return Stream.of(
            mapper.readValue("{\"from\" : \"1/????\", \"to\" : \"sadas dsadsa * dsfds\"}", ConversionRequest.class),
            mapper.readValue("{\"from\" : \"asdfsa\", \"to\" : \"??\"}", ConversionRequest.class),
            mapper.readValue("{\"from\" : \"1/sadsads\", \"to\" : \"\"}", ConversionRequest.class),
            mapper.readValue("{\"from\" : \"????*??/??cccc\", \"to\" : \"??cccc/1\"}", ConversionRequest.class)
        );
    }

    @DisplayName("simple tests with right answer")
    @ParameterizedTest
    @MethodSource("sourcesForCorrectTests")
    public void simpleRightTests(TestRequest testRequest){
        ConversionRequest request = testRequest.request;
        BigDecimal answer = testRequest.answer;

        var ref = new Object() {
            String currentAnswer;
        };

        assertDoesNotThrow(() -> ref.currentAnswer = algorithm(request.getFrom(), request.getTo()).stripTrailingZeros().toPlainString());
        assertEquals(answer.toPlainString(), ref.currentAnswer);
    }


    @DisplayName("simple tests with dimensionless measure")
    @ParameterizedTest
    @MethodSource("sourcesForCorrectTestsWithDimensionless")
    public void simpleRightTestsWithDimensionless(TestRequest testRequest){
        ConversionRequest request = testRequest.request;
        BigDecimal answer = testRequest.answer;

        var ref = new Object() {
            String currentAnswer;
        };

        assertDoesNotThrow(() -> ref.currentAnswer = algorithm(request.getFrom(), request.getTo()).stripTrailingZeros().toPlainString());
        assertEquals(answer.toPlainString(), ref.currentAnswer);
    }

    @DisplayName("simple tests with thrown IncorrectDimensionException")
    @ParameterizedTest
    @MethodSource("sourcesForIncorrectDimensionTests")
    public void testWithIncorrectDimension(ConversionRequest request){
        assertThrows(IncorrectDimensionException.class, () -> algorithm(request.getFrom(), request.getTo()));
    }

    @DisplayName("simple tests with thrown UnknownNameOfUnitException")
    @ParameterizedTest
    @MethodSource("sourcesForIncorrectNameOfUnitTests")
    public void testWithIncorrectNameOfUnit(ConversionRequest request){
        assertThrows(UnknownNameOfUnitException.class, () -> algorithm(request.getFrom(), request.getTo()));
    }

}
