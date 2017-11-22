package com.ncsavault.models;

import android.os.AsyncTask;

import com.ncsavault.controllers.AppController;

/**
 * Class used to create a change password model
 * And get the data of change change password from server.
 */

public class ChangePasswordModel extends BaseModel {

    private String resultData;
    private String mOldPass;
    private String mNewPass;

    /**
     * Constructor of the class
     * @param oldPass Set the old password
     * @param newPass Set the new password.
     */
    public void loadChangePasswordData(String oldPass, String newPass)
    {
        mOldPass = oldPass;
        mNewPass = newPass;
        ChangePasswordTask changePasswordTask = new ChangePasswordTask();
        changePasswordTask.execute();
        changePasswordTask.isCancelled();
    }


    /**
     * Async class used for get the banner data from server.
     */
    private class ChangePasswordTask extends AsyncTask<Void,Void,String>
    {
        @Override
        protected String doInBackground(Void... params) {
            String result = "";
            try {
                result = AppController.getInstance().getServiceManager().getVaultService().changeUserPassword(AppController.getInstance().
                        getModelFacade().getLocalModel().getUserId(), mOldPass, mNewPass);
            } catch (Exception e) {
                e.printStackTrace();
            }
            resultData = result;
            informViews();
            state = STATE_SUCCESS;
            return result;
        }
    }

    /**
     * Method used for get the value of change password from server.
     * @return the json array.
     */
    public String getResult()
    {
        return resultData;
    }
}
