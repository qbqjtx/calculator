package temp;

/**
 * Created by adrianoob on 5/6/14.
 */
public class FEndingException extends Exception {
    public FEndingException () {
        super();
    }
    public FEndingException (String ss) {
        super(ss);
    }
    public String toString () {
        return super.toString();
    }
    public String getMessage () {
        return "**Exception: Invalid Character At End Of Input";
    }
}
