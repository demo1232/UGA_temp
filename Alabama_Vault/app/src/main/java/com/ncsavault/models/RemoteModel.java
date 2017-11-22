package com.ncsavault.models;

/**
 * Class used for create a object of all the model class.
 */
public class RemoteModel implements IModel {

    /**
     * Instance of Banner Model data
     */
    private BannerDataModel bannerDataModel;
    /**
     * Instance of LoginEmail Model
     */
    private LoginEmailModel loginEmailModel;

    /**
     * Instance of Fetch all data Model
     */
    private FetchingAllDataModel fetchingAllDataModel;

    /**
     * Instance of FB LOGIN Data
     */
    private FBLoginModel fbLoginModel;

    /**
     * Instance of Mail chimp data.
     */
    private MailChimpDataModel mailChimpDataModel;

    /**
     * Instance of vault user data model
     */
    private UserDataModel userDataModel;

    /**
     * Instance of user profile data model
     */
    private UserProfileModel userProfileModel;

    /**
     * Instance of change password screen model
     */
    private ChangePasswordModel changePasswordModel;

    /**
     * Instance of contact screen model
     */
    private CreateTaskOnAsanaModel createTaskOnAsanaModel;

    /**
     * Instance of video data task model
     */
    private VideoDataTaskModel videoDataTaskModel;

    /**
     * Instance of login password model
     */
    private LoginPasswordModel loginPasswordModel;

    @Override
    public void initialize() {

        // homeModel = new HomeModel();
        bannerDataModel = new BannerDataModel();
        loginEmailModel = new LoginEmailModel();
        fetchingAllDataModel = new FetchingAllDataModel();
        fbLoginModel = new FBLoginModel();
        mailChimpDataModel = new MailChimpDataModel();
        userDataModel = new UserDataModel();
        userProfileModel = new UserProfileModel();
        changePasswordModel = new ChangePasswordModel();
        createTaskOnAsanaModel = new CreateTaskOnAsanaModel();
        videoDataTaskModel = new VideoDataTaskModel();
        loginPasswordModel = new LoginPasswordModel();
    }

    @Override
    public void destroy() {

    }

    public LoginEmailModel getLoginEmailModel() {
        return loginEmailModel;
    }

    public FetchingAllDataModel getFetchingAllDataModel() {
        return fetchingAllDataModel;
    }

    public FBLoginModel getFbLoginModel() {
        return fbLoginModel;
    }

    public BannerDataModel getBannerDataModel() {
        return bannerDataModel;
    }

    public MailChimpDataModel getMailChimpDataModel() {
        return mailChimpDataModel;
    }

    public UserDataModel getUserDataModel() {
        return userDataModel;
    }

    public UserProfileModel getUserProfileModel() {
        return userProfileModel;
    }

    public ChangePasswordModel getChangePasswordModel() {
        return changePasswordModel;
    }

    public CreateTaskOnAsanaModel getCreateTaskOnAsanaModel() {
        return createTaskOnAsanaModel;
    }

    public VideoDataTaskModel getVideoDataTaskModel() {
        return videoDataTaskModel;
    }

    public LoginPasswordModel getLoginPasswordModel() {
        return loginPasswordModel;
    }


}
