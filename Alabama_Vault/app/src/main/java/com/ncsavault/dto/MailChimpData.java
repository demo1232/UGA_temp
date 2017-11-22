package com.ncsavault.dto;

/**
 * Class will hold the data of mail chimp.
 */
public class MailChimpData {

    //this is user id
    private long userID;

    //this is registered user status
    private String IsRegisteredUser;

    /**
     * @return Gets the value of userID and returns userID
     */
    public long getUserID() {
        return userID;
    }

    /**
     * Sets the userID
     * You can use getUserID() to get the value of userID
     */
    public void setUserID(long userID) {
        this.userID = userID;
    }

    /**
     * @return Gets the value of IsRegisteredUser and returns IsRegisteredUser
     */
    public String getIsRegisteredUser() {
        return IsRegisteredUser;
    }

    /**
     * Sets the IsRegisteredUser
     * You can use getIsRegisteredUser() to get the value of IsRegisteredUser
     */
    public void setIsRegisteredUser(String isRegisteredUser) {
        IsRegisteredUser = isRegisteredUser;
    }
}
