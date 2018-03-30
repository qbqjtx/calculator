package temp;

/**
 * Created by adrianoob on 5/6/14.
 */
public class FOperatorException extends Exception {
    public FOperatorException () {
        super();
    }
    public FOperatorException (String ss) {
        super(ss);
    }
    public String toString () {
        return super.toString();
    }
    public String getMessage () {
        return "**Exception: Operators Are Abused";
    }
}
