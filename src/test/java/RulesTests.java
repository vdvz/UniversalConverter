import com.example.UniversalConverter.Expression;
import com.example.UniversalConverter.ConversionRequestParser;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;


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
        Expression e = ConversionRequestParser.parseStringToExpression("см*мм*км/кг");

    }

    @Test
    public void bigDec(){
        BigDecimal bd = new BigDecimal(60);
        System.out.println(BigDecimal.ONE.divide(bd, 15, RoundingMode.HALF_UP));

    }

    @Test
    public void tt(){
    }


}
