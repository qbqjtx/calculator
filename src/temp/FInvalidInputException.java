package temp;

/**
 * Created by adrianoob on 5/6/14.
 */
public class FInvalidInputException extends Exception {
    public FInvalidInputException () {
        super();
    }
    public FInvalidInputException (String ss) {
        super(ss);
    }
    public String toString () {
        return super.toString();
    }
    public String getMessage () {
        return "**Exception: Invalid Symbols In Input";
    }
}
