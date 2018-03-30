package model;

import com.sun.istack.internal.NotNull;

import java.util.ArrayList;
import java.util.Stack;

public class Parser {
    private Expression [] exps;

    public Parser(@NotNull String str) {
        exps = Parser.tokenize(str);
        if (!hasValidSyntax()) throw new IllegalArgumentException("Parser: syntax is invalid");

        // now syntax is valid, it's available to parse
    }

    /**
     * Tokenize [str] into a list of Expression, namely, +, -, *, /, (, ), numbers.
     * @param str - input str
     * @return - a list of expression
     */
    // TODO should be private static
    public static Expression[] tokenize(String str) {
        if (str == null || str.equals("")) return null;

        int idx = -1;
        String temp = "";
        ArrayList<Expression> parserList = new ArrayList<>();
        while (++idx < str.length()) {
            char c = str.charAt(idx);
            if (c == '+' || c == '-' || c == '*' || c == '/' || c == '(' || c == ')') {

                if (!temp.equals("")) {
                    parserList.add(new Expression(temp));
                    temp = "";
                }
                // get the sign
                parserList.add(new Expression(Character.toString(c)));
            }
            else {
                temp = temp + Character.toString(c);
            }
        }
        // if temp has something left after looping, we need to record last token
        if (!temp.equals("")) parserList.add(new Expression(temp));

        // turn negative or reverse, 12 -> -12, ( -> -(, /3 -> (1/3), /( -> 1/(
        // for when starting with -
        if (parserList.get(0).isMinus()) {
            parserList.get(1).turnNegative();
            parserList.remove(0);
        }
        // for others
        for (int i = 1; i < parserList.size(); i++) {
            if (parserList.get(i).isMinus()) {
                parserList.get(i+1).turnNegative();
                parserList.get(i).setValue("+");
                i++;
            }
            else if (parserList.get(i).isDivide()) {
                parserList.get(i+1).reverse();
                parserList.get(i).setValue("*");
                i++;
            }
        }

        return parserList.toArray(new Expression[parserList.size()]);
    }

    /**
     * check if [exps] list has valid syntax
     * @return - result
     */
    // TODO should be private
    public boolean hasValidSyntax() {

        // 1. go through each exps, make sure:
        //    - numbers are followed by operators or right brackets
        //    - operators are followed by numbers or left brackets
        //    - left brackets followed by numbers or left brackets
        //    - right brackets followed by operators or right brackets
        boolean isValid = true;
        for (int i = 0; i < exps.length - 1; i++) {
            Expression next = exps[i+1];
            if (exps[i].isNumber()) isValid = next.isOperator() || next.isRightBracket();
            else if (exps[i].isOperator()) isValid = next.isNumber() || next.isLeftBracket();
            else if (exps[i].isLeftBracket()) isValid = next.isNumber() || next.isLeftBracket();
            else if (exps[i].isRightBracket()) isValid = next.isOperator() || next.isRightBracket();

            if (!isValid) return false;
        }

        // 2. make sure exps starts with numbers or left brackets or minus sign, ends with numbers or right brackets;
        if (((exps[0]).isOperator() && !exps[0].isMinus()) || exps[0].isRightBracket())
            return false;
        if (exps[exps.length-1].isOperator() || exps[exps.length-1].isLeftBracket())
            return false;

        // 3. check if brackets are paired using a stack
        Stack<Boolean> stack = new Stack<>();
        for (Expression e : exps) {
            if (e.isLeftBracket()) stack.push(true);
            else if (e.isRightBracket()) {
                if (stack.isEmpty())
                    return false;
                stack.pop();
            }
        }
        if (!stack.isEmpty()) return false;

        return true;
    }

    /**
     * parse an expression list to ExpTree, starting from index [from] to [to]
     * the list MUST HAVE VALID SYNTAX, and only has + and *!
     * Rules are in Parsing.pdf
     * @param from - starting index of [exps] (included)
     * @param to - ending index (NOT included)
     * @return - result expTree
     */
    // TODO should be private
    public ExpTree parseExpressionList(int from, int to) {
        if (to > exps.length) throw new IllegalArgumentException("Parser: [to] out of array boundary");
        if (from >= to) throw new IllegalArgumentException("Parser: [from] bigger or equal to [to]");

        // if only indices only include 1 Expression
        if  (from + 1 == to) return new ExpTree(exps[from]);

        // since now there are more than 1 expression, there has to be an operator
        int firstOperator = from + 1; // idx of first operator

        // if first Expression in the list is "(", find its soul mate, and treat everything
        // in the bracket as an (child) ExpTree, and first operator is following right bracket
        if (exps[from].isLeftBracket())
            firstOperator = indexOfPairedBracket(from) + 1;

        // look ahead
        // make sure firstOperator operator exists, if not, current exp is a sole single Expression
        // either like [num], or [(num + num + num)]
        if (firstOperator >= to){
            switch (exps[from].getValue()) {
                case "(":
                    return parseExpressionList(from + 1, to - 1);
                case "-(":
                    return new ExpTree(new ExpTree(new Expression("-1")), new Expression("*"), parseExpressionList(from + 1, to - 1));
                case "1/(":
                    return new ExpTree(new ExpTree(new Expression("1")), new Expression("/"), parseExpressionList(from + 1, to - 1));
                default:
                    return new ExpTree(null, exps[from], null);
            }
        }

        // now we know that first operator exists
        // if it's "+", everything before the first operator is [left], "+" is self, continue
        if (exps[firstOperator].isPlus())
            return new ExpTree(parseExpressionList(from, firstOperator), exps[firstOperator], parseExpressionList(firstOperator + 1, to));

        // now first sign must be *, look for first none * sign
        return dealWithMultiply(from, firstOperator, to);

    }


    /**
     * input the location of a left bracket, and find its corresponding right bracket
     * @param idx - index of left bracket
     * @return - its soul mate
     */
    // TODO: should be private
    public int indexOfPairedBracket(int idx) {
        if (!exps[idx].isLeftBracket()) throw new IllegalArgumentException("Parser: input should be but is not a left bracket");

        int ans = -1;
        // use a stack to find soul mate, and it's guaranteed since syntax is valid
        int stack = 1;
        for (int i = idx+1; i < exps.length; i++) {
            if (exps[i].isLeftBracket()) stack++;
            else if (exps[i].isRightBracket()) stack--;
            if (stack == 0) {
                ans = i;
                break;
            }
        }
        return ans;
    }


    /**
     * helper function
     * @param from - reference to main function body
     * @param firstOperator - reference to main function body
     * @param to - reference to main function body
     * @return - reference to main function body
     */
    public ExpTree dealWithMultiply(int from, int firstOperator, int to) {
        // look ahead for the first none-* operator, before that operator, everything times together
        // the operator will be the root, rest will be right
        int idx1 = firstOperator;
        int idx2 = 0;
        for (int i = idx1+1; i < to; i++){
            if (exps[i].isLeftBracket()) {
                i = indexOfPairedBracket(i);
                continue;
            }
            if (exps[i].isOperator()) {
                idx2 = i;
                break;
            }
        }
        // if firstOperator is the only operator
        if (idx2 == 0)
            return new ExpTree(parseExpressionList(from, firstOperator), exps[firstOperator], parseExpressionList(firstOperator+1, to));

        // now there must exist at least another operator
        // everything before that operator forms a tree, call it leftTemp
        ExpTree leftTemp = parseExpressionList(from, firstOperator);
        while (exps[idx2].isMultiply()) {
            // take old leftTemp as left, make new tree.
            leftTemp  = new ExpTree(leftTemp, exps[idx1], parseExpressionList(idx1+1, idx2));
            // look for the next operator
            idx1 = idx2;
            for (int i = idx1+1; i< to; i++) {
                if (exps[i].isLeftBracket()) {
                    i = indexOfPairedBracket(i);
                    continue;
                }
                if (exps[i].isOperator()) {
                    idx2 = i;
                    break;
                }
            }
            // if until the end, all sign are multiplication
            if (idx1 == idx2)
                return new ExpTree(leftTemp, exps[idx2], parseExpressionList(idx2+1, to));
            // now we have a new idx2
            if (exps[idx2].isPlus())
                break;
        }
        // now we find one plus at idx2
        leftTemp = new ExpTree(leftTemp, exps[idx1], parseExpressionList(idx1+1, idx2));
        return new ExpTree(leftTemp, exps[idx2], parseExpressionList(idx2+1, to));
    }

    public ExpTree parseToTree() {
        return parseExpressionList(0, exps.length);

    }
}
