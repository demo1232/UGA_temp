package com.ncsavault.models;

import android.os.AsyncTask;

import com.ncsavault.controllers.AppController;

/**
 * Class used for check the validation of login email.
 */

public class LoginEmailModel extends BaseModel {

    private String mLoginResult;
    private String mEmail;

    /**
     * Method used for load login email credential.
     * @param email set the email id.
     */
    public void loadLoginData(String email) {
        this.mEmail = email;
        LoginTask loginTask = new LoginTask();
        loginTask.execute();
    }


    /**
     * Async class used for check the validation on server.
     */
    private class LoginTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {

            String validateValue = AppController.getInstance().getServiceManager().
                    getVaultService().validateEmail(mEmail);
            mLoginResult = validateValue;
            state = STATE_SUCCESS;
            informViews();
            return validateValue;
        }
    }

    /**
     * Method used for get the login validation result.
     * @return the value of result.
     */
    public String getLoginResult() {
        return mLoginResult;
    }


    public int getState() {
        return state;
    }
}
