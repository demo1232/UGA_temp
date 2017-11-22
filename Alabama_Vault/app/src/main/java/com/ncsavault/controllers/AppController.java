package com.ncsavault.controllers;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.ncsavault.app.AndroidApplication;
import com.ncsavault.defines.AppDefines;
import com.ncsavault.factory.ViewFactory;
import com.ncsavault.models.ModelFacade;
import com.ncsavault.service.AndroidServiceContext;
import com.ncsavault.service.ServiceManager;
import com.ncsavault.serviceimpl.AbstractServiceManagerImpl;

/**
 * Singleton Class and to handle the all activity class's
 */
public class AppController {

    //singleton instance
    @SuppressLint("StaticFieldLeak")
    private static AppController instance;

    //ModelFacade Reference
    private final ModelFacade modelFacade;

    //Reference to android Application class object
    private AndroidApplication application;
    private ServiceManager serviceManager;


    /**
     * Private constructor to achieve singleton design patterns
     */
    private AppController() {
        modelFacade = new ModelFacade();

        init();
    }

    public void setCurrentActivity(Activity currentActivity) {
        Log.d("current activity", "current activity" + currentActivity);
    }


    /**
     * To get singleton reference of AppController class
     *
     * @return the reference of AppController class
     */
    public static AppController getInstance() {
        if (instance == null) {
            synchronized (AppController.class) {
                if (instance == null) {
                    instance = new AppController();
                }
            }

        }
        return instance;
    }

    /**
     * This function should called only once.
     * Initialize the required objects
     */
    public void initialize() {

        modelFacade.initialize();
    }


    /**
     * Set Android application reference
     *
     * @param application Set the reference of AndroidApplication
     */
    public void setApplication(AndroidApplication application) {
        this.application = application;
    }

    /**
     * Get the Android application reference
     *
     * @return Get the reference of AndroidApplication
     */
    public AndroidApplication getApplication() {
        return application;
    }

    /**
     * To get the application context reference to be used in different position
     *
     * @return Get the application context reference
     */
    public Context getApplicationContext() {

        return application.getApplicationContext();
    }

    /**
     * To get the reference of the ModelFacade Class
     *
     * @return The reference of the ModelFacade Class
     */
    public ModelFacade getModelFacade() {
        return modelFacade;
    }


    /**
     * HandleEvent function to manage events inside the application.
     * this functions should be used for various background events and activity launch activity
     *
     * @param eventID To manage the events inside the application.
     */
    public void handleEvent(int eventID) {
        handleEvent(eventID, null);
    }

    /**
     * HandleEvent function to manage events inside the application.
     * this functions should be used for various background events and activity launch activity
     *
     * @param eventID      eventID to process the particular events
     * @param eventObjects eventObjects to be passed to next activity
     */
    public void handleEvent(int eventID, Object eventObjects) {

        Class className;
        switch (eventID) {

            case AppDefines.EVENT_ID_LOGIN_SCREEN: {
                //to launch login screen.
                className = ViewFactory.getInstance().getActivityClass(ViewFactory.LOGIN_SCREEN);
                ActivityUIController.getInstance().launchActivity(className, eventID, eventObjects);
            }
            break;

            case AppDefines.EVENT_ID_LOGIN_PASSWORD_SCREEN: {
                //to launch password screen.
                className = ViewFactory.getInstance().getActivityClass(ViewFactory.LOGIN_PASSWORD_SCREEN);
                ActivityUIController.getInstance().launchActivity(className, eventID, eventObjects);
            }
            break;

            case AppDefines.EVENT_ID_MAIN_SCREEN: {
                // to launch main screen
                className = ViewFactory.getInstance().getActivityClass(ViewFactory.MAIN_SCREEN);
                ActivityUIController.getInstance().launchActivity(className, eventID, eventObjects);
            }
            break;

            case AppDefines.EVENT_ID_UPLOAD_PHOTO_SCREEN: {
                // to launch upload photo screen
                className = ViewFactory.getInstance().getActivityClass(ViewFactory.UPLOAD_PHOTO_SCREEN);
                ActivityUIController.getInstance().launchActivity(className, eventID, eventObjects);
            }
            break;

            case AppDefines.EVENT_ID_USER_PROFILE_SCREEN: {
                // to launch user profile screen
                className = ViewFactory.getInstance().getActivityClass(ViewFactory.USER_PROFILE_SCREEN);
                ActivityUIController.getInstance().launchActivity(className, eventID, eventObjects);
            }
            break;

            case AppDefines.EVENT_ID_CHANGE_PASSWORD_SCREEN: {
                // to launch change password screen
                className = ViewFactory.getInstance().getActivityClass(ViewFactory.CHANGE_PASSWORD_SCREEN);
                ActivityUIController.getInstance().launchActivity(className, eventID, eventObjects);
            }
            break;

            case AppDefines.EVENT_ID_CONTACT_SCREEN: {
                // to launch change contact screen
                className = ViewFactory.getInstance().getActivityClass(ViewFactory.CONTACT_SCREEN);
                ActivityUIController.getInstance().launchActivity(className, eventID, eventObjects);
            }
            break;

            case AppDefines.EVENT_ID_VIDEO_INFO_SCREEN: {
                // to launch video info screen
                className = ViewFactory.getInstance().getActivityClass(ViewFactory.VIDEO_INFO_SCREEN);
                ActivityUIController.getInstance().launchActivity(className, eventID, eventObjects);
            }
            break;

            case AppDefines.EVENT_ID_FORGOT_PASSWORD_SCREEN: {
                // to launch forgot password screen
                className = ViewFactory.getInstance().getActivityClass(ViewFactory.FORGOT_PASSWORD_SCREEN);
                ActivityUIController.getInstance().launchActivity(className, eventID, eventObjects);
            }
            break;

            case AppDefines.EVENT_ID_REGISTRATION_SCREEN: {
                // to launch registration screen
                className = ViewFactory.getInstance().getActivityClass(ViewFactory.REGISTRATION_SCREEN);
                ActivityUIController.getInstance().launchActivity(className, eventID, eventObjects);
            }
            break;

            //NEW UI SCREEN

            case AppDefines.EVENT_ID_HOME_SCREEN: {
                // to launch registration screen
                className = ViewFactory.getInstance().getActivityClass(ViewFactory.HOME_SCREEN);
                ActivityUIController.getInstance().launchActivity(className, eventID, eventObjects);
            }
            break;


        }

    }

    public ServiceManager getServiceManager() {
        while (serviceManager == null) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        synchronized (this) {
            return serviceManager;
        }
    }

    /**
     * Initialize the request manager and the image cache
     */
    private void init() {

        new AsyncTask<Void, Void, Void>() {

            protected void onPreExecute() {
            }

            @Override
            protected Void doInBackground(Void... params) {
                synchronized (AppController.this) {
                    try {
                        AndroidServiceContext serviceContext = null;
                        serviceManager = new AbstractServiceManagerImpl(
                                serviceContext);

                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
            }

        }.execute(null, null, null);
    }


}
