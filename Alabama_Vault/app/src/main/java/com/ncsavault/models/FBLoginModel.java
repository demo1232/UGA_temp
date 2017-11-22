package com.ncsavault.models;

import android.os.AsyncTask;

import com.ncsavault.controllers.AppController;
import com.ncsavault.dto.User;

/**
 * Class used for post the facebook data from server.
 */

public class FBLoginModel extends BaseModel {

    private User mSocialUser;
    @SuppressWarnings("unused")
    private long mUserId;
    private String mResultData;
    @SuppressWarnings("unused")
    private String returnPostData;


    /**
     * Method used for post the facebook data from server.
     * @param user set the facebook user data on server.
     */
    public void fetchData(User user) {
        this.mSocialUser = user;
        FBLoginTask fbLoginModel = new FBLoginTask();
        fbLoginModel.execute();
    }


    /**
     * Async class used for post the data on server.
     */
    private class FBLoginTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {

            String result = "";
            try {

                    result = AppController.getInstance().getServiceManager().getVaultService().
                            postUserData(mSocialUser);

            } catch (Exception e) {
                e.printStackTrace();
            }
            mResultData = result;
            state = STATE_SUCCESS_FETCH_FB_DATA;
            informViews();
            return result;
        }
    }

    /**
     * Method used for get the used id.
     * @return the used id.
     */
    public long getUserId() {
        return mUserId;
    }

    /**
     * Method used for return the result data after post on server.
     * @return the json array.
     */
    public String getResultData() {
        return mResultData;
    }
}
