package com.ncsavault.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ncsavault.controllers.AppController;
import com.ncsavault.database.VaultDatabaseHelper;
import com.ncsavault.dto.CategoriesTabDao;
import com.ncsavault.dto.PlaylistDto;
import com.ncsavault.dto.VideoDTO;
import com.ncsavault.fragments.views.HomeFragment;
import com.ncsavault.fragments.views.PlaylistFragment;
import com.ncsavault.fragments.views.VideoDetailFragment;
import com.ncsavault.globalconstants.GlobalConstants;
import com.ncsavault.utils.Utils;

import java.util.ArrayList;

/**
 * Service used for get the home screen,video list and playlist data from server
 * at the app launch.
 */

public class TrendingFeaturedVideoService extends Service {

    private final ArrayList<VideoDTO> arrayListVideos = new ArrayList<>();
    private final ArrayList<String> apiUrls = new ArrayList<>();
    private final ArrayList<PlaylistDto> playlistDtoArrayList = new ArrayList<>();

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        apiUrls.clear();
        apiUrls.add(GlobalConstants.GET_TRENDING_PLAYLIST_URL);
        apiUrls.add(GlobalConstants.FEATURED_API_URL);
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    String url;
                    for (String apiUrl : apiUrls) {

                        if (Utils.isInternetAvailable(AppController.getInstance().getApplicationContext())) {

                            url = apiUrl + "userid=" + AppController.getInstance().getModelFacade().getLocalModel().getUserId();

                            try {
                                Log.d("Size","Size of list after calling " + apiUrl + " : " + arrayListVideos.size());
                                if (url.contains("Featured")) {
                                    arrayListVideos.addAll(AppController.getInstance().getServiceManager().getVaultService().getVideosListFromServer(url));
                                    VaultDatabaseHelper.getInstance(getApplicationContext()).insertVideosInDatabase(arrayListVideos);
                                } else if (url.contains("TrendingPlayList")) {
                                    arrayListVideos.addAll(AppController.getInstance().getServiceManager().getVaultService().getVideosListFromServer(url));
                                    VaultDatabaseHelper.getInstance(getApplicationContext()).insertTrendingVideosInDatabase(arrayListVideos);
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            arrayListVideos.clear();
                            Log.d("tabBannerDTO","tabBannerDTO thread end ");

                        }
                    }

                    Intent broadCastIntent = new Intent();
                    broadCastIntent.setAction(HomeFragment.HomeResponseReceiver.ACTION_RESP);
                    broadCastIntent.addCategory(Intent.CATEGORY_DEFAULT);
                    sendBroadcast(broadCastIntent);

                    ArrayList<CategoriesTabDao> categoriesListData = new ArrayList<>();
                    String categoriesUrl = GlobalConstants.CATEGORIES_TAB_URL + "userid=" +
                            AppController.getInstance().getModelFacade().getLocalModel().getUserId();

                    categoriesListData.addAll(AppController.getInstance().getServiceManager()
                            .getVaultService().getCategoriesData(categoriesUrl));

                    for (CategoriesTabDao categoriesTabDao : categoriesListData) {
                        VaultDatabaseHelper.getInstance(getApplicationContext()).insertCategoriesTabData(categoriesListData);

                        String videoUrl = GlobalConstants.VIDEO_LIST_BY_CATEGORIES_ID_URL + "userid=" +
                                AppController.getInstance().getModelFacade().getLocalModel().getUserId() +
                                "&nav_tab_id=" + categoriesTabDao.getCategoriesId();

                        arrayListVideos.clear();
                        arrayListVideos.addAll(AppController.getInstance().getServiceManager().
                                getVaultService().getNewVideoData(videoUrl));

                        VaultDatabaseHelper.getInstance(getApplicationContext()).insertVideosInDatabase(arrayListVideos);

                        String playlistUrl = GlobalConstants.CATEGORIES_PLAYLIST_URL + "userid=" +
                                AppController.getInstance().getModelFacade().getLocalModel().getUserId() + "&nav_tab_id="
                                + categoriesTabDao.getCategoriesId();

                        playlistDtoArrayList.clear();
                        playlistDtoArrayList.addAll(AppController.getInstance().getServiceManager().
                                getVaultService().getPlaylistData(playlistUrl));

                        VaultDatabaseHelper.getInstance(getApplicationContext()).
                                insertPlaylistTabData(playlistDtoArrayList, categoriesTabDao.getCategoriesId());

                    }
                    Intent videoIntent = new Intent();
                    videoIntent.setAction(VideoDetailFragment.VideoResponseReceiver.ACTION_RESP);
                    videoIntent.addCategory(Intent.CATEGORY_DEFAULT);
                    sendBroadcast(videoIntent);

                    Intent playlistIntent = new Intent();
                    playlistIntent.setAction(PlaylistFragment.PlaylistResponseReceiver.ACTION_RESP);
                    playlistIntent.addCategory(Intent.CATEGORY_DEFAULT);
                    sendBroadcast(playlistIntent);

                    stopSelf();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            }).start();

        return Service.START_NOT_STICKY;

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
