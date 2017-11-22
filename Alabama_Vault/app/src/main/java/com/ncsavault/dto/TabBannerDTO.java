package com.ncsavault.dto;

import java.io.Serializable;

/**
 * Class will hold the data on our server for tab banner.
 */
public class TabBannerDTO implements Serializable {

    //this is tab id
    private long tabId;

    //this is tab name
    private String tabName;

    //this is tab index position
    private long tabIndexPosition;

    //this is tab display type
    private String tabDisplayType;

    //this is tab keyword
    private String tabKeyword;

    //this is tab created
    private long tabCreated;

    //this is tab modified
    private long tabModified;

    //this is tab data created
    private long tabDataCreated;

    //this is tab data modified
    private long tabDataModified;

    //this is tab banner id
    private long tabBannerId;

    //this is tab banner name
    private String tabBannerName;

    //this is tab banner active
    private boolean isBannerActive;

    //this is hyperlink status
    private boolean isHyperlinkActive;

    //this is banner action url
    private String bannerActionURL;

    //this is banner created
    private long bannerCreated;

    //this is banner modified
    private long bannerModified;

    //this is banner url
    private String bannerURL;

    /**
     * @return Gets the value of tabId and returns tabId
     */
    public long getTabId() {
        return tabId;
    }

    /**
     * Sets the tabId
     * You can use getTabId() to get the value of tabId
     */
    public void setTabId(long tabId) {
        this.tabId = tabId;
    }

    /**
     * @return Gets the value of tabName and returns tabName
     */
    public String getTabName() {
        return tabName;
    }

    /**
     * Sets the tabName
     * You can use getTabName() to get the value of tabName
     */
    public void setTabName(String tabName) {
        this.tabName = tabName;
    }

    /**
     * @return Gets the value of tabIndexPosition and returns tabIndexPosition
     */
    public long getTabIndexPosition() {
        return tabIndexPosition;
    }

    /**
     * Sets the tabIndexPosition
     * You can use getTabIndexPosition() to get the value of tabIndexPosition
     */
    public void setTabIndexPosition(long tabIndexPosition) {
        this.tabIndexPosition = tabIndexPosition;
    }

    /**
     * @return Gets the value of tabDisplayType and returns tabDisplayType
     */
    public String getTabDisplayType() {
        return tabDisplayType;
    }

    /**
     * Sets the tabDisplayType
     * You can use getTabDisplayType() to get the value of tabDisplayType
     */
    public void setTabDisplayType(String tabDisplayType) {
        this.tabDisplayType = tabDisplayType;
    }

    /**
     * @return Gets the value of tabKeyword and returns tabKeyword
     */
    public String getTabKeyword() {
        return tabKeyword;
    }

    /**
     * Sets the tabKeyword
     * You can use getTabKeyword() to get the value of tabKeyword
     */
    public void setTabKeyword(String tabKeyword) {
        this.tabKeyword = tabKeyword;
    }

    /**
     * @return Gets the value of tabCreated and returns tabCreated
     */
    public long getTabCreated() {
        return tabCreated;
    }

    /**
     * Sets the tabCreated
     * You can use getTabCreated() to get the value of tabCreated
     */
    public void setTabCreated(long tabCreated) {
        this.tabCreated = tabCreated;
    }

    /**
     * @return Gets the value of tabModified and returns tabModified
     */
    public long getTabModified() {
        return tabModified;
    }

    /**
     * Sets the tabModified
     * You can use getTabModified() to get the value of tabModified
     */
    public void setTabModified(long tabModified) {
        this.tabModified = tabModified;
    }

    /**
     * @return Gets the value of tabDataCreated and returns tabDataCreated
     */
    public long getTabDataCreated() {
        return tabDataCreated;
    }

    /**
     * Sets the tabDataCreated
     * You can use getTabDataCreated() to get the value of tabDataCreated
     */
    public void setTabDataCreated(long tabDataCreated) {
        this.tabDataCreated = tabDataCreated;
    }

    /**
     * @return Gets the value of tabDataModified and returns tabDataModified
     */
    public long getTabDataModified() {
        return tabDataModified;
    }

    /**
     * Sets the tabDataModified
     * You can use getTabDataModified() to get the value of tabDataModified
     */
    public void setTabDataModified(long tabDataModified) {
        this.tabDataModified = tabDataModified;
    }

    /**
     * @return Gets the value of tabBannerId and returns tabBannerId
     */
    public long getTabBannerId() {
        return tabBannerId;
    }

    /**
     * Sets the tabBannerId
     * You can use getTabBannerId() to get the value of tabBannerId
     */
    public void setTabBannerId(long tabBannerId) {
        this.tabBannerId = tabBannerId;
    }

    /**
     * @return Gets the value of tabBannerName and returns tabBannerName
     */
    public String getTabBannerName() {
        return tabBannerName;
    }

    /**
     * Sets the tabBannerName
     * You can use getTabBannerName() to get the value of tabBannerName
     */
    public void setTabBannerName(String tabBannerName) {
        this.tabBannerName = tabBannerName;
    }

    /**
     * @return Gets the value of isBannerActive and returns isBannerActive
     */
    public boolean isBannerActive() {
        return isBannerActive;
    }

    /**
     * Sets the isBannerActive
     * You can use getBannerActive() to get the value of isBannerActive
     */
    public void setIsBannerActive(boolean bannerActive) {
        isBannerActive = bannerActive;
    }

    /**
     * @return Gets the value of isHyperlinkActive and returns isHyperlinkActive
     */
    public boolean isHyperlinkActive() {
        return isHyperlinkActive;
    }

    /**
     * Sets the isHyperlinkActive
     * You can use getHyperlinkActive() to get the value of isHyperlinkActive
     */
    public void setIsHyperlinkActive(boolean hyperlinkActive) {
        isHyperlinkActive = hyperlinkActive;
    }

    /**
     * @return Gets the value of bannerActionURL and returns bannerActionURL
     */
    public String getBannerActionURL() {
        return bannerActionURL;
    }

    /**
     * Sets the bannerActionURL
     * You can use getBannerActionURL() to get the value of bannerActionURL
     */
    public void setBannerActionURL(String bannerActionURL) {
        this.bannerActionURL = bannerActionURL;
    }

    /**
     * @return Gets the value of bannerCreated and returns bannerCreated
     */
    public long getBannerCreated() {
        return bannerCreated;
    }

    /**
     * Sets the bannerCreated
     * You can use getBannerCreated() to get the value of bannerCreated
     */
    public void setBannerCreated(long bannerCreated) {
        this.bannerCreated = bannerCreated;
    }

    /**
     * @return Gets the value of bannerModified and returns bannerModified
     */
    public long getBannerModified() {
        return bannerModified;
    }

    /**
     * Sets the bannerModified
     * You can use getBannerModified() to get the value of bannerModified
     */
    public void setBannerModified(long bannerModified) {
        this.bannerModified = bannerModified;
    }

    /**
     * @return Gets the value of bannerURL and returns bannerURL
     */
    public String getBannerURL() {
        return bannerURL;
    }

    /**
     * Sets the bannerURL
     * You can use getBannerURL() to get the value of bannerURL
     */
    public void setBannerURL(String bannerURL) {
        this.bannerURL = bannerURL;
    }



}