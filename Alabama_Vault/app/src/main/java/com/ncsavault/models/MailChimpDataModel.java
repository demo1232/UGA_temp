package com.ncsavault.models;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

import com.ncsavault.controllers.AppController;
import com.ncsavault.dto.MailChimpData;
import com.ncsavault.mailchimp.api.lists.ListMethods;
import com.ncsavault.mailchimp.api.lists.MergeFieldListUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Objects;

import applicationId.R;

/**
 * Class used for post the mail chimp data on server.
 */

public class MailChimpDataModel extends BaseModel {

    private MailChimpData mMailChimpData;
    private String mEmail;
    private String mFName;
    private String mLName;


    /**
     * Method used for check on server mail chimp register or not.
     * @param mailChimpData set the value of mail chimp data
     * @param email set the email
     * @param fName set the first name
     * @param lName set the last name
     */
    public void loadMailChimpData(MailChimpData mailChimpData, String email, String fName, String lName) {
        this.mMailChimpData = mailChimpData;
        this.mEmail = email;
        this.mFName = fName;
        this.mLName = lName;
        MailChimpTask mailChimpTask = new MailChimpTask();
        mailChimpTask.execute();
    }

    /**
     * Async class used for check on server mail chimp register or not.
     */
    private class MailChimpTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {

            state = STATE_SUCCESS_MAIL_CHIMP;
            if (mMailChimpData != null && Objects.equals(mMailChimpData.getIsRegisteredUser(), "Y") || mMailChimpData == null) {
                return addToList(mEmail, mFName, mLName);
            } else {
                 AppController.getInstance().getServiceManager().getVaultService()
                        .postMailChimpData(mMailChimpData);
            }
            informViews();
            return false;

        }
    }

    /**
     * This is mail chimp method to check all the information on mail chimp server.
     * @param emailId set the email
     * @param firstName set first name
     * @param lastName set the last name.
     * @return the value true or false.
     */
    @SuppressLint("SimpleDateFormat")
    private boolean addToList(String emailId, String firstName, String lastName) {

        MergeFieldListUtil mergeFields = new MergeFieldListUtil();
        mergeFields.addEmail(emailId);
        try {
            mergeFields.addDateField((new SimpleDateFormat("MM/dd/yyyy")).parse("07/30/2007"));
        } catch (ParseException e1) {
            e1.printStackTrace();
        }
        mergeFields.addField("FNAME", firstName);
        mergeFields.addField("LNAME", lastName);
        mergeFields.addField("PLATFORM", AppController.getInstance().getApplicationContext().getResources().getString(R.string.device_type));
        mergeFields.addField("SCHOOL",AppController.getInstance().getApplicationContext().getResources().getString(R.string.app_school_name));

        // ListMethods listMethods = new ListMethods(getResources().getText(R.string.mc_api_key));
        ListMethods listMethods = new ListMethods(AppController.getInstance().getApplicationContext().getResources().getString(R.string.mail_chimp_api_key));

        boolean mIsSignUpSuccessfully;
        try {
            try {
                mIsSignUpSuccessfully = listMethods.listSubscribe(AppController.getInstance().getApplicationContext().getResources().getString(R.string.mail_chimp_list_id), emailId, mergeFields);
            } catch (Exception e) {
                e.printStackTrace();
                mIsSignUpSuccessfully = false;
                informViews();
                return mIsSignUpSuccessfully;
            }
        } catch (Exception e) {
            Log.e("MailChimp", "Exception subscribing person: " + e.getMessage());
            e.getMessage();

            mIsSignUpSuccessfully = false;
            informViews();
            return mIsSignUpSuccessfully;
        }
        informViews();
        return mIsSignUpSuccessfully;

    }
}
