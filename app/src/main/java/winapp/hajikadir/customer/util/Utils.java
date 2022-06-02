package winapp.hajikadir.customer.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.view.View;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import winapp.hajikadir.customer.R;
import winapp.hajikadir.customer.config.StringUtils;


/**
 * Created by user on 01-Nov-16.
 */

public class Utils {



    // Email Pattern
    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    public static String getAmountInFormat(double dblAmount) {
        return StringUtils.DECIMAL_FORMAT.format(dblAmount);
    }

    public static boolean isEmpty(String str) {
        return TextUtils.isEmpty(str);
    }
    public static boolean validate(String email) {
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
    public static boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 2;
    }


    public static boolean isNetworkReachable(Context context) {
        ConnectivityManager mManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo current = mManager.getActiveNetworkInfo();
        if (current == null) {
            return false;
        }
        return (current.getState() == NetworkInfo.State.CONNECTED);
    }

    public static void setBadgeCount(Context context, LayerDrawable icon, int count) {

        BadgeDrawable badge;

        // Reuse drawable if possible
        Drawable reuse = icon.findDrawableByLayerId(R.id.ic_badge);
        if (reuse != null && reuse instanceof BadgeDrawable) {
            badge = (BadgeDrawable) reuse;
        } else {
            badge = new BadgeDrawable(context);
        }

        badge.setCount(count);
        icon.mutate();
        icon.setDrawableByLayerId(R.id.ic_badge, badge);
    }
    public static void showViews(boolean show, View... views) {
        int visibility = show ? View.VISIBLE : View.GONE;
        for (View view : views) {
            view.setVisibility(visibility);
        }
    }
    public static void inVisibleViews(boolean show, View... views) {
        int visibility = show ? View.VISIBLE : View.INVISIBLE;
        for (View view : views) {
            view.setVisibility(visibility);
        }
    }
    public static String twoDecimalPoint(double d) {
        DecimalFormat df = new DecimalFormat("#.##");
        df.setMinimumFractionDigits(2);
        String tax = df.format(d);
        return tax;
    }
    public static String fourDecimalPoint(double d) {
        DecimalFormat df = new DecimalFormat("#.####");
        df.setMinimumFractionDigits(4);
        String tax = df.format(d);
        return tax;
    }

}
