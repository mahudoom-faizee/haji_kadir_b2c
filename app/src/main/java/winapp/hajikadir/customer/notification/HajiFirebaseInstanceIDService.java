package winapp.hajikadir.customer.notification;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import winapp.hajikadir.customer.helper.SettingsManager;


public class HajiFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = HajiFirebaseInstanceIDService.class.getSimpleName();
    private SettingsManager settingsManager;
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

       Log.d("TEst: ","Test");

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

       // Log.e(TAG, "sendRegistration: " + refreshedToken);
        // Saving reg id to shared preferences
      //  storeRegIdInPref(refreshedToken);


        // sending reg id to your server
        sendRegistrationToServer(refreshedToken);

        // Notify UI that registration has completed, so the progress indicator can be hidden.

      Intent registrationComplete = new Intent(Config.REGISTRATION_COMPLETE);
        registrationComplete.putExtra("token", refreshedToken);
      LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    private void sendRegistrationToServer(final String token) {
        // sending gcm token to server
        Log.e(TAG, "sendRegistrationToServer: " + token);
    }

    private void storeRegIdInPref(String token) {
        settingsManager = new SettingsManager(getApplicationContext());
        settingsManager.putRegisterTokenId(token);
              }
}

