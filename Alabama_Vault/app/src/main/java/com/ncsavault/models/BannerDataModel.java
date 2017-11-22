package com.ncsavault.models;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import com.ncsavault.controllers.AppController;
import com.ncsavault.database.VaultDatabaseHelper;
import com.ncsavault.dto.TabBannerDTO;
import com.ncsavault.dto.VideoDTO;
import com.ncsavault.globalconstants.GlobalConstants;

import java.util.ArrayList;

/**
 * Class used for get all the banner data from server.
 */

public class BannerDataModel extends BaseModel {

    @SuppressWarnings("unused")
    private ArrayList<VideoDTO> arrayListVideos;
    private final Context context;
    @SuppressWarnings("unused")
    private ArrayList<TabBannerDTO> tabBannerDTOs = new ArrayList<>();

    @SuppressWarnings({"unused", "MismatchedReadAndWriteOfArray"})
    private String[] tabNameArray = {GlobalConstants.FEATURED, GlobalConstants.GAMES, GlobalConstants.PLAYERS,
            GlobalConstants.OPPONENTS, GlobalConstants.COACHES_ERA};

    @SuppressWarnings({"MismatchedReadAndWriteOfArray", "unused"})
    private String[] tabUrl = {GlobalConstants.FEATURED_API_URL, GlobalConstants.GAMES_API_URL, GlobalConstants.PLAYER_API_URL,
            GlobalConstants.OPPONENT_API_URL, GlobalConstants.COACH_API_URL};

    @SuppressWarnings({"unused", "MismatchedReadAndWriteOfArray"})
    private String[] tabOKFName = {GlobalConstants.OKF_FEATURED,GlobalConstants.OKF_GAMES,GlobalConstants.OKF_PLAYERS,
            GlobalConstants.OKF_OPPONENT,GlobalConstants.OKF_COACH};


    /**
     * Constructor of the class
     */
    public BannerDataModel() {
        context = AppController.getInstance().getApplicationContext();
    }

    /**
     * Method used to call banner data Async Task.
     */
    public void loadTabData()
    {
        BannerData bannerData = new BannerData();
        bannerData.execute();
    }


    /**
     * Async class used for get the banner data from server.
     */
    private class BannerData extends AsyncTask<Void, Void, ArrayList<TabBannerDTO>> {

