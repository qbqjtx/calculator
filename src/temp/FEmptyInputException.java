package temp;
/**
 * Created by adrianoob on 5/6/14.
 */
public class FEmptyInputException extends Exception {
    public FEmptyInputException () {
        super();
    }
    public FEmptyInputException (String ss) {
        super(ss);
    }
    public String toString () {
        return super.toString();
    }
    public String getMessage () {
        return "**Exception: Input String Is Empty";
    }
}
