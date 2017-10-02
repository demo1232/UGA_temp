package com.ncsavault.models;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.ncsavault.controllers.AppController;
import com.ncsavault.dto.User;
import com.ncsavault.globalconstants.GlobalConstants;
import com.ncsavault.network.POSTJsonRequest;
import com.ncsavault.utils.Utils;
import com.ncsavault.views.UploadPhotoActivity;

import org.json.JSONObject;

/**
 * Created by gauravkumar.singh on 12/23/2016.
 */

public class UserDataModel extends BaseModel {

    private String mVaultUserResult;
    private User mVaultUser;
    public void loadVaultData(User user) {
        this.mVaultUser = user;
        UserDataTask userDataTask = new UserDataTask();
        userDataTask.execute();
    }

    public void loadVaultData1(User user) {
        String url = GlobalConstants.POST_USER_DATA_URL;
        String postStr = new Gson().toJson(user);
        POSTJsonRequest jsonRequest = new POSTJsonRequest(url, postStr,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            state = STATE_SUCCESS_VAULTUSER_DATA;
                            mVaultUserResult = response.toString();
                            informViews();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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

    private class UserDataTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            String result = "";
            try {
                result = AppController.getInstance().getServiceManager().getVaultService().postUserData(mVaultUser);
            } catch (Exception e) {
                e.printStackTrace();
            }
            state = STATE_SUCCESS_VAULTUSER_DATA;
            mVaultUserResult = result;
            informViews();
            return result;
        }
    }



    public String getmVaultUserResult() {
        return mVaultUserResult;
    }
}
