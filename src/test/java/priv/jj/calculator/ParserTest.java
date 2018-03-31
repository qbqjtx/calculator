package priv.jj.calculator;


import org.junit.Test;
import static org.junit.Assert.*;
import priv.jj.calculator.model.ExpTree;
import priv.jj.calculator.model.Parser;

public class ParserTest {

    @Test
    public void tokenize() {
        String message = "tokenzied list has wrong size";
        assertEquals(message, 1, Parser.tokenize("123").length);
        assertEquals(message, 2, Parser.tokenize("123)").length);
        assertEquals(message, 2, Parser.tokenize("(123").length);
        assertEquals(message, 4, Parser.tokenize("(+123)").length);
        assertEquals(message, 5, Parser.tokenize("(12+12)").length);
        assertEquals(message, 7, Parser.tokenize("+-13*/2(").length);
    }

    @Test
    public void hasValidSyntax() {
        String [] goodStrings = new String[]{
                "123","123+123","(123+123)+2","(12*32+2)*(32+23)","((12+12)*(32+32)+1)/23","(((1+1)*2+1)/1-1)*2","-123","-2*3","-(1+2)*3"
        };
        String [] badStrings = new String[]{
                "(123","123)","123(123+2)","+1","1*","123+(123-(123*123))-2)","123+)123","(123+123)123","12++12"
        };
        for (String s : goodStrings)
            assertTrue(s+" should have valid syntax, but does not", new Parser(s).hasValidSyntax());
        for (String s : badStrings) {
            boolean good = true;
            try {
                new Parser(s).hasValidSyntax();
            } catch (IllegalArgumentException e) {
                assertEquals("Invalid Syntax should throw exception", "Parser: syntax is invalid", e.getMessage());
                good = false;
            }
            assertFalse(s + " should throw invalid syntax exception, but did not", good);
        }
    }

    @Test
    public void indexOfPairedBracket() {
        String message = "should find the matched right bracket, but didn't";
        Parser p;
        p = new Parser("(123+123)+2");
        assertEquals(message, 4, p.indexOfPairedBracket(0));

        p = new Parser("(12*32+2)*(32+23)");
        assertEquals(message, 6, p.indexOfPairedBracket(0));
        assertEquals(message, 12, p.indexOfPairedBracket(8));

        p = new Parser("((12+12)*(32+32)+1)/23");
        assertEquals(message, 14, p.indexOfPairedBracket(0));
        assertEquals(message, 5, p.indexOfPairedBracket(1));
        assertEquals(message, 11, p.indexOfPairedBracket(7));

        p = new Parser("(((1+1)*2+1)/1-1)*2");
        assertEquals(message, 16, p.indexOfPairedBracket(0));
        assertEquals(message, 11, p.indexOfPairedBracket(1));
        assertEquals(message,6, p.indexOfPairedBracket(2));

        // failure test
        try {
            p.indexOfPairedBracket(2);
        } catch (IllegalArgumentException e) {
            assertEquals("Parser: input should be but is not a left bracket", e.getMessage(), "should throw exception when input is not left bracket index, but did not");
        }
    }


    private void testTreeHelper(ExpTree tree, String sign, String left, String right) {
        assertEquals("Tree.self is not right", sign, tree.getSelf().getValue());
        if (left != null)
            assertEquals("Tree.left is not right", left, tree.getLeft().getSelf().getValue());
        if (right != null)
            assertEquals("Tree.right is not right", right, tree.getRight().getSelf().getValue());
    }

    @Test
    public void parserExpressionList_someTestAndPlus() {
        ExpTree t = new Parser("121").parseExpressionList(0,1);
        testTreeHelper(t, "121", null, null);

        t = new Parser("1+2").parseExpressionList(0,3);
        testTreeHelper(t, "+", "1", "2");

        t = new Parser("1+2+3").parseExpressionList(0,5);
        testTreeHelper(t, "+", "1", "+");

        t = new Parser("1+2+3").parseExpressionList(2,5);
        testTreeHelper(t, "+", "2", "3");

        t = new Parser("1-2-3").parseExpressionList(0,5);
        testTreeHelper(t, "+", "1", "+");

        t = new Parser("1-2-3").parseExpressionList(2,5);
        testTreeHelper(t, "+", "-2", "-3");

        t = new Parser("-2").parseExpressionList(0,1);
        testTreeHelper(t, "-2", null, null);

        t = new Parser("-(1-2)").parseExpressionList(0,5); // (-1)*(1+(-2))
        testTreeHelper(t, "*", "-1", "+");
        testTreeHelper(t.getRight(), "+", "1", "-2");


        t = new Parser("1-2-(3-4)").parseExpressionList(0, 9); // 1+(-2)+(-1)*(3+(-4))
        testTreeHelper(t, "+", "1", "+");
        testTreeHelper(t.getLeft(), "1", null, null);
        testTreeHelper(t.getRight(), "+", "-2", "*");
        testTreeHelper(t.getRight().getRight(), "*", "-1", "+");
        testTreeHelper(t.getRight().getRight().getRight(), "+", "3", "-4");
    }
//
//    @Test
//    public void dealWithMinus() {
//        ExpTree t = new Parser("2-1").dealWithMinus(0,1,3);
//        testTreeHelper(t, "-", "2", "1");
//
//        t = new Parser("1-2-3").dealWithMinus(0,1,5);
//        testTreeHelper(t, "-", "-", "3");
//
//        t = new Parser("1-2+3").dealWithMinus(0, 1, 5);
//        testTreeHelper(t, "+", "-", "3");
//
////        t = new Parser("1-2*3").dealWithMinus(0, 1, 5);
////        testTreeHelper(t, "-", "1", "*");
//
//        t = new Parser("1-2/3").dealWithMinus(0, 1, 5);
//        testTreeHelper(t, "-", "1", "/");
//
//        // TODO: test with brackets
//
//    }

