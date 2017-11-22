package com.ncsavault.models;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.ncsavault.controllers.AppController;
import com.ncsavault.globalconstants.GlobalConstants;
import com.ncsavault.network.GETJsonObjectRequest;

import org.json.JSONObject;

/**
 * Class used for check the validation of login password.
 */

public class LoginPasswordModel extends BaseModel {

    private String mEmailPasswordResult;
    @SuppressWarnings("unused")
    private String mUserName;


    /**
     * Method used for check the validation of email and password.
     * @param email set the email
     * @param password set the password.
     */
    public void loadEmailAndPassData(String email, String password) {

        String url = GlobalConstants.VALIDATE_USER_CREDENTIALS_URL + "?emailID=" + email + "&pass=" + password;

        GETJsonObjectRequest jsonRequest = new GETJsonObjectRequest(url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        state = STATE_SUCCESS_EMAIL_PASSWORD_DATA;
                        mEmailPasswordResult = response.toString();
                        informViews();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        error.printStackTrace();

                    }
                });
        Volley.newRequestQueue(AppController.getInstance().getApplicationContext()).add(jsonRequest);
    }

    /**
     * Method used for get the result of email and password.
     * @return the value of email and password result.
     */
    public String getEmailPasswordResult() {
        return mEmailPasswordResult;
    }

    public int getState() {
        return state;
    }
}
