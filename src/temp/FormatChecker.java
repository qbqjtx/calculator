package temp;
/**
 * Created by adrianoob on 5/6/14.
 */

public class FormatChecker {
    String formatted_string = "0";
    public FormatChecker (String s) {
        try {
            String checked_string = check(s);
            formatted_string = fix_informal_format(checked_string);
        }
        catch (FEmptyInputException e) {
            System.out.println(e.getMessage());
            formatted_string = "  **fail";
        }
        catch (FInvalidInputException e) {
            System.out.println(e.getMessage());
            formatted_string = "  **fail";
        }
        catch (FEndingException e) {
            System.out.println(e.getMessage());
            formatted_string = "  **fail";
        }
        catch (FBeginningException e) {
            System.out.println(e.getMessage());
            formatted_string = "  **fail";
        }
        catch (FDotException e) {
            System.out.println(e.getMessage());
            formatted_string = "  **fail";
        }
    }

    /*Check Exception

         ** String can't be empty (8)

         ** Using LL parsing to eliminate format problems
            * case number (1)
            * case '('  (2)
            * case ')'  (3)
            * case operators   (4)
            * case '.'  (11)

         ** Check Invalid Characters (5)

         ** Check The Last Character (6)

         ** Check The First Character (10)

         ** Check Bracket pairs (7)

         ** Check One number two Dots (12)

         ** Fix some informal format in numbers and ')' (9)
    */
    public String check (String str) //returns "  **fail" is exception is caught
            throws FEmptyInputException, FInvalidInputException,FEndingException,FBeginningException,FDotException {
        int length = str.length();
        try {
            if (length == 1) {
                char cc = str.charAt(0);
                if (cc>57 || cc < 48) {
                    throw new FInvalidInputException();
                }
            }
            else if (length>1) {
                for (int i = 0; i < length; i++) {
                    char cc= str.charAt(i);
                    if (!((cc<58&&cc>46)||(cc>39&&cc<44)||cc==45||cc=='.')) {
                        throw new FInvalidInputException();
                    }
                }
                for (int i = 0; i < length-1; i++) {
                    char c0 = str.charAt(i);
                    char c1 = str.charAt(i+1);
                    if (c0 == '(') {//'(' (2)
                        validity_Suffix_LeftBracket(c1);
                    }

                    else if (c0 > 47 && c0 < 58) {//numbers (1)
                        //anything will be fine except for invalid characters
                    }
                    else if (c0=='+'||c0=='-'||c0=='*'||c0=='/') {//arithmetic operators (4)
                        validity_Suffix_Operator(c1);
                    }
                    else if (c0 == ')') {//')' (3)
                        //anything will be fine except for invalid characters
                    }
                    else if (c0 == '.') {
                        //only fine with numbers
                        if (c1>57 || c1<48) {
                            throw new FDotException();
                        }
                    }
                }
                //Check one number two dots (12)
                int dot_counter = 0;
                for (int i = 0; i < str.length(); i++) {
                    if (dot_counter > 1) {
                        throw new FDotException();
                    }
                    else if (str.charAt(i)=='.') {
                        dot_counter++;
                    }
                    else if (str.charAt(i)>57||str.charAt(i)<48) {//'.' is considered above
                        dot_counter = 0;
                    }
                }
                // Last Character (6)
                char lastChar = str.charAt(length-1);
                if (lastChar=='+'||lastChar=='-'||lastChar=='*'||lastChar=='/'||lastChar=='(') {
                    throw new FEndingException();
                }
                //First Character (10)
                char firstChar = str.charAt(0);
                if (!((firstChar>47&&firstChar<58)||firstChar=='+'||firstChar=='-'||firstChar=='(')) {
                    throw new FBeginningException();
                }
                //need check bracket pairs
                check_BracketPairs(str);
            }
            else {//Empty String (8)
                throw new FEmptyInputException();
            }
        }
        catch (FBracketException e) {
            str = "  **fail";
            System.out.println(e.getMessage());
        }
        catch (FOperatorException e) {
            str = "  **fail";
            System.out.println(e.getMessage());
        }
        return str;
    }

    void validity_Suffix_LeftBracket (char c1) throws FBracketException,FOperatorException {
        if (c1 == ')') {
            throw new FBracketException();
        }
        else if (c1 == '*' || c1 == '/') {
            throw new FOperatorException();
        }
    }

    void validity_Suffix_Operator (char c1) throws FOperatorException, FBracketException {
        if (c1=='+'||c1=='-'||c1=='/'||c1=='*') {
            throw new FOperatorException();
        }
        else if (c1 == ')') {
            throw new FBracketException();
        }
    }
    void check_BracketPairs (String str) throws FBracketException{
        int counter = 0;
        int length = str.length();
        for (int i = 0; i < length; i++) {
            if (counter < 0) {
                throw new FBracketException();
            }
            else {
                if (str.charAt(i) == '(') {
                    counter++;
                }
                else if (str.charAt(i) == ')') {
                    counter--;
                }
            }
        }
        if (counter != 0) {
            throw new FBracketException();
        }
    }

    //(9) fix 1() ()1 or ()()
    public String fix_informal_format (String ss) {
        String str = ss;
        int len = str.length();
        for (int i = 0; i < len-1; i++) {
            char c0 = str.charAt(i);
            char c1 = str.charAt(i+1);
            if ((c0<58&&c0>47&&c1=='(') || (c0==')'&&c1=='(') || (c0==')'&&c1>47&&c1<58)) {
                String s1 = str.substring(0,i+1);
                String s2 = str.substring(i+1);
                str = s1+"*"+s2;//insert * operator
                break;
            }
        }
        if (ss.length() != str.length()) {
            str = fix_informal_format(str);
        }
        return str;
    }
}
