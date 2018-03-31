package priv.jj.calculator;


import org.junit.Test;
import priv.jj.calculator.model.Expression;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ExpressionTest {
    @Test
    public void testConstructor() {
        // + - * / should be taken as operator
        for (String s : new String[]{"+", "-", "*", "/"})
            assertTrue("type should be operator, but is not", new Expression(s).isOperator());

        // ( ) should be taken as brackets
        assertTrue("type should be left bracket, but is not", new Expression("(").isLeftBracket());
        assertTrue("type should be right bracket, but is not", new Expression(")").isRightBracket());

        // some correct number forms
        for (String n : new String[]{"123", "12.12", "123.123", "0120.120"})
            assertTrue("type should be number, but is not", new Expression(n).isNumber());

        // some incorrect number forms
        for (String n : new String[]{".123", "123.", "12+3", "342-", "12.12.12", "(1212"}) {
            boolean exception;
            try {
                exception = false;
                new Expression(n);
            } catch (IllegalArgumentException e) {
                exception = true;
                assertEquals("it throws wrong type of exception", "Expression class: value is an illegal number", e.getMessage());
            }
            assertTrue(n + " should throw exception, but didn't happen", exception);
        }
    }

    @Test
    public void turnNegative() {
        String [] tests = new String[] {"123", "-123", "(", "-("};
        String [] anss = new String[] {"-123", "123", "-(", "("};
        for (int i = 0; i < tests.length; i++) {
            Expression e = new Expression(tests[i]);
            e.turnNegative();
            assertEquals("should turn negative correctly, but did not", anss[i], e.getValue());
        }

    }
}