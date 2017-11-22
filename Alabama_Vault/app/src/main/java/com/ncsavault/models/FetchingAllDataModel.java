package com.ncsavault.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ncsavault.controllers.AppController;
import com.ncsavault.dto.User;
import com.ncsavault.globalconstants.GlobalConstants;
import com.ncsavault.utils.Utils;

import java.lang.reflect.Type;

import applicationId.R;

/**
 * Method used for fetched the all user detail from server.
 */

public class FetchingAllDataModel extends BaseModel {

    private final Context context;
    private boolean aBoolean;
    private User responseUserData;

    /**
     * Constructor of the class
     */
    public FetchingAllDataModel() {

        context = AppController.getInstance().getApplicationContext();
    }

    /**
     * Method used for get the all user detail from server.
     */
    public void fetchData()
    {
        FetchingDataTask fetchingDataTask =  new FetchingDataTask();
        fetchingDataTask.execute();
    }

    public int getState() {
        return state;
    }

    /**
     * Async class used for get the data from server.
     */
    private class FetchingDataTask extends AsyncTask<Void,Void,Boolean>
    {
        @Override
        protected Boolean doInBackground(Void... params) {
            boolean status;
            String userJsonData;
            try {
                SharedPreferences pref = context.getSharedPreferences(context.getResources().getString(R.string.pref_package_name), Context.MODE_PRIVATE);
                final long userId = pref.getLong(GlobalConstants.PREF_VAULT_USER_ID_LONG, 0);
                final String email = pref.getString(GlobalConstants.PREF_VAULT_USER_EMAIL, "");
                userJsonData = AppController.getInstance().getServiceManager().getVaultService().getUserData(userId, email);

                if (userJsonData != null) {
                    if (!userJsonData.isEmpty()) {
                        Gson gson = new Gson();
                        Type classType = new TypeToken<User>() {
                        }.getType();
                        Log.d("User Data : ","User Data : " + userJsonData);
                        User responseUser = gson.fromJson(userJsonData.trim(), classType);
                        responseUserData = responseUser;
                        if (responseUser != null) {
                            if (responseUser.getUserID() > 0) {
                                AppController.getInstance().getModelFacade().getLocalModel().storeUserDataInPreferences(responseUser);
                            }
                        }
                    }
                }

                status =  Utils.loadDataFromServer(context);
                state =STATE_SUCCESS_FETCH_ALL_DATA;
            } catch (Exception e) {
                e.printStackTrace();
                status = false;
            }
            aBoolean = status;
            informViews();
            return status;
        }
    }

    /**
     * Method used for check the status of the data
     * @return the value true and false.
     */
    public Boolean getABoolean()
    {
        return aBoolean;
    }

    /**
     * Method used for get all user data
     * @return get the user data.
     */
    public User getResponseUserData()
    {
        return responseUserData;
    }

  }
