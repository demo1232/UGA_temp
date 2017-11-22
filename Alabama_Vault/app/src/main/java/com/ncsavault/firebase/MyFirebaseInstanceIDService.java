package com.ncsavault.firebase;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.provider.Settings;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.ncsavault.controllers.AppController;
import com.ncsavault.globalconstants.GlobalConstants;

import applicationId.R;


/**
 * Class using for the get the access token from firebase
 * And hit to server send the access token.
 */
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";
    private String refreshedToken;
    private SharedPreferences prefs;
    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        prefs = getSharedPreferences(AppController.getInstance().getApplicationContext().getResources()
                .getString(R.string.pref_package_name), Context.MODE_PRIVATE);
        refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        sendRegistrationToServer();
    }
    // [END refresh_token]

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     */
    private void sendRegistrationToServer() {
        // Add custom implementation, as needed.
        AsyncTask<Void, Void, Void> mRegisterTask = new RegisterTask();
        // execute AsyncTask
        mRegisterTask.execute();
    }

    /**
     * Inner Async task class used for to send the access token to server.
     */
    private class RegisterTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Log.i("MainActivity", "Device tokenId : = "
                        + refreshedToken);
                @SuppressLint("HardwareIds")
                String deviceId = Settings.Secure.getString(getContentResolver(),
                        Settings.Secure.ANDROID_ID);

                String result = AppController.getInstance().getServiceManager().getVaultService().sendPushNotificationRegistration(GlobalConstants.PUSH_REGISTER_URL,
                        refreshedToken, deviceId, true,0);
                if (result != null) {
                    Log.i("MainActivity", "Response from server after registration : = "
                            + result);
                }

                if (result.toLowerCase().contains("success")) {
                    prefs.edit().putBoolean(GlobalConstants.PREF_IS_NOTIFICATION_ALLOW, true).apply();
                    prefs.edit().putBoolean(GlobalConstants.PREF_IS_DEVICE_REGISTERED, true).apply();

                }
            } catch (Exception e) {
                Log.i("GCMIntentService", "Exception onRegistered : = " + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            refreshedToken = null;

        }
    }

}
