package winapp.hajikadir.customer.helper;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

import winapp.hajikadir.customer.util.Constants;

/**
 * Created by user on 03-Feb-17.
 */

public class SessionManager  implements Constants {
    SharedPreferences preferences;
    Context context;

    public SessionManager(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
    }
    public void putCurrentChoice(String position) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PREF_CURRENT_CHOICE, position);
        editor.commit();
    }
    public String getCurrentChoice() {
        return preferences.getString(PREF_CURRENT_CHOICE, null);
    }
    public void putBranchDisplayName(String name) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PREF_BRANCH_DISPLAY_NAME, name);
        editor.commit();
    }
    public String getBranchDisplayName() {
        return preferences.getString(PREF_BRANCH_DISPLAY_NAME, null);
    }



   /* public void putBranch(String id,String name,String pincode) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PREF_BRANCH_ID, id);
        editor.putString(PREF_BRANCH_NAME, name);
        editor.putString(PREF_BRANCH_PINCODE, pincode);
        editor.commit();
    }*/


    public void putBranch(String id,String name,String address1,String address2,String phoneNo,String country,String pincode,String latitude,String longitude) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PREF_BRANCH_ID, id);
        editor.putString(PREF_BRANCH_NAME, name);
        editor.putString(PREF_BRANCH_ADDRESS1, address1);
        editor.putString(PREF_BRANCH_ADDRESS2, address2);
        editor.putString(PREF_BRANCH_PHONENO, phoneNo);
        editor.putString(PREF_BRANCH_PINCODE, pincode);
        editor.putString(PREF_BRANCH_COUNTRY, country);
        editor.putString(PREF_BRANCH_LATITUDE,latitude);
        editor.putString(PREF_BRANCH_LONGITUDE,longitude);
        editor.commit();
    }

    public HashMap<String, String> geBranchInfo() {
        HashMap<String, String> user = new HashMap<String, String>();
        user.put(PREF_BRANCH_ID, preferences.getString(PREF_BRANCH_ID, null));
        user.put(PREF_BRANCH_NAME, preferences.getString(PREF_BRANCH_NAME, null));
        user.put(PREF_BRANCH_ADDRESS1, preferences.getString(PREF_BRANCH_ADDRESS1, null));
        user.put(PREF_BRANCH_ADDRESS2, preferences.getString(PREF_BRANCH_ADDRESS2, null));
        user.put(PREF_BRANCH_PHONENO, preferences.getString(PREF_BRANCH_PHONENO, null));
        user.put(PREF_BRANCH_PINCODE, preferences.getString(PREF_BRANCH_PINCODE, null));
        user.put(PREF_BRANCH_COUNTRY, preferences.getString(PREF_BRANCH_COUNTRY, null));
        user.put(PREF_BRANCH_LATITUDE,preferences.getString(PREF_BRANCH_LATITUDE,null));
        user.put(PREF_BRANCH_LONGITUDE,preferences.getString(PREF_BRANCH_LONGITUDE,null));
        return user;
    }


    public String getBranchId() {
        return preferences.getString(PREF_BRANCH_ID, null);
    }

    public String getBranchId(String pref_branch_id, Object o) {
        return preferences.getString(PREF_BRANCH_ID, null);
    }

    public String getBranchPincode() {
        return preferences.getString(PREF_BRANCH_PINCODE, null);
    }
    public void createLoginSession(String userId, String password,String name) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(PREF_IS_LOGGED_IN, true);
        editor.putString(PREF_SESSION_USER_ID, userId);
        editor.putString(PREF_SESSION_PASSWORD, password);
        editor.putString(PREF_SESSION_USER_NAME, name);
        editor.commit();
    }
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        user.put(PREF_SESSION_USER_ID, preferences.getString(PREF_SESSION_USER_ID, null));
        user.put(PREF_SESSION_PASSWORD, preferences.getString(PREF_SESSION_PASSWORD, null));
        user.put(PREF_SESSION_USER_NAME, preferences.getString(PREF_SESSION_USER_NAME, null));
        return user;
    }
    public void clearLoginSession() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(PREF_IS_LOGGED_IN);
        editor.remove(PREF_SESSION_USER_ID);
        editor.remove(PREF_SESSION_PASSWORD);
        editor.remove(PREF_SESSION_USER_NAME);
        editor.commit();

    }
    public boolean isLoggedIn() {
        return preferences.getBoolean(PREF_IS_LOGGED_IN, false);
    }
}