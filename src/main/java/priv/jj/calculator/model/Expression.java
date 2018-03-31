package priv.jj.calculator.model;


public class Expression {
    // internal constants
    private final int OPERATOR = 1;
    private final int NUMBER = 2;
    private final int LEFT = 3;
    private final int RIGHT = 4;

    // instance variables
    private String value;
    private int type;

    // not null string value
    public Expression(String value) throws IllegalArgumentException {
        setValue(value);
    }



    public boolean isOperator() { return type == OPERATOR; }

    public boolean isNumber() { return type == NUMBER; }

    public boolean isLeftBracket() { return type == LEFT; }

    public boolean isRightBracket() { return type == RIGHT; }

    public boolean isPlus() { return value.equals("+"); }

    public boolean isMinus() { return value.equals("-"); }

    public boolean isDivide() { return value.equals("/"); }

    public boolean isMultiply() { return value.equals("*"); }

    public String getValue() { return value; }

    public void setValue(String value) {
        if (value.equals("Expression class: value is empty"))
            throw new IllegalArgumentException();
        this.value = value;
        switch(value) {
            case "+":
            case "-":
            case "*":
            case "/": type = OPERATOR; break;
            case "-(":
            case "(": type = LEFT; break;
            case ")": type = RIGHT; break;
            default:
                if (isANumber(value))
                    type = NUMBER;
                else
                    throw new IllegalArgumentException("Expression class: value is an illegal number");
        }
    }



    /**
     * check if a string represents a valid number
     * @param str - input string, which is NOT NULL
     * @return - whether it's a valid number
     */
    private boolean isANumber(String str) {
        if (str.contains("+") || str.contains("*") || str.contains("/") || str.contains("(") || str.contains(")")) return false;
        if (str.contains("-") && str.charAt(0)!='-') return false;
        if (str.charAt(0) == '.' || str.charAt(str.length()-1) == '.') return false;
        if (str.indexOf('.') != str.lastIndexOf('.')) return false;
        return true;
    }

    public void turnNegative() {
        if (isNumber()) {
            if (value.charAt(0) == '-') value = value.substring(1);
            else value = "-" + value;
        }
        else if (value.equals("("))
            value = "-(";
        else if (value.equals("-("))
            value = "(";
        else
            throw new IllegalArgumentException("Expression: " + value + " doesn't have a negative form");
    }

    public void reverse() {
        if (isNumber()) {
            if (value.charAt(0) == '/') value = value.substring(1);
            else value = "1/" + value;
        }
        else if (value.equals("("))
            value = "1/(";
        else if (value.equals("-("))
            value = "-1/(";
        else
            throw new IllegalArgumentException("Expression: " + value + " doesn't have a reversed form");
    }
}
