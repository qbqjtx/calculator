package test;

import model.Expression;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExpressionTest {
    @Test
    public void testConstructor() {
        // + - * / should be taken as operator
        for (String s : new String[]{"+", "-", "*", "/"})
            assertTrue(new Expression(s).isOperator(), "type should be operator, but is not");

        // ( ) should be taken as brackets
        assertTrue(new Expression("(").isLeftBracket(), "type should be left bracket, but is not");
        assertTrue(new Expression(")").isRightBracket(), "type should be right bracket, but is not");

        // some correct number forms
        for (String n : new String[]{"123", "12.12", "123.123", "0120.120"})
            assertTrue(new Expression(n).isNumber(), "type should be number, but is not");

        // some incorrect number forms
        for (String n : new String[]{".123", "123.", "12+3", "342-", "12.12.12", "(1212"}) {
            boolean exception;
            try {
                exception = false;
                new Expression(n);
            } catch (IllegalArgumentException e) {
                exception = true;
                assertEquals("Expression class: value is an illegal number", e.getMessage(),"it throws wrong type of exception");
            }
            assertTrue(exception, n + " should throw exception, but didn't happen");
        }
    }

    @Test
    public void turnNegative() {
        String [] tests = new String[] {"123", "-123", "(", "-("};
        String [] anss = new String[] {"-123", "123", "-(", "("};
        for (int i = 0; i < tests.length; i++) {
            Expression e = new Expression(tests[i]);
            e.turnNegative();
            assertEquals(anss[i], e.getValue(), "should turn negative correctly, but did not");
        }

    }
}