package test;

import model.ExpTree;
import model.Parser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ParserTest {

    @Test
    public void tokenize() {
        assertEquals(1, Parser.tokenize("123").length,"tokenzied list has wrong size");
        assertEquals(2, Parser.tokenize("123)").length,"tokenzied list has wrong size");
        assertEquals(2, Parser.tokenize("(123").length,"tokenzied list has wrong size");
        assertEquals(4, Parser.tokenize("(+123)").length,"tokenzied list has wrong size");
        assertEquals(5, Parser.tokenize("(12+12)").length,"tokenzied list has wrong size");
        assertEquals(7, Parser.tokenize("+-13*/2(").length,"tokenzied list has wrong size");
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
            assertTrue(new Parser(s).hasValidSyntax(), s+" should have valid syntax, but does not");
        for (String s : badStrings) {
            boolean good = true;
            try {
                new Parser(s).hasValidSyntax();
            } catch (IllegalArgumentException e) {
                assertEquals("Parser: syntax is invalid", e.getMessage(), "Invalid Syntax should throw exception");
                good = false;
            }
            assertFalse(good, s + " should throw invalid syntax exception, but did not");
        }
    }

    @Test
    public void indexOfPairedBracket() {
        String message = "should find the matched right bracket, but didn't";
        Parser p;
        p = new Parser("(123+123)+2");
        assertEquals(4, p.indexOfPairedBracket(0), message);

        p = new Parser("(12*32+2)*(32+23)");
        assertEquals(6, p.indexOfPairedBracket(0), message);
        assertEquals(12, p.indexOfPairedBracket(8), message);

        p = new Parser("((12+12)*(32+32)+1)/23");
        assertEquals(14, p.indexOfPairedBracket(0), message);
        assertEquals(5, p.indexOfPairedBracket(1), message);
        assertEquals(11, p.indexOfPairedBracket(7), message);

        p = new Parser("(((1+1)*2+1)/1-1)*2");
        assertEquals(16, p.indexOfPairedBracket(0), message);
        assertEquals(11, p.indexOfPairedBracket(1), message);
        assertEquals(6, p.indexOfPairedBracket(2), message);

        // failure test
        try {
            p.indexOfPairedBracket(2);
        } catch (IllegalArgumentException e) {
            assertEquals("Parser: input should be but is not a left bracket", e.getMessage(), "should throw exception when input is not left bracket index, but did not");
        }
    }


    private void testTreeHelper(ExpTree tree, String sign, String left, String right) {
        assertEquals(sign, tree.getSelf().getValue(), "Tree.self is not right");
        if (left != null)
            assertEquals(left, tree.getLeft().getSelf().getValue(), "Tree.left is not right");
        if (right != null)
            assertEquals(right, tree.getRight().getSelf().getValue(), "Tree.right is not right");
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