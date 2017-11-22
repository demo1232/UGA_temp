package com.ncsavault.models;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ncsavault.controllers.AppController;
import com.ncsavault.dto.APIResponse;
import com.ncsavault.dto.User;
import java.lang.reflect.Type;

/**
 * Class used for update the user profile detail on server
 * Also we have saved on local data base.
 */

public class UserProfileModel extends BaseModel {

    private User responseUser;
    private String mEmail;
    private long mUserId;
    private Boolean userProfileResult;
    private String fetchingResult;


    /**
     * Method used for update user profile detail on server.
     * @param user set the user detail
     * @param email set email
     * @param userId set the user id.
     */
    public void loadUserProfileData(User user, String email, long userId) {
        this.responseUser = user;
        this.mEmail = email;
        this.mUserId = userId;

        UpdateUserProfileTask updateUserProfileTask = new UpdateUserProfileTask();
        updateUserProfileTask.execute();
    }

    /**
     * Method used for fetch user detail from server.
     * @param email using email
     * @param userId and user id.
     */
    public void loadFetchData(String email, long userId) {
        this.mEmail = email;
        this.mUserId = userId;

        FetchingTask fetchingTask = new FetchingTask();
        fetchingTask.execute();

    }

    /**
     * Async class used for update user detail on server.
     */
    private class UpdateUserProfileTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                String result = AppController.getInstance().getServiceManager().getVaultService().updateUserData(responseUser);
                Log.d("result","Result of user data updating : " + result);
                Gson gson = new Gson();
                Type classType = new TypeToken<APIResponse>() {
                }.getType();
                APIResponse response = gson.fromJson(result.trim(), classType);
                if (response != null) {
                    if (response.getReturnStatus().toLowerCase().equals("success")) {
                        String userJsonData = AppController.getInstance().getServiceManager().getVaultService().getUserData(mUserId, mEmail);
                        if (!userJsonData.isEmpty()) {
                            Type classUserType = new TypeToken<User>() {
                            }.getType();
                            Log.d("User Data : ","User Data : " + userJsonData);
                            User responseUser = gson.fromJson(userJsonData.trim(), classUserType);
                            if (responseUser != null) {
                                userProfileResult = true;
                                state = STATE_SUCCESS;
                                informViews();
                                if (responseUser.getUserID() > 0) {
                                    AppController.getInstance().getModelFacade().getLocalModel().storeUserDataInPreferences(responseUser);
                                    return true;
                                }

                            } else
                                return false;
                        } else
                            return false;
                    } else
                        return false;
                } else {
                    return false;
                }

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

            return true;
        }
    }

    /**
     * Async class used for fetch user detail from server.
     */
    private class FetchingTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {

            String result = AppController.getInstance().getServiceManager().getVaultService().getUserData(mUserId, mEmail);
            fetchingResult = result;
            state = STATE_SUCCESS_FETCH_ALL_DATA;
            informViews();
            return result;
        }
    }

    /**
     * Method used for the response of user detail
     * @return the value true or false
     */
    public Boolean getUserProfileResult() {
        return userProfileResult;
    }

    /**
     * Method used for get the response of user data from server
     * @return the response.
     */
    public String getFetchingResult() {
        return fetchingResult;
    }

}