        @SuppressWarnings("deprecation")
        @Override
        protected ArrayList<TabBannerDTO> doInBackground(Void... params) {
            ArrayList<TabBannerDTO> arrayListBanner = new ArrayList<>();
            Intent broadCastIntent = new Intent();
            try {
                arrayListBanner.addAll(AppController.getInstance().getServiceManager().getVaultService().getAllTabBannerData());

                state = STATE_SUCCESS;
                ArrayList<String> lstUrls = new ArrayList<>();

                for (TabBannerDTO bDTO : arrayListBanner) {
                     TabBannerDTO localBannerData = VaultDatabaseHelper.getInstance(context.getApplicationContext()).getLocalTabBannerDataByTabId(bDTO.getTabId());
                    if (bDTO.getTabName().toLowerCase().contains((GlobalConstants.FEATURED).toLowerCase())) {
                        AppController.getInstance().getModelFacade().getLocalModel().setTabId(bDTO.getTabId());
                    }
                    if (localBannerData != null) {
                        if ((localBannerData.getBannerModified() != bDTO.getBannerModified()) || (localBannerData.getBannerCreated() != bDTO.getBannerCreated())) {
                            VaultDatabaseHelper.getInstance(context.getApplicationContext()).updateBannerData(bDTO);
                        }
                        if (localBannerData.getTabDataModified() != bDTO.getTabDataModified()) {
                            VaultDatabaseHelper.getInstance(context.getApplicationContext()).updateTabData(bDTO);
                            if (localBannerData.getTabName().toLowerCase().contains((GlobalConstants.FEATURED).toLowerCase())) {

                                VaultDatabaseHelper.getInstance(context.getApplicationContext()).removeRecordsByTab(GlobalConstants.OKF_FEATURED);
                                lstUrls.add(GlobalConstants.FEATURED_API_URL);
                                String url = GlobalConstants.FEATURED_API_URL + "userid=" + AppController.getInstance().getModelFacade().getLocalModel().getUserId();
                                try {
                                    arrayListVideos.addAll(AppController.getInstance().getServiceManager().getVaultService().getVideosListFromServer(url));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                VaultDatabaseHelper.getInstance(context.getApplicationContext()).insertVideosInDatabase(arrayListVideos);
                            } else if (localBannerData.getTabName().toLowerCase().contains((GlobalConstants.GAMES).toLowerCase())) {

                                VaultDatabaseHelper.getInstance(context.getApplicationContext()).removeRecordsByTab(GlobalConstants.OKF_GAMES);
                                lstUrls.add(GlobalConstants.GAMES_API_URL);
                                String url = GlobalConstants.GAMES_API_URL + "userid=" + AppController.getInstance().getModelFacade().getLocalModel().getUserId();
                                try {
                                    arrayListVideos.addAll(AppController.getInstance().getServiceManager().getVaultService().getVideosListFromServer(url));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                VaultDatabaseHelper.getInstance(context.getApplicationContext()).insertVideosInDatabase(arrayListVideos);
                            } else if (localBannerData.getTabName().toLowerCase().contains((GlobalConstants.PLAYERS).toLowerCase())) {

                                VaultDatabaseHelper.getInstance(context.getApplicationContext()).removeRecordsByTab(GlobalConstants.OKF_PLAYERS);

                                lstUrls.add(GlobalConstants.PLAYER_API_URL);
                                String url = GlobalConstants.PLAYER_API_URL + "userid=" + AppController.getInstance().getModelFacade().getLocalModel().getUserId();
                                try {
                                    arrayListVideos.addAll(AppController.getInstance().getServiceManager().getVaultService().getVideosListFromServer(url));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                VaultDatabaseHelper.getInstance(context.getApplicationContext()).insertVideosInDatabase(arrayListVideos);
                            } else if (localBannerData.getTabName().toLowerCase().contains((GlobalConstants.OPPONENTS).toLowerCase())) {

                                VaultDatabaseHelper.getInstance(context.getApplicationContext()).removeRecordsByTab(GlobalConstants.OKF_OPPONENT);
                                lstUrls.add(GlobalConstants.OPPONENT_API_URL);
                                String url = GlobalConstants.OPPONENT_API_URL + "userid=" + AppController.getInstance().getModelFacade().getLocalModel().getUserId();
                                try {
                                    arrayListVideos.addAll(AppController.getInstance().getServiceManager().getVaultService().getVideosListFromServer(url));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                VaultDatabaseHelper.getInstance(context.getApplicationContext()).insertVideosInDatabase(arrayListVideos);
                            } else if (localBannerData.getTabName().toLowerCase().contains((GlobalConstants.COACHES_ERA).toLowerCase())) {

                                VaultDatabaseHelper.getInstance(context.getApplicationContext()).removeRecordsByTab(GlobalConstants.OKF_COACH);
                                lstUrls.add(GlobalConstants.COACH_API_URL);
                                String url = GlobalConstants.COACH_API_URL + "userid=" + AppController.getInstance().getModelFacade().getLocalModel().getUserId();
                                try {
                                    arrayListVideos.addAll(AppController.getInstance().getServiceManager().getVaultService().getVideosListFromServer(url));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                VaultDatabaseHelper.getInstance(context.getApplicationContext()).insertVideosInDatabase(arrayListVideos);
                            }
                            broadCastIntent.addCategory(Intent.CATEGORY_DEFAULT);
                            context.sendBroadcast(broadCastIntent);
                            arrayListVideos.clear();
                        }
                    } else {
                        VaultDatabaseHelper.getInstance(context.getApplicationContext()).insertTabBannerData(bDTO);
                    }

                }
                if (lstUrls.size() == 0) {
                    int count = VaultDatabaseHelper.getInstance(context.getApplicationContext()).getTabBannerCount();
                    if (count > 0) {
                        lstUrls.add(GlobalConstants.FEATURED_API_URL);
                        lstUrls.add(GlobalConstants.GAMES_API_URL);
                        lstUrls.add(GlobalConstants.PLAYER_API_URL);
                        lstUrls.add(GlobalConstants.OPPONENT_API_URL);
                        lstUrls.add(GlobalConstants.COACH_API_URL);

                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            if (arrayListBanner.size() == 0) {
                state = STATE_RESULT_NOT_FOUND;
            }
            informViews();
            return arrayListBanner;
        }

    }

}
