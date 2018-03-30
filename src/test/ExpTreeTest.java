package test;

import model.ExpTree;
import model.Expression;
import model.Parser;
import org.junit.Test;

import static org.junit.Assert.*;

public class ExpTreeTest {
    public void assertEqualHelper(String str, double expected) {
        assertEquals(expected, new Parser(str).parseToTree().evaluate(), 0.000001);
    }

    @Test
    public void evaluate1() {
        // evaluating numbers
        assertEqualHelper("1", 1);
        assertEqualHelper("-2", -2);

        Expression special = new Expression("3"); special.reverse();
        assertEquals((1.0/3), new ExpTree(special).evaluate(), 0.0000001);
        special = new Expression("-3"); special.reverse();
        assertEquals((-1.0/3), new ExpTree(special).evaluate(), 0.000001);
        special = new Expression("0"); special.reverse();
        boolean wrong = false;
        try {
            new ExpTree(special).evaluate();
        } catch (IllegalArgumentException e) {
            wrong = true;
        }
        assertTrue("divide by 0 should throw exception, but did not", wrong);
    }

    @Test
    public void evaluate2() {
        // simplest calculation
        assertEqualHelper("1+2", 3);
        assertEqualHelper("3-4", -1);
        assertEqualHelper("5.6*5", 28);
        assertEqualHelper("7/8", 0.875);

        boolean wrong = false;
        try {
            new Parser("9/0").parseToTree().evaluate();
        } catch (IllegalArgumentException e) {
            wrong = true;
        }
        assertTrue("divide by 0 should throw exception, but did not", wrong);
    }

    @Test
    public void evaluate3() {
        // 3 number evaluation
        assertEqualHelper("2.1+3.3*3", 12);
        assertEqualHelper("1+2+3", 6);
        assertEqualHelper("3+4-5", 2);
        assertEqualHelper("3+4/5", 3.8);

        assertEqualHelper("1-2+3", 2);
        assertEqualHelper("2-3-4", -5);
        assertEqualHelper("3-4*5", -17);
        assertEqualHelper("3-4/5", 2.2);

        assertEqualHelper("11*2+3", 25);
        assertEqualHelper("2.2*3-1.6", 5);
        assertEqualHelper("2.5*8*1.5", 30);
        assertEqualHelper("3.5*4/2.5", 5.6);

        assertEqualHelper("5/2+5", 7.5);
        assertEqualHelper("12.5/2.5-6", -1);
        assertEqualHelper("015/3*4", 20);
        assertEqualHelper("2500/50/500", 0.1);

        // involve 0
        boolean wrong = false;
        try {
            new Parser("3/0+3").parseToTree().evaluate();
        } catch (IllegalArgumentException e) {
            wrong = true;
        }
        assertTrue("divide by 0 should throw exception, but did not", wrong);
    }

    @Test
    public void evaluate4() {
        // long calculation
        assertEqualHelper("(1+2)*(3+4)*(5+6)*(7+8)", 3465);
        assertEqualHelper("(2+3)*(2-3)*(1+2)-(3-4)*(4+2)-(1+9)", -19);
        assertEqualHelper("(2+3)/(2-3)*(1+2)-1/(3-5)*(4+2)-0.4*(1+9)", -16);
    }
}