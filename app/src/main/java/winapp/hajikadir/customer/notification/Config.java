package winapp.hajikadir.customer.notification;

/**
 * Created by user on 28-Sep-17.
 */

public class Config {

    //firebase notifiction serverkey = AIzaSyCwIQlNFeJOphd6W2KTv8MpG8w-foHZ4jk     Login androidappintegration@gmail.com

    // global topic to receive app wide push notifications
    public static final String TOPIC_GLOBAL = "global";

    // broadcast receiver intent filters
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";

    // id to handle the notification in the notification tray
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;

    public static final String SHARED_PREF = "ah_firebase";
}
