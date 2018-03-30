package temp;

/**
 * Created by adrianoob on 5/11/14.
 */
public class FDotException extends Exception {
    public FDotException () {
        super();
    }
    public FDotException (String ss) {
        super(ss);
    }
    public String toString () {
        return super.toString();
    }
    public String getMessage () {
        return "**Exception: Dots Are In Crazy Positions";
    }
}
