package com.ncsavault.dto;

/**
 * Class will hold the data for api response.
 */
@SuppressWarnings("unused")
public class APIResponse {
    //this is return status
    private String returnStatus;
    //this is user id
    private long UserID;
    //this is verfication code
    private String VerficationCode;
    //this is email id
    private String emailID;

    /**
     * @return Gets the value of returnStatus and returns returnStatus
     */
    public String getReturnStatus() {
        return returnStatus;
    }

    /**
     * Sets the returnStatus
     * You can use getReturnStatus() to get the value of returnStatus
     */
    public void setReturnStatus(String returnStatus) {
        this.returnStatus = returnStatus;
    }

    /**
     * @return Gets the value of UserID and returns UserID
     */
    public long getUserID() {
        return UserID;
    }

    /**
     * Sets the UserID
     * You can use getUserID() to get the value of UserID
     */
    public void setUserID(long userID) {
        UserID = userID;
    }

    /**
     * @return Gets the value of VerficationCode and returns VerficationCode
     */
    public String getVerficationCode() {
        return VerficationCode;
    }

    /**
     * Sets the VerficationCode
     * You can use getVerficationCode() to get the value of VerficationCode
     */
    public void setVerficationCode(String verficationCode) {
        VerficationCode = verficationCode;
    }

    /**
     * @return Gets the value of emailID and returns emailID
     */
    public String getEmailID() {
        return emailID;
    }

    /**
     * Sets the emailID
     * You can use getEmailID() to get the value of emailID
     */
    public void setEmailID(String emailID) {
        this.emailID = emailID;
    }
}