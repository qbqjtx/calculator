package temp;

/**
 * Created by adrianoob on 5/12/14.
 */
public class Exptree {
    String operator = "0";
    Exptree left = null;
    Exptree right = null;

    //[isNumber] :: String -> boolean
    //[get_rid_of_outside_brackets] :: String -> String
    //[get_array_of_operator] :: String -> Char[]
    //[evaluate] :: String, Exptree, Exptree -> double`

    //four constructors :: only take formatted strings
    public Exptree () {
        String operator = "?";
        Exptree left = null;
        Exptree right = null;
    }

    public Exptree (double v) {
        String operator = String.valueOf(v);
        Exptree left = null;
        Exptree right = null;
    }

    public Exptree (String o, Exptree l, Exptree r) {
        operator = o;
        left = l;
        right = r;
    }

    public Exptree (String input) {
        String temp = get_rid_of_outside_brackets(input);
        if (isNumber(temp)) {
            operator = temp;
            left = null;
            right = null;
        }
        else {
            int [] opp = get_operator_position(temp);
            char [] op = new char [opp.length];
            for (int i=0; i<opp.length; i++) {
                op[i] = temp.charAt(opp[i]);
            }
            int plus_posi = -1;
            int mul_posi = -1;
            for (int i=0; i<op.length; i++) {
                if (op[i]=='+'||op[i]=='-') {
                    plus_posi = i;
                }
                else if (op[i]=='*'||op[i]=='/') {
                    mul_posi = i;
                }
            }
            if (plus_posi>-1) {//it has + or -
                operator = String.valueOf(op[plus_posi]);
                String left_string = temp.substring(0,opp[plus_posi]);
                left = new Exptree(left_string);
                String right_string = temp.substring(opp[plus_posi]+1);
                right = new Exptree(right_string);
            }
            else {//it doesn't have + or -
                operator = String.valueOf(op[mul_posi]);
                String left_string = temp.substring(0,opp[mul_posi]);
                left = new Exptree(left_string);
                String right_string = temp.substring(opp[mul_posi]+1);
                right = new Exptree(right_string);
            }
        }
    }



    public boolean isNumber (String input) {
        boolean flag = true;
        for (int i=0; i<input.length(); i++) {
            char cc = input.charAt(i);
            if (!((cc<58&&cc>47)||cc=='.')) {
                flag = false;
                break;
            }
        }
        return flag;
    }

    public String get_rid_of_outside_brackets (String input) {
        String str = input;
        //check if such pair of brackets exist
        boolean right_bracket_for_bracket_at_index_zero_is_at_end = false;
        if (str.charAt(0)=='(') {
            right_bracket_for_bracket_at_index_zero_is_at_end = true;
            int b_counter = 1;
            for (int i = 1; i < str.length()-1; i++) {
                if (b_counter == 0) {
                    right_bracket_for_bracket_at_index_zero_is_at_end = false;
                    break;
                }
                char cc = str.charAt(i);
                if (cc == ')') {
                    b_counter--;
                }
                else if (cc == '(') {
                    b_counter++;
                }
            }
        }
        if (right_bracket_for_bracket_at_index_zero_is_at_end) {//such brackets exists
            String str2 = input.substring(1,input.length()-1);
            str = get_rid_of_outside_brackets(str2);//do the same thing again in case there's another layer of brackets
        }
        return str;
    }

    public int[] get_operator_position (String input) {
        //search through string to find number of operators
        int b_counter = 0;
        int number_of_operators = 0;
        for (int i=0; i<input.length(); i++) {
            char cc = input.charAt(i);
            if (cc=='(') {
                b_counter++;
            }
            else if (cc==')') {
                b_counter--;
            }
            else {
                if (b_counter == 0) {
                    if (cc=='+'
                      ||cc=='-'
                      ||cc=='*'
                      ||cc=='/') {
                        number_of_operators++;
                    }
                }
            }

        }

        //save operators into an array
        int [] opp = new int [number_of_operators];
        b_counter = 0;
        number_of_operators = 0;
        for (int i=0; i<input.length(); i++) {
            char cc = input.charAt(i);
            if (cc=='(') {
                b_counter++;
            }
            else if (cc==')') {
                b_counter--;
            }
            else {
                if (b_counter == 0) {
                    if (cc == '+'
                            || cc == '-'
                            || cc == '*'
                            || cc == '/') {
                        opp[number_of_operators] = i;
                        number_of_operators++;
                    }
                }
            }
        }
        return opp;
    }

    public double evaluate () throws ZeroDenominatorException {
        double result = 0;
        if (operator.equals("?")) {
            result = 0;
        }
        else if (operator.equals("+")) {
            result = left.evaluate() + right.evaluate();
        }
        else if (operator.equals("-")) {
            result = left.evaluate() - right.evaluate();
        }
        else if (operator.equals("*")) {
            result = left.evaluate() * right.evaluate();
        }
        else if (operator.equals("/")) {
            if (right.evaluate() == 0) {
                throw new ZeroDenominatorException();
            } else {
                result = left.evaluate() / right.evaluate();
            }
        }
        else {
            result = Double.parseDouble(operator);
        }
        return result;
    }

    public String toString () {
        String output;
        if (isNumber(operator)) {
            output = operator;
        }
        else {
            output = (left.toString()+operator+right.toString());
        }
        return output;
    }
}
