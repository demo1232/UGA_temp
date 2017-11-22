package com.ncsavault.dto;

import java.io.Serializable;

/**
 * Class will hold the data on our server to related to video
 */
public class VideoDTO implements Serializable {

//this is video id
    private long videoId;

    //this is video name
    private String videoName;

    //this is video short description
    private String videoShortDescription;

    //this is video long description
    private String videoLongDescription;

    //this is video short url
    private String videoShortUrl;

    //this is video long url
    private String videoLongUrl;

    //this is video thumbnail url
    private String videoThumbnailUrl;

    //this is video still url
    private String videoStillUrl;

    //this is video wide still url
    private String videoWideStillUrl;

    //this is video cover url
    private String videoCoverUrl;

    //this is video badge url
    private String videoBadgeUrl;

    //this is video duration
    private long videoDuration;

    //this is video tags
    private String videoTags;

    //this is video favourite status
    private boolean videoIsFavorite;

    //this is video index
    private int videoIndex;

    //this is playlist id
    private long playlistId;

    //this is playlist name
    private String playlistName;

    //this is playlist thumbnail url
    private String playlistThumbnailUrl;

    //this is playlist short description
    private String playlistShortDescription;

    //this is playlist long description
    private String playlistLongDescription;

    //this is playlist tags
    private String playlistTags;

    //this is playlist reference id
    private String playlistReferenceId;

    //this is video social url
    private String videoSocialUrl;

    //this is playlist modified
    private long vedioList_modified;

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
     * @return Gets the value of videoName and returns videoName
     */
    public String getVideoName() {
        return videoName;
    }

    /**
     * Sets the videoName
     * You can use getVideoName() to get the value of videoName
     */
    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    /**
     * @return Gets the value of videoShortDescription and returns videoShortDescription
     */
    public String getVideoShortDescription() {
        return videoShortDescription;
    }

    /**
     * Sets the videoShortDescription
     * You can use getVideoShortDescription() to get the value of videoShortDescription
     */
    public void setVideoShortDescription(String videoShortDescription) {
        this.videoShortDescription = videoShortDescription;
    }

    /**
     * @return Gets the value of videoLongDescription and returns videoLongDescription
     */
    public String getVideoLongDescription() {
        return videoLongDescription;
    }

    /**
     * Sets the videoLongDescription
     * You can use getVideoLongDescription() to get the value of videoLongDescription
     */
    public void setVideoLongDescription(String videoLongDescription) {
        this.videoLongDescription = videoLongDescription;
    }

    /**
     * @return Gets the value of videoShortUrl and returns videoShortUrl
     */
    public String getVideoShortUrl() {
        return videoShortUrl;
    }

    /**
     * Sets the videoShortUrl
     * You can use getVideoShortUrl() to get the value of videoShortUrl
     */
    public void setVideoShortUrl(String videoShortUrl) {
        this.videoShortUrl = videoShortUrl;
    }

    /**
     * @return Gets the value of videoLongUrl and returns videoLongUrl
     */
    public String getVideoLongUrl() {
        return videoLongUrl;
    }

    /**
     * Sets the videoLongUrl
     * You can use getVideoLongUrl() to get the value of videoLongUrl
     */
    public void setVideoLongUrl(String videoLongUrl) {
        this.videoLongUrl = videoLongUrl;
    }

    /**
     * @return Gets the value of videoThumbnailUrl and returns videoThumbnailUrl
     */
    public String getVideoThumbnailUrl() {
        return videoThumbnailUrl;
    }

    /**
     * Sets the videoThumbnailUrl
     * You can use getVideoThumbnailUrl() to get the value of videoThumbnailUrl
     */
    public void setVideoThumbnailUrl(String videoThumbnailUrl) {
        this.videoThumbnailUrl = videoThumbnailUrl;
    }

    /**
     * @return Gets the value of videoStillUrl and returns videoStillUrl
     */
    public String getVideoStillUrl() {
        return videoStillUrl;
    }

    /**
     * Sets the videoStillUrl
     * You can use getVideoStillUrl() to get the value of videoStillUrl
     */
    public void setVideoStillUrl(String videoStillUrl) {
        this.videoStillUrl = videoStillUrl;
    }

    /**
     * @return Gets the value of videoWideStillUrl and returns videoWideStillUrl
     */
    public String getVideoWideStillUrl() {
        return videoWideStillUrl;
    }

    /**
     * Sets the videoWideStillUrl
     * You can use getVideoWideStillUrl() to get the value of videoWideStillUrl
     */
    public void setVideoWideStillUrl(String videoWideStillUrl) {
        this.videoWideStillUrl = videoWideStillUrl;
    }

    /**
     * @return Gets the value of videoCoverUrl and returns videoCoverUrl
     */
    public String getVideoCoverUrl() {
        return videoCoverUrl;
    }

    /**
     * Sets the videoCoverUrl
     * You can use getVideoCoverUrl() to get the value of videoCoverUrl
     */
    public void setVideoCoverUrl(String videoCoverUrl) {
        this.videoCoverUrl = videoCoverUrl;
    }

    /**
     * @return Gets the value of videoBadgeUrl and returns videoBadgeUrl
     */
    public String getVideoBadgeUrl() {
        return videoBadgeUrl;
    }

    /**
     * Sets the videoBadgeUrl
     * You can use getVideoBadgeUrl() to get the value of videoBadgeUrl
     */
    public void setVideoBadgeUrl(String videoBadgeUrl) {
        this.videoBadgeUrl = videoBadgeUrl;
    }

    /**
     * @return Gets the value of videoDuration and returns videoDuration
     */
    public long getVideoDuration() {
        return videoDuration;
    }

    /**
     * Sets the videoDuration
     * You can use getVideoDuration() to get the value of videoDuration
     */
    public void setVideoDuration(long videoDuration) {
        this.videoDuration = videoDuration;
    }

    /**
     * @return Gets the value of videoTags and returns videoTags
     */
    public String getVideoTags() {
        return videoTags;
    }

    /**
     * Sets the videoTags
     * You can use getVideoTags() to get the value of videoTags
     */
    public void setVideoTags(String videoTags) {
        this.videoTags = videoTags;
    }

    /**
     * @return Gets the value of videoIsFavorite and returns videoIsFavorite
     */
    public boolean isVideoIsFavorite() {
        return videoIsFavorite;
    }

    /**
     * Sets the videoIsFavorite
     * You can use getVideoIsFavorite() to get the value of videoIsFavorite
     */
    public void setVideoIsFavorite(boolean videoIsFavorite) {
        this.videoIsFavorite = videoIsFavorite;
    }

    /**
     * @return Gets the value of videoIndex and returns videoIndex
     */
    public int getVideoIndex() {
        return videoIndex;
    }

    /**
     * Sets the videoIndex
     * You can use getVideoIndex() to get the value of videoIndex
     */
    public void setVideoIndex(int videoIndex) {
        this.videoIndex = videoIndex;
    }

    /**
     * @return Gets the value of playlistId and returns playlistId
     */
    public long getPlaylistId() {
        return playlistId;
    }

    /**
     * Sets the playlistId
     * You can use getPlaylistId() to get the value of playlistId
     */
    public void setPlaylistId(long playlistId) {
        this.playlistId = playlistId;
    }

    /**
     * @return Gets the value of playlistName and returns playlistName
     */
    public String getPlaylistName() {
        return playlistName;
    }

    /**
     * Sets the playlistName
     * You can use getPlaylistName() to get the value of playlistName
     */
    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }

    /**
     * @return Gets the value of playlistThumbnailUrl and returns playlistThumbnailUrl
     */
    public String getPlaylistThumbnailUrl() {
        return playlistThumbnailUrl;
    }

    /**
     * Sets the playlistThumbnailUrl
     * You can use getPlaylistThumbnailUrl() to get the value of playlistThumbnailUrl
     */
    public void setPlaylistThumbnailUrl(String playlistThumbnailUrl) {
        this.playlistThumbnailUrl = playlistThumbnailUrl;
    }

    /**
     * @return Gets the value of playlistShortDescription and returns playlistShortDescription
     */
    public String getPlaylistShortDescription() {
        return playlistShortDescription;
    }

    /**
     * Sets the playlistShortDescription
     * You can use getPlaylistShortDescription() to get the value of playlistShortDescription
     */
    public void setPlaylistShortDescription(String playlistShortDescription) {
        this.playlistShortDescription = playlistShortDescription;
    }

    /**
     * @return Gets the value of playlistLongDescription and returns playlistLongDescription
     */
    public String getPlaylistLongDescription() {
        return playlistLongDescription;
    }

    /**
     * Sets the playlistLongDescription
     * You can use getPlaylistLongDescription() to get the value of playlistLongDescription
     */
    public void setPlaylistLongDescription(String playlistLongDescription) {
        this.playlistLongDescription = playlistLongDescription;
    }

    /**
     * @return Gets the value of playlistTags and returns playlistTags
     */
    public String getPlaylistTags() {
        return playlistTags;
    }

    /**
     * Sets the playlistTags
     * You can use getPlaylistTags() to get the value of playlistTags
     */
    public void setPlaylistTags(String playlistTags) {
        this.playlistTags = playlistTags;
    }

    /**
     * @return Gets the value of playlistReferenceId and returns playlistReferenceId
     */
    public String getPlaylistReferenceId() {
        return playlistReferenceId;
    }

    /**
     * Sets the playlistReferenceId
     * You can use getPlaylistReferenceId() to get the value of playlistReferenceId
     */
    public void setPlaylistReferenceId(String playlistReferenceId) {
        this.playlistReferenceId = playlistReferenceId;
    }

    /**
     * @return Gets the value of videoSocialUrl and returns videoSocialUrl
     */
    public String getVideoSocialUrl() {
        return videoSocialUrl;
    }

    /**
     * Sets the videoSocialUrl
     * You can use getVideoSocialUrl() to get the value of videoSocialUrl
     */
    public void setVideoSocialUrl(String videoSocialUrl) {
        this.videoSocialUrl = videoSocialUrl;
    }

    /**
     * @return Gets the value of vedioList_modified and returns vedioList_modified
     */
    public long getVedioList_modified() {
        return vedioList_modified;
    }

    /**
     * Sets the vedioList_modified
     * You can use getVedioList_modified() to get the value of vedioList_modified
     */
    public void setVedioList_modified(long vedioList_modified) {
        this.vedioList_modified = vedioList_modified;
    }
}
