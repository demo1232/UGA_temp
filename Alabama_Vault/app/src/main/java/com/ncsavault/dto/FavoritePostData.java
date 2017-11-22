package com.ncsavault.dto;

/**
 * Class will hold the data for Favorite tab.
 */
@SuppressWarnings("unused")
public class FavoritePostData {

    //this is user id
    private long userid;

    //this is video id
    private long videoId;

    //this is play list id
    private long playListId;

    //this is favourite status
    private boolean favStatus;

    /**
     * @return Gets the value of userid and returns userid
     */
    public long getUserid() {
        return userid;
    }

    /**
     * Sets the userid
     * You can use getUserid() to get the value of userid
     */
    public void setUserid(long userid) {
        this.userid = userid;
    }

    /**
     * @return Gets the value of videoId and returns videoId
     */
    public long getVideoId() {
        return videoId;
    }

    /**
     * Sets the videoId
     * You can use getVideoId() to get the value of videoId
     */
    public void setVideoId(long videoId) {
        this.videoId = videoId;
    }

    /**
     * @return Gets the value of playListId and returns playListId
     */
    public long getPlayListId() {
        return playListId;
    }

    /**
     * Sets the playListId
     * You can use getPlayListId() to get the value of playListId
     */
    public void setPlayListId(long playListId) {
        this.playListId = playListId;
    }

    /**
     * @return Gets the value of favStatus and returns favStatus
     */
    public boolean isFavStatus() {
        return favStatus;
    }

    /**
     * Sets the favStatus
     * You can use getFavStatus() to get the value of favStatus
     */
    public void setFavStatus(boolean favStatus) {
        this.favStatus = favStatus;
    }
}
