package com.ncsavault.dto;

/**
 * This class will hold data related to Push Notification
 *         Registration on our server
 */
@SuppressWarnings("unused")
public class NotificationData {

	//this is device id
	private String deviceId;

	//this is register
	private String regId;

    //this is device type
	private String deviceType;

    //this is app name
	private String appName;

    //this is status of response
	private String status;

    //this is app version
	private String appVersion;

    //this is app id
    private int appID;


    //this is user ID
    private long UserId;

    /**
     * @return Gets the value of UserId and returns UserId
     */
    public long getUserId() {
        return UserId;
    }

    /**
     * Sets the UserId
     * You can use getUserId() to get the value of UserId
     */
    public void setUserId(long userId) {
        UserId = userId;
    }


    /**
     * @return Gets the value of deviceId and returns deviceId
     */
    public String getDeviceId() {
        return deviceId;
    }

    /**
     * Sets the deviceId
     * You can use getDeviceId() to get the value of deviceId
     */
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    /**
     * @return Gets the value of regId and returns regId
     */
    public String getRegId() {
        return regId;
    }

    /**
     * Sets the regId
     * You can use getRegId() to get the value of regId
     */
    public void setRegId(String regId) {
        this.regId = regId;
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
     * @return Gets the value of appName and returns appName
     */
    public String getAppName() {
        return appName;
    }

    /**
     * Sets the appName
     * You can use getAppName() to get the value of appName
     */
    public void setAppName(String appName) {
        this.appName = appName;
    }

    /**
     * @return Gets the value of status1 and returns status1
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status1
     * You can use getStatus1() to get the value of status1
     */
    public void setStatus(String status) {
        this.status = status;
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
}
