package com.ncsavault.models;

import android.app.ProgressDialog;

import com.ncsavault.views.AbstractView;

import java.util.Vector;

/**
 *
 * This class is parent of all class which should extend it while implementing
 * the abstract view class, This class helps to achieve the MVC Design
 * Patterns
 */

public class BaseModel {


    int state = -1;
    public static final int STATE_SUCCESS = 1;
    @SuppressWarnings("unused")
    public static final int STATE_SERVER_ERROR = 2;
    @SuppressWarnings("unused")
    public static final int STATE_NO_INTERNET = 3;
    @SuppressWarnings("unused")
    public static final int STATE_SERVER_TIME_OUT =4;
    @SuppressWarnings("unused")
    public static final int STATE_WRONG_USERNAME_OR_PASSWORD = 5;
    @SuppressWarnings("unused")
    public static final int STATE_UNABLE_TO_UPLOAD_FILE = 6;
    public static final int STATE_RESULT_NOT_FOUND = 7;
    @SuppressWarnings("unused")
    public static final int STATE_EMAIL_ADDRESS_ALREADY_IN_USE =8;
    @SuppressWarnings("unused")
    public static final int STATE_EMAIL_ADDRESS_IS_NOT_REGISTERED =9 ;
    public static final int STATE_SUCCESS_FETCH_ALL_DATA =10 ;
    public static final int STATE_SUCCESS_FETCH_FB_DATA =11 ;
    public static final int STATE_SUCCESS_EMAIL_PASSWORD_DATA =12 ;
    public static final int STATE_SUCCESS_USERNAME_DATA =13 ;
    public static final int STATE_SUCCESS_VAULTUSER_DATA =14 ;
    @SuppressWarnings("unused")
    public static final int STATE_SUCCESS_FRAGMENT_DATA =15 ;
    public static final int STATE_SUCCESS_MAIL_CHIMP = 16;


    @SuppressWarnings("deprecation")
    private ProgressDialog progressDialog;
    /**
     * The reference to an Array/Vector of Abstract View class
     */
    private final Vector<AbstractView> views;

    /**
     * Constructor
     */
    public BaseModel() {
        views = new Vector<>();
    }

    /**
     * Informing to different views registered to this model
     */
    public void informViews() {

        for (int i = 0; i < views.size(); i++) {
            AbstractView abstractView = views.elementAt(i);
            abstractView.update();
        }
    }

    /**
     * To register a view reference later the model can inform view about any changes in model
     * @param abstractView view
     */
    public void registerView(AbstractView abstractView) {
        views.add(abstractView);
    }

    /**
     * To unregister a view so that model can stop informing about any changes to this view
     * @param abstractView view
     */
    public void unRegisterView(AbstractView abstractView) {
        views.removeElement(abstractView);
    }

    public int getState() {
        return state;
    }

    @SuppressWarnings("unused")
    public void setState(int state) {
        this.state = state;
    }

    @SuppressWarnings("deprecation")
    public ProgressDialog getProgressDialog() {
        return progressDialog;
    }

    @SuppressWarnings("deprecation")
    public void setProgressDialog(ProgressDialog progressDialog) {
        this.progressDialog = progressDialog;
    }

}
