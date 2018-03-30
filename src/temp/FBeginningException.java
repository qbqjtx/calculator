package temp;

/**
 * Created by adrianoob on 5/6/14.
 */
public class FBeginningException extends Exception {
    public FBeginningException () {
        super();
    }
    public FBeginningException (String ss) {
        super(ss);
    }
    public String toString () {
        return super.toString();
    }
    public String getMessage () {
        return "**Exception: Beginning Character Is Invalid";
    }
}
