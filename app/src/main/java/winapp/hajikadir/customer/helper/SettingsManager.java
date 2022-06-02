package winapp.hajikadir.customer.helper;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

import winapp.hajikadir.customer.model.Address;
import winapp.hajikadir.customer.util.Constants;

/**
 * Created by user on 13-Jul-16.
 */
public class SettingsManager implements Constants {

    private SharedPreferences preferences;
    private Context context;
    public static final String PREF_POSITION = "Position";

    public SettingsManager(Context context) {
        this.context = context;
        preferences = this.context.getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
    }
    public void putWebserviceUrl(String webserviceUrl) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PREF_WEBSERVICE_URL, webserviceUrl);
        editor.commit();
    }
    public String getWebserviceUrl() {
        return preferences.getString(PREF_WEBSERVICE_URL, null);
    }

    public void createUserDetail(String userId, String firstName, String LastName,String address1,String address2,String phoneNo,
                                 String pincode,String customer_id ,String customer_group_id,String fax
            ,   String city,
                                 String country_id,
                                 String zone_id,  String company
    ) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PREF_USER_ID, userId);
        editor.putString(PREF_FIRST_NAME, firstName);
        editor.putString(PREF_LAST_NAME, LastName);
        editor.putString(PREF_ADDRESS1, address1);
        editor.putString(PREF_ADDRESS2, address2);
        editor.putString(PREF_PHONENO, phoneNo);
        editor.putString(PREF_PINCODE, pincode);
        editor.putString(PREF_CUSTOMER_ID, customer_id);
        editor.putString(PREF_CUSTOMER_GROUP_ID, customer_group_id);
        editor.putString(PREF_FAX, fax);
        editor.putString(PREF_CITY, city);
        editor.putString(PREF_COUNTRY_ID, country_id);
        editor.putString(PREF_ZONE_ID, zone_id);
        editor.putString(PREF_COMPANY, company);
        editor.commit();
    }


    public void updateCustomerDetail(Address address) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PREF_FIRST_NAME, address.getFirstName());
        editor.putString(PREF_LAST_NAME, address.getLastName());
        editor.putString(PREF_ADDRESS1, address.getAddress1());
        editor.putString(PREF_ADDRESS2, address.getAddress2());
        editor.putString(PREF_PINCODE, address.getPostalCode());
        editor.putString(PREF_UNITNO, address.getUnitNo());
        editor.commit();
    }

    public void setBtnPosition(String position) {
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString(PREF_POSITION, position);
        editor.commit();
    }
    public String getBtnPosition(){
        return preferences.getString(PREF_POSITION, null);
    }

    public void setToastShow(String show){

        SharedPreferences.Editor editor = preferences.edit();

        editor.putString(PREF_SHOW, show);

        editor.commit();

    }


    public String getToastShow(){

        return preferences.getString(PREF_SHOW, null);
    }

    public String getUserId() {
        return preferences.getString(PREF_USER_ID, null);
    }
    public String getPostalCode() {
        return preferences.getString(PREF_PINCODE, null);
    }
    public HashMap<String, String> getUserInfo() {
        HashMap<String, String> user = new HashMap<String, String>();
        user.put(PREF_USER_ID, preferences.getString(PREF_USER_ID, null));
        user.put(PREF_FIRST_NAME, preferences.getString(PREF_FIRST_NAME, null));
        user.put(PREF_LAST_NAME, preferences.getString(PREF_LAST_NAME, null));
        user.put(PREF_ADDRESS1, preferences.getString(PREF_ADDRESS1, null));
        user.put(PREF_ADDRESS2, preferences.getString(PREF_ADDRESS2, null));
        user.put(PREF_PHONENO, preferences.getString(PREF_PHONENO, null));
        user.put(PREF_PINCODE, preferences.getString(PREF_PINCODE, null));
        user.put(PREF_CUSTOMER_ID, preferences.getString(PREF_CUSTOMER_ID, null));
        user.put(PREF_CUSTOMER_GROUP_ID, preferences.getString(PREF_CUSTOMER_GROUP_ID, null));
        user.put(PREF_FAX, preferences.getString(PREF_FAX, null));

        user.put(PREF_CITY, preferences.getString(PREF_CITY, null));
        user.put(PREF_COUNTRY_ID, preferences.getString(PREF_COUNTRY_ID, null));
        user.put(PREF_ZONE_ID, preferences.getString(PREF_ZONE_ID, null));

        user.put(PREF_COMPANY, preferences.getString(PREF_COMPANY, null));
        user.put(PREF_UNITNO, preferences.getString(PREF_UNITNO, null));


        return user;
    }
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        user.put(PREF_USER_ID, preferences.getString(PREF_USER_ID, null));
        user.put(PREF_USER_NAME, preferences.getString(PREF_USER_NAME, null));
        return user;
    }
    public void clearLoginSession() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(PREF_USER_ID);
        editor.remove(PREF_USER_NAME);
        editor.commit();

    }
    public String getRegisterTokenId(){
        return preferences.getString(PREF_REGISTERED_TOKEN_ID,null);
    }
    public void putRegisterTokenId(String regTokenId){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PREF_REGISTERED_TOKEN_ID,regTokenId);
        editor.commit();
    }

}
