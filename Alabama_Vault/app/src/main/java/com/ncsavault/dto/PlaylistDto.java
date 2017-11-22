package com.ncsavault.dto;

import java.io.Serializable;

/**
 * Class will hold the data of Playlist screen and show the playlist item.
 */

public class PlaylistDto implements Serializable {

    //this is play list id
    private long playlistId;

    //this is play list name
    private String playlistName;

    //this is play list type
    private String playlistType;

    //this is play list reference id
    private String playlistReferenceId;

    //this is play list thumbnail url
    private String playlistThumbnailUrl;

    //this is play list short description
    private String playlistShortDescription;

    //this is play list long description
    private String playlistLongDescription;

    //this is play list tags
    private String playlistTags;

    //this is category id
    private long CategoriesId;

    //this is play list modified
    private long playlist_modified;


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
     * @return Gets the value of playlistType and returns playlistType
     */
    public String getPlaylistType() {
        return playlistType;
    }

    /**
     * Sets the playlistType
     * You can use getPlaylistType() to get the value of playlistType
     */
    public void setPlaylistType(String playlistType) {
        this.playlistType = playlistType;
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
     * @return Gets the value of CategoriesId and returns CategoriesId
     */
    public long getCategoriesId() {
        return CategoriesId;
    }

    /**
     * Sets the CategoriesId
     * You can use getCategoriesId() to get the value of CategoriesId
     */
    public void setCategoriesId(long categoriesId) {
        CategoriesId = categoriesId;
    }

    /**
     * @return Gets the value of playlist_modified and returns playlist_modified
     */
    public long getPlaylist_modified() {
        return playlist_modified;
    }

    /**
     * Sets the playlist_modified
     * You can use getPlaylist_modified() to get the value of playlist_modified
     */
    public void setPlaylist_modified(long playlist_modified) {
        this.playlist_modified = playlist_modified;
    }
}
