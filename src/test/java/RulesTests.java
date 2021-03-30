import com.example.UniversalConverter.*;
import com.example.UniversalConverter.Exceptions.IncorrectDimensionException;
import com.example.UniversalConverter.Exceptions.InvalidStringForParsing;
import com.example.UniversalConverter.Exceptions.UnknownNameOfUnitException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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


public class RulesTests {
    static Rules rules = null;
    static String rulePath = "C:\\Users\\Vadim\\Desktop\\UniversalConverter\\src\\main\\resources\\conversion_rules";

    static ConversionRequestParser parser = new ConversionRequestParser();
    ExpressionConverter converter;

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
        converter = new ExpressionConverter();
    }

    @Test
    @DisplayName("building graph tests")
    public void graphCreationTest(){

        MeasureGraph lengthMeasuresGraph = rules.getGraph("км");

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

        effectivelyFinalNeighbors.setFinalNeighbors(lengthMeasuresGraph.getNodeByName("км").getNeighbors());
        Function<String, ConversionRate> getConversionRateToNeighborNamed = (name) -> effectivelyFinalNeighbors.getFinalNeighbors().entrySet().stream().filter((entry) -> entry.getKey().getUnitName().equals(name)).map(Map.Entry::getValue).findFirst().orElse(null);

        assertEquals(effectivelyFinalNeighbors.getFinalNeighbors().size(), 1);
        assertEquals(getConversionRateToNeighborNamed.apply("м"), new ConversionRate(new BigDecimal("1000"), BigDecimal.ONE));

        effectivelyFinalNeighbors.setFinalNeighbors(lengthMeasuresGraph.getNodeByName("м").getNeighbors());
        assertEquals(effectivelyFinalNeighbors.getFinalNeighbors().size(), 2);
        assertEquals(getConversionRateToNeighborNamed.apply("км"), new ConversionRate(new BigDecimal("1000"), BigDecimal.ONE).invert());
        assertEquals(getConversionRateToNeighborNamed.apply("см"), new ConversionRate(new BigDecimal("100"), BigDecimal.ONE));

        effectivelyFinalNeighbors.setFinalNeighbors(lengthMeasuresGraph.getNodeByName("см").getNeighbors());
        assertEquals(effectivelyFinalNeighbors.getFinalNeighbors().size(), 2);
        assertEquals(getConversionRateToNeighborNamed.apply("м"), new ConversionRate(new BigDecimal("100"), BigDecimal.ONE).invert());
        assertEquals(getConversionRateToNeighborNamed.apply("мм"), new ConversionRate(new BigDecimal("10"), BigDecimal.ONE));

        effectivelyFinalNeighbors.setFinalNeighbors(lengthMeasuresGraph.getNodeByName("мм").getNeighbors());
        assertEquals(effectivelyFinalNeighbors.getFinalNeighbors().size(), 1);
        assertEquals(getConversionRateToNeighborNamed.apply("см"), new ConversionRate(new BigDecimal("10"), BigDecimal.ONE).invert());


    }

    @Test
    @DisplayName("check if parser works correct")
    public void checkParser() throws InvalidStringForParsing, UnknownNameOfUnitException {
        String expression1 = "км*см";
        String expression2 = "км/см";
        String expression3 = "км*см/мм";
        String expression4 = "мм/км*см";

        MeasureGraph graph = rules.getGraph("км");

        Expression expression = parser.parseStringToExpression(expression1, rules);
        assertEquals(expression.getMeasures().size(), 1);
        var measures = expression.getMeasures();
        MeasureGroup group = measures.get(measures.indexOf(new MeasureGroup(graph)));
        Unit unit;
        unit = group.getUnitByName("км");
        assertNotNull(unit);
        assertEquals(unit.getPower(), 1);
        unit = group.getUnitByName("см");
        assertNotNull(unit);
        assertEquals(unit.getPower(), 1);

        expression = parser.parseStringToExpression(expression2, rules);
        assertEquals(expression.getMeasures().size(), 1);
        measures = expression.getMeasures();
        group = measures.get(measures.indexOf(new MeasureGroup(graph)));
        unit = group.getUnitByName("км");
        assertNotNull(unit);
        assertEquals(unit.getPower(), 1);
        unit = group.getUnitByName("см");
        assertNotNull(unit);
        assertEquals(unit.getPower(), -1);

        expression = parser.parseStringToExpression(expression3, rules);
        assertEquals(expression.getMeasures().size(), 1);
        measures = expression.getMeasures();
        group = measures.get(measures.indexOf(new MeasureGroup(graph)));
        unit = group.getUnitByName("км");
        assertNotNull(unit);
        assertEquals(unit.getPower(), 1);
        unit = group.getUnitByName("см");
        assertNotNull(unit);
        assertEquals(unit.getPower(), 1);
        unit = group.getUnitByName("мм");
        assertNotNull(unit);
        assertEquals(unit.getPower(), -1);

        expression = parser.parseStringToExpression(expression4, rules);
        assertEquals(expression.getMeasures().size(), 1);
        measures = expression.getMeasures();
        group = measures.get(measures.indexOf(new MeasureGroup(graph)));
        unit = group.getUnitByName("км");
        assertNotNull(unit);
        assertEquals(unit.getPower(), -1);
        unit = group.getUnitByName("см");
        assertNotNull(unit);
        assertEquals(unit.getPower(), -1);
        unit = group.getUnitByName("мм");
        assertNotNull(unit);
        assertEquals(unit.getPower(), 1);

    }

    @Test
    @DisplayName("test for measure group")
    public void testMeasureGroup(){
    }

    private BigDecimal algorithm(String fromStr, String toStr)
        throws InvalidStringForParsing, UnknownNameOfUnitException, IncorrectDimensionException {

        Expression from = parser.parseStringToExpression(fromStr, rules);
        Expression to = parser.parseStringToExpression(toStr, rules);

        PreProcessingPhase.preprocessing(from, to);

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

    public static Stream<TestRequest> sourcesForCorrectTests() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return Stream.of(
                new TestRequest(mapper.readValue("{\"from\" : \"км\", \"to\" : \"м\"}", ConversionRequest.class), new BigDecimal("1000").setScale(15)),
                new TestRequest(mapper.readValue("{\"from\" : \"1/м\", \"to\" : \"1/км\"}", ConversionRequest.class), new BigDecimal("1000").setScale(15)),
                new TestRequest(mapper.readValue("{\"from\" : \"1/км\", \"to\" : \"1/м\"}", ConversionRequest.class), new BigDecimal("0.001").setScale(15)),
                new TestRequest(mapper.readValue("{\"from\" : \"м\", \"to\" : \"км\"}", ConversionRequest.class), new BigDecimal("0.001").setScale(15)),
                new TestRequest(mapper.readValue("{\"from\" : \"д\", \"to\" : \"ч\"}", ConversionRequest.class), new BigDecimal("24").setScale(15)),
                new TestRequest(mapper.readValue("{\"from\" : \"ч\", \"to\" : \"д\"}", ConversionRequest.class), new BigDecimal("0.041666666666667").setScale(15)),
                new TestRequest(mapper.readValue("{\"from\" : \"м/км*км\", \"to\" : \"1/см\"}", ConversionRequest.class), new BigDecimal("0.00000001").setScale(15)),
                new TestRequest(mapper.readValue("{\"from\" : \"км*км*км/м\", \"to\" : \"мм*см\"}", ConversionRequest.class), new BigDecimal("100000000000000").setScale(15)),
                new TestRequest(mapper.readValue("{\"from\" : \"мм/км*км*км\", \"to\" : \"1/м*см\"}", ConversionRequest.class), new BigDecimal("0.00000000000001").setScale(15)),
                new TestRequest(mapper.readValue("{\"from\" : \"гр\", \"to\" : \"мг\"}", ConversionRequest.class), new BigDecimal("1000").setScale(15)),
                new TestRequest(mapper.readValue("{\"from\" : \"1/гр\", \"to\" : \"1/мг\"}", ConversionRequest.class), new BigDecimal("0.001").setScale(15)),
                new TestRequest(mapper.readValue("{\"from\" : \"кг/гр\", \"to\" : \"\"}", ConversionRequest.class), new BigDecimal("0.001").setScale(15))

        );
    }

    public static Stream<ConversionRequest> sourcesForIncorrectDimensionTests()
        throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return Stream.of(
            mapper.readValue("{\"from\" : \"1/км\", \"to\" : \"м\"}", ConversionRequest.class),
            mapper.readValue("{\"from\" : \"км*км\", \"to\" : \"м\"}", ConversionRequest.class),
            mapper.readValue("{\"from\" : \"км\", \"to\" : \"м*м\"}", ConversionRequest.class),
            mapper.readValue("{\"from\" : \"км*ч/с\", \"to\" : \"м*ч*с/с\"}", ConversionRequest.class)
        );
    }

    private static Stream<ConversionRequest> sourcesForIncorrectNameOfUnitTests()
        throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return Stream.of(
            mapper.readValue("{\"from\" : \"1/км\", \"to\" : \"sadas dsadsa * dsfds\"}", ConversionRequest.class),
            mapper.readValue("{\"from\" : \"asdfsa\", \"to\" : \"м\"}", ConversionRequest.class),
            mapper.readValue("{\"from\" : \"1/sadsads\", \"to\" : \"\"}", ConversionRequest.class),
            mapper.readValue("{\"from\" : \"км*ч/сcccc\", \"to\" : \"сcccc/1\"}", ConversionRequest.class)
        );
    }

    @DisplayName("simple tests with right answer")
    @ParameterizedTest
    @MethodSource("sourcesForCorrectTests")
    public void simpleRightTests(TestRequest testRequest){
        ConversionRequest request = testRequest.request;
        BigDecimal answer = testRequest.answer;

        var ref = new Object() {
            BigDecimal currentAnswer;
        };

        assertDoesNotThrow(() -> ref.currentAnswer = algorithm(request.getFrom(), request.getTo()));
        assertEquals(answer, ref.currentAnswer);
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
