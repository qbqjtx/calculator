package temp;

/**
 * Created by adrianoob on 5/6/14.
 */
public class FBracketException extends Exception {
    public FBracketException () {
        super();
    }
    public FBracketException (String ss) {
        super(ss);
    }
    public String toString () {
        return super.toString();
    }
    public String getMessage () {
        return "**Exception: Brackets Are Abused";
    }
}