    @Test
    public void dealWithMultiply() {
        ExpTree t = new Parser("1*2").dealWithMultiply(0,1,3);
        testTreeHelper(t, "*", "1", "2");

        t = new Parser("1/2").dealWithMultiply(0,1,3);
        testTreeHelper(t, "*", "1", "1/2");

        t = new Parser("-2*1").dealWithMultiply(0, 1, 3);
        testTreeHelper(t, "*", "-2", "1");

        t = new Parser("1*2*3").dealWithMultiply(0,1,5);
        testTreeHelper(t, "*", "*", "3");
        testTreeHelper(t.getLeft(), "*", "1", "2");


        t = new Parser("1*2*3*4*5").dealWithMultiply(0,1,9);
        testTreeHelper(t, "*", "*", "5");
        testTreeHelper(t.getLeft(), "*", "*", "4");
        testTreeHelper(t.getLeft().getLeft(), "*", "*", "3");
        testTreeHelper(t.getLeft().getLeft().getLeft(), "*", "1", "2");

        t = new Parser("1/2*3").dealWithMultiply(0, 1, 5);
        testTreeHelper(t, "*", "*", "3");
        testTreeHelper(t.getLeft(), "*", "1", "1/2");

        t = new Parser("1*2/3").dealWithMultiply(0, 1, 5);
        testTreeHelper(t, "*", "*", "1/3");
        testTreeHelper(t.getLeft(), "*", "1", "2");
    }

    @Test
    public void moreHybridTests() {
        ExpTree t = new Parser("1*2*3-4").parseExpressionList(0,7);
        testTreeHelper(t, "+", "*", "-4");
        testTreeHelper(t.getLeft(), "*", "*", "3");
        testTreeHelper(t.getLeft().getLeft(), "*", "1", "2");

        t = new Parser("1*2*3*4-5").parseExpressionList(0,9);
        testTreeHelper(t, "+", "*", "-5");
        testTreeHelper(t.getLeft(), "*", "*", "4");
        testTreeHelper(t.getLeft().getLeft(), "*", "*", "3");
        testTreeHelper(t.getLeft().getLeft().getLeft(), "*", "1", "2");

        // now involve brackets
        t = new Parser("(2*3)").parseToTree();
        testTreeHelper(t, "*", "2", "3");

        t = new Parser("-(1*2)").parseToTree();
        testTreeHelper(t, "*", "-1", "*");
        testTreeHelper(t.getRight(), "*", "1", "2");

        t = new Parser("1/(2+3)").parseToTree();
        testTreeHelper(t, "*", "1", "/");
        testTreeHelper(t.getRight(), "/", "1", "+");
        testTreeHelper(t.getRight().getRight(), "+", "2", "3");


        t = new Parser("1*(2*3)").dealWithMultiply(0,1, 7);
        testTreeHelper(t, "*", "1", "*");
        testTreeHelper(t.getRight(), "*", "2", "3");

        t = new Parser("1*(2-3)").dealWithMultiply(0,1, 7);
        testTreeHelper(t, "*", "1", "+");
        testTreeHelper(t.getRight(), "+", "2", "-3");

        t = new Parser("(2+3)*(2-3)*(1+2)-(3-4)*(4+2)-(1+9)").parseToTree();
        /*  + (2+3)*(2-3)*(1+2)
         *    -(3-4)*(4+2)-(1+9)
         *
         *  + (2+3)*(2-3)*(1+2)
         *    + -(3-4)*(4+2)
         *      -(1+9)
         *
         *  + (2+3)*(2-3)*(1+2)
         *    + * * -1
         *          3-4
         *        4+2
         *      * -1
         *        1+9
         */
        testTreeHelper(t, "+", "*","+");
        testTreeHelper(t.getLeft(), "*", "*", "+");
        testTreeHelper(t.getLeft().getRight(), "+", "1", "2");
        testTreeHelper(t.getLeft().getLeft(), "*", "+", "+");
        testTreeHelper(t.getLeft().getLeft().getLeft(), "+", "2", "3");
        testTreeHelper(t.getLeft().getLeft().getRight(), "+", "2", "-3");

        testTreeHelper(t.getRight(), "+", "*", "*");
        testTreeHelper(t.getRight().getLeft(), "*", "*", "+");
        testTreeHelper(t.getRight().getLeft().getLeft(), "*", "-1", "+");
        testTreeHelper(t.getRight().getLeft().getLeft().getLeft(), "-1", null, null);
        testTreeHelper(t.getRight().getLeft().getLeft().getRight(), "+", "3", "-4");
        testTreeHelper(t.getRight().getLeft().getRight(), "+", "4", "2");
        testTreeHelper(t.getRight().getRight(), "*", "-1", "+");
        testTreeHelper(t.getRight().getRight().getRight(), "+", "1", "9");



    }




}