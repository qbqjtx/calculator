package temp;
import java.util.Scanner;
public class Main {

    public static void main(String[] args) {

        Scanner scan = new Scanner (System.in);
        System.out.print("> ");
        String str0 = scan.next();

        while (!str0.equals("q")) {
            // two step:
            // 1. check format :format exception -> end
            // 2. calculate -> end
            try {
                FormatChecker fc = new FormatChecker(str0);
                String formatted_string = fc.formatted_string;
                if (formatted_string.equals("  **fail")) {//format flaw
                    System.out.println(formatted_string);
                }
                else {//format is correct
                    Exptree expt = new Exptree(formatted_string);
                    String result = String.valueOf(expt.evaluate());
                    if (!result.contains("E")
                            &&result.contains(".")
                            &&result.charAt(result.length()-1)=='0') {
                        result = result.substring(0,result.length()-1);
                    }
                    if (result.charAt(result.length()-1) == '.') {
                        result = result.substring(0,result.length()-1);
                    }
                    System.out.println(result);
                }
            }
            catch (ZeroDenominatorException e) {
                System.out.println(e.getMessage());
            }
            /*if (exp.expression.equals("  **fail")) {
                System.out.println(exp.expression);
            }
            else {
                System.out.println(exp.value);
            }*/
            System.out.print("> ");
            str0 = scan.next();
        }
    }
}


