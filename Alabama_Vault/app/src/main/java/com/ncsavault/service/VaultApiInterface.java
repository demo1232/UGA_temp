package com.ncsavault.service;



import com.ncsavault.dto.CategoriesTabDao;
import com.ncsavault.dto.MailChimpData;

import com.ncsavault.dto.PlaylistDto;
import com.ncsavault.dto.TabBannerDTO;
import com.ncsavault.dto.User;
import com.ncsavault.dto.VideoDTO;

import java.util.ArrayList;

/**
 * Interface used for call all the web api
 * So we have declared all the method here and implemented in VaultApiCallImpl.
 */

public interface VaultApiInterface {

    ArrayList<VideoDTO> getVideosListFromServer(String url) throws BusinessException;
    VideoDTO getVideosDataFromServer(String url) throws BusinessException;
    String postFavoriteStatus(long userId, long videoId, long playListId, boolean status) throws BusinessException;
    @SuppressWarnings("unused")
    String postSharingInfo(String videoId) throws BusinessException;
    String validateEmail(String emailId) throws BusinessException;
    @SuppressWarnings("unused")
    String validateUsername(String userName) throws BusinessException;
    String postUserData(User user) throws BusinessException;
    @SuppressWarnings("unused")
    String validateUserCredentials(String emailId, String password) throws BusinessException;
    String getUserData(long userId, String emailId) throws BusinessException;
    String updateUserData(User updatedUser) throws BusinessException;

    @SuppressWarnings("unused")
    String validateSocialLogin(String emailId, String flagStatus) throws BusinessException;
    String changeUserPassword(long emailId, String oldPassword, String newPassword) throws BusinessException;

    String sendPushNotificationRegistration(String url, String regId, String deviceId, boolean isAllowed,long userId) throws BusinessException;
    String createTaskOnAsana(String nameAndEmail, String taskNotes, String type) throws BusinessException;
    String createTagForAsanaTask(String tagId, String taskId) throws BusinessException;

    ArrayList<TabBannerDTO> getAllTabBannerData() throws BusinessException;
    TabBannerDTO getTabBannerDataById(long bannerId, String tabName, long tabId) throws BusinessException;

    String postMailChimpData(MailChimpData mailChimpData) throws BusinessException;
    String forgotPassword(String emailId, boolean isResetPassword) throws BusinessException;
    String confirmPassword(long userID, String newPass) throws BusinessException;
    String socialLoginExits(String tokenId, String email) throws BusinessException;
    ArrayList<CategoriesTabDao> getCategoriesData(String url) throws BusinessException;
    ArrayList<PlaylistDto> getPlaylistData(String url) throws BusinessException;
    ArrayList<VideoDTO> getNewVideoData(String url) throws BusinessException;


    @SuppressWarnings("unused")
    ArrayList<VideoDTO> getTrendingVideoData(String url) throws BusinessException;

    @SuppressWarnings("unused")
    ArrayList<VideoDTO> getVideoListByCategory(String url) throws BusinessException;


}
