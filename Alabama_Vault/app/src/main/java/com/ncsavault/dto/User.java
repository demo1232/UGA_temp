package com.ncsavault.dto;

import java.io.Serializable;

/**
 * Class will hold the data of all the information of user.
 */
public class User implements Serializable {

    //this is email id
    private String emailID;

    //this is username
    private String username;

    //this is password
    private String passwd;

    //this is first name
    private String fname ;

    //this is last name
    private String lname ;

    //this is user age
    private int age ;

    //this is user gender
    private String gender ;

    //this is image url
    private String imageurl ;

    //this is app id
    private int appID;

    //this is device type
    private String deviceType;

    //this is app version
    private String appVersion;

    //this is biotext
    private String biotext;

    //this is flag status
    private String flagStatus;

    //this is user id
    private long userID;

    //this is registered user status
    private String IsRegisteredUser;

    //this is social login token
    private String socialLoginToken;

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

    /**
     * @return Gets the value of username and returns username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username
     * You can use getUsername() to get the value of username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return Gets the value of passwd and returns passwd
     */
    public String getPasswd() {
        return passwd;
    }

    /**
     * Sets the passwd
     * You can use getPasswd() to get the value of passwd
     */
    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    /**
     * @return Gets the value of fname and returns fname
     */
    public String getFname() {
        return fname;
    }

    /**
     * Sets the fname
     * You can use getFname() to get the value of fname
     */
    public void setFname(String fname) {
        this.fname = fname;
    }

    /**
     * @return Gets the value of lname and returns lname
     */
    public String getLname() {
        return lname;
    }

    /**
     * Sets the lname
     * You can use getLname() to get the value of lname
     */
    public void setLname(String lname) {
        this.lname = lname;
    }

    /**
     * @return Gets the value of age and returns age
     */
    public int getAge() {
        return age;
    }

    /**
     * Sets the age
     * You can use getAge() to get the value of age
     */
    public void setAge(int age) {
        this.age = age;
    }

    /**
     * @return Gets the value of gender and returns gender
     */
    public String getGender() {
        return gender;
    }

    /**
     * Sets the gender
     * You can use getGender() to get the value of gender
     */
    public void setGender(String gender) {
        this.gender = gender;
    }

    /**
     * @return Gets the value of imageurl and returns imageurl
     */
    public String getImageurl() {
        return imageurl;
    }

    /**
     * Sets the imageurl
     * You can use getImageurl() to get the value of imageurl
     */
    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    /**
     * @return Gets the value of appID and returns appID
     */
    public int getAppID() {
        return appID;
    }

    /**
     * Sets the appID
     * You can use getAppID() to get the value of appID
     */
    public void setAppID(int appID) {
        this.appID = appID;
    }

    /**
     * @return Gets the value of deviceType and returns deviceType
     */
    public String getDeviceType() {
        return deviceType;
    }

    /**
     * Sets the deviceType
     * You can use getDeviceType() to get the value of deviceType
     */
    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    /**
     * @return Gets the value of appVersion and returns appVersion
     */
    public String getAppVersion() {
        return appVersion;
    }

    /**
     * Sets the appVersion
     * You can use getAppVersion() to get the value of appVersion
     */
    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    /**
     * @return Gets the value of biotext and returns biotext
     */
    public String getBiotext() {
        return biotext;
    }

    /**
     * Sets the biotext
     * You can use getBiotext() to get the value of biotext
     */
    public void setBiotext(String biotext) {
        this.biotext = biotext;
    }

    /**
     * @return Gets the value of flagStatus and returns flagStatus
     */
    public String getFlagStatus() {
        return flagStatus;
    }

    /**
     * Sets the flagStatus
     * You can use getFlagStatus() to get the value of flagStatus
     */
    public void setFlagStatus(String flagStatus) {
        this.flagStatus = flagStatus;
    }

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

    /**
     * @return Gets the value of socialLoginToken and returns socialLoginToken
     */
    public String getSocialLoginToken() {
        return socialLoginToken;
    }

    /**
     * Sets the socialLoginToken
     * You can use getSocialLoginToken() to get the value of socialLoginToken
     */
    public void setSocialLoginToken(String socialLoginToken) {
        this.socialLoginToken = socialLoginToken;
    }
}
