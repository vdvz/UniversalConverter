import com.example.UniversalConverter.*;
import com.example.UniversalConverter.Exceptions.InvalidStringForParsing;
import com.example.UniversalConverter.Exceptions.UnknownNameOfUnitException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;


public class RulesTests {
    static Rules rules = null;
    static String rulePath = "C:\\Users\\Vadim\\Desktop\\UniversalConverter\\src\\main\\resources\\conversion_rules";

    static ConversionRequestParser parser = new ConversionRequestParser();
    PreProcessingPhase preProcessingPhase = new PreProcessingPhase();
    ExpressionConverter converter = new ExpressionConverter();

    @BeforeAll
    public static void initForTest(){
        try {
            RulesManager.setPathToResourceWithRules(rulePath);
            rules = RulesManager.createRules();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("building graph tests")
    public void graphCreationTest(){

    }

    @Test
    public void checkDimension(){
    }

    @Test
    @DisplayName("test for measure group")
    public void testMeasureGroup(){
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
                new TestRequest(mapper.readValue("{\"from\" : \"ч\", \"to\" : \"мин\"}", ConversionRequest.class), new BigDecimal("60").setScale(15)),
                new TestRequest(mapper.readValue("{\"from\" : \"км*км*км/мм\", \"to\" : \"м*см\"}", ConversionRequest.class), new BigDecimal("100000000000000").setScale(15))
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
        } catch (InvalidStringForParsing | UnknownNameOfUnitException invalidStringForParsing) {
            invalidStringForParsing.printStackTrace();
        }

        Expression finalEx = null;
        assert from != null;
        assert to != null;
        finalEx = PreProcessingPhase.combine(from, to);

        assert finalEx != null;
        if(finalEx.isConversionAvailable()){
            converter.convert(finalEx);
        }

        assertEquals(answer, finalEx.getK());
    }


    @ParameterizedTest(name = "simple tests with exception")
    @MethodSource("sourcesForIncorrectTests")
    public void simpleTestsWithExceptions(){
    }

}
