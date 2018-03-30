package temp;

/**
 * Created by adrianoob on 5/6/14.
 */
public class ZeroDenominatorException extends Exception{
    public ZeroDenominatorException () {
        super();
    }
    public ZeroDenominatorException (String ss) {
        super(ss);
    }

    public String toString () {
        return super.toString();
    }

    public String getMessage (){
        return "**Exception: Denominator Is Zero";
    }
}
