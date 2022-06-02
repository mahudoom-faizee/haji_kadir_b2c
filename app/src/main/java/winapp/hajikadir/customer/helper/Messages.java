package winapp.hajikadir.customer.helper;

/**
 * Created by user on 13-Jul-16.
 */
public class Messages {
    public static final String ERROR_PRECEDE = "Error - ";

    public static final String CONFIRM_CLOSE_APP = "Confirm to close application?";

    public static String getEmptyText(String str) {
        return String.format("Please enter %s!", str);
    }

    public static String getEmptyChoose(String str) {
        return String.format("Please choose %s!", str);
    }

    public static String getInvalidText(String str) {
        return String.format("Invalid %s value", str);
    }

    public static String getInvalidChoose(String str) {
        return String.format("Please choose valid %s!", str);
    }

    public static String err(Exception e) {
        return ERROR_PRECEDE + e.toString();
    }

}
