package com.ncsavault.fragments.views;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ProgressBar;


import com.baoyz.widget.PullRefreshLayout;
import com.ncsavault.asynctask.VideoPlayTask;

import applicationId.R;

import com.ncsavault.adapters.FilterSubtypesAdapter;
import com.ncsavault.controllers.AppController;
import com.ncsavault.customviews.RecyclerViewDisable;
import com.ncsavault.database.VaultDatabaseHelper;
import com.ncsavault.dto.TabBannerDTO;
import com.ncsavault.dto.VideoDTO;
import com.ncsavault.globalconstants.GlobalConstants;
import com.ncsavault.models.BaseModel;
import com.ncsavault.models.VideoDataTaskModel;
import com.ncsavault.service.TrendingFeaturedVideoService;
import com.ncsavault.utils.Utils;
import com.ncsavault.views.AbstractView;
import com.ncsavault.views.HomeScreen;
import com.ncsavault.views.LoginEmailActivity;
import com.ncsavault.views.VideoInfoActivity;
import com.ncsavault.views.VideoSearchActivity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * Class using for show the list of Horizontal view,Banner
 * Inline Ads and feature list using recycler view.
 * Also we are using pull to refresh functionality to handle the update from server.
 */


@SuppressWarnings("deprecation")
public class HomeFragment extends BaseFragment implements AbsListView.OnScrollListener, AbstractView, FilterSubtypesAdapter.BannerClickListener {


    @SuppressLint("StaticFieldLeak")
    private static Activity mContext;


    // The RecyclerView that holds and displays Native Express ads and menu items.
    private RecyclerView mRecyclerView;
    private PullRefreshLayout refreshLayout;

    // List of Native Express ads and MenuItems that populate the RecyclerView.
    private final List<VideoDTO> mRecyclerViewItems = new ArrayList<>();
    private ProgressBar progressBar;
    private ProgressDialog pDialog;
    private TabBannerDTO tabBannerDTO = null;
    private final ArrayList<VideoDTO> trendingArraylist = new ArrayList<>();
    private VideoDataTaskModel mVideoDataTaskModel;
    private FilterSubtypesAdapter adapter;
    private HomeResponseReceiver receiver;
    private RecyclerView.OnItemTouchListener disable;

    /**
     * Method used for create a new instance of fragment.
     * @param context The reference of Context.
     * @return The value of new fragment.
     */
    public static Fragment newInstance(Activity context) {
        Fragment frag = new HomeFragment();
        mContext = context;
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.home_fragment_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String videoUrl = AppController.getInstance().getModelFacade().getLocalModel().getVideoUrl();
        String videoId = AppController.getInstance().getModelFacade().getLocalModel().getVideoId();

        IntentFilter filter = new IntentFilter(HomeResponseReceiver.ACTION_RESP);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new HomeResponseReceiver();
        getActivity().registerReceiver(receiver, filter);

        tabBannerDTO = VaultDatabaseHelper.getInstance(getActivity()).getLocalTabBannerDataByTabId(3);


        if (videoUrl != null || (videoId != null && !videoId.equalsIgnoreCase("0"))) {
            if (videoUrl == null) {
                videoUrl = videoId;
            }
            playFacebookVideo(videoUrl);

            AppController.getInstance().getModelFacade().getLocalModel().setVideoUrl(null);

        }

        initComponents(view);
        setPagerAdapter(view);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (refreshLayout != null) {
            refreshLayout.setEnabled(true);
            refreshLayout.setOnRefreshListener(refreshListener);
        }

        if (adapter!= null) {
            adapter.notifyDataSetChanged();
        }

        ArrayList<String> apiUrls = new ArrayList<>();
        apiUrls.add(GlobalConstants.FEATURED_API_URL);
        apiUrls.add(GlobalConstants.GET_TRENDING_PLAYLIST_URL);
        Intent intent = new Intent(context.getApplicationContext(), TrendingFeaturedVideoService.class);
        intent.putStringArrayListExtra("apiUrls", apiUrls);


    }

    /**
     * Method used for initialize the component of home fragment.
     * @param view The reference of View.
     */
    private void initComponents(View view) {
        refreshLayout = view.findViewById(R.id.refresh_layout);
        disable = new RecyclerViewDisable();
        refreshLayout.setRefreshStyle(PullRefreshLayout.STYLE_RING);
        refreshLayout.setEnabled(false);

        progressBar = view.findViewById(R.id.progressBar);
        setToolbarIcons();

        ((HomeScreen) mContext).imageViewSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, VideoSearchActivity.class);
                intent.putExtra("Fragment", "HomeFragment");
                startActivity(intent);
            }
        });

    }

    /**
     * Method used for the set the toolbar icons and text.
     */
    private void setToolbarIcons() {
        ((HomeScreen) mContext).linearLayoutToolbarText.setVisibility(View.VISIBLE);
        ((HomeScreen) mContext).imageViewSearch.setVisibility(View.VISIBLE);
        ((HomeScreen) mContext).textViewEdit.setVisibility(View.INVISIBLE);
        ((HomeScreen) mContext).imageViewBackNavigation.setVisibility(View.INVISIBLE);
        ((HomeScreen) mContext).textViewToolbarText1.setVisibility(View.VISIBLE);
        ((HomeScreen) mContext).textViewToolbarText2.setText(getResources().getString(R.string.vault_text));
        Typeface faceNormal = Typeface.createFromAsset(mContext.getAssets(), "fonts/OpenSans-Bold.ttf");
        ((HomeScreen) getActivity()).textViewToolbarText2.setTypeface(faceNormal);
    }

    /**
     * Class used to get the updated data from server using Async task
     * Used for to handle the pull to refresh functionality.
     */
    private final PullRefreshLayout.OnRefreshListener refreshListener = new PullRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            if (!Utils.isInternetAvailable(mContext.getApplicationContext())) {
                refreshLayout.setEnabled(false);
                refreshLayout.setRefreshing(false);
            } else {
                refreshLayout.setEnabled(true);
                refreshLayout.setRefreshing(true);

                PullRefreshTask pullTask = new PullRefreshTask();
                pullTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        }
    };

    @Override
    public void update() {

        try {
            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mVideoDataTaskModel != null && mVideoDataTaskModel.getState() == BaseModel.STATE_SUCCESS) {
                        if (mVideoDataTaskModel.getVideoDTO().size() > 0) {
                            VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).
                                    insertVideosInDatabase(mVideoDataTaskModel.getVideoDTO());
                            if (Utils.isInternetAvailable(mContext)) {
                                if (mVideoDataTaskModel.getVideoDTO().get(0).getVideoLongUrl() != null) {
                                    if (mVideoDataTaskModel.getVideoDTO().get(0).getVideoLongUrl().length() > 0
                                            && !mVideoDataTaskModel.getVideoDTO()
                                            .get(0).getVideoLongUrl().toLowerCase().equals("none")) {
                                        String videoCategories = GlobalConstants.FEATURED;
                                        Intent intent = new Intent(mContext,
                                                VideoInfoActivity.class);
                                        intent.putExtra(GlobalConstants.KEY_CATEGORY, videoCategories);
                                        intent.putExtra(GlobalConstants.PLAYLIST_REF_ID, mVideoDataTaskModel.getVideoDTO()
                                                .get(0).getPlaylistReferenceId());
                                        intent.putExtra(GlobalConstants.VIDEO_OBJ, mVideoDataTaskModel.getVideoDTO().get(0));
                                        mContext.startActivity(intent);
                                        mContext.overridePendingTransition(R.anim.slide_up_video_info,
                                                R.anim.nochange);
                                    } else {
                                        ((HomeScreen) mContext).showToastMessage(GlobalConstants.MSG_NO_INFO_AVAILABLE);
                                    }
                                } else {
                                    ((HomeScreen) mContext).showToastMessage(GlobalConstants.MSG_NO_INFO_AVAILABLE);
                                }
                            } else {
                                ((HomeScreen) mContext).showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
                            }
                        }
                        pDialog.dismiss();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method used for click on banner.
     * Also check the condition isHyperlinkActive or not.
     */
    private void bannerClick() {
        if (tabBannerDTO != null) {
            if (tabBannerDTO.isBannerActive()) {
                if (tabBannerDTO.isHyperlinkActive() && tabBannerDTO.getBannerActionURL().length() > 0) {
                    //Start the ActionUrl in Browser
                    Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(tabBannerDTO.getBannerActionURL()));
                    startActivity(intent);
                } else if (!tabBannerDTO.isHyperlinkActive() && tabBannerDTO.getBannerActionURL().length() > 0) {
                    //The ActionUrl has DeepLink associated with it
                    @SuppressWarnings("unchecked")
                    HashMap<String, String> videoMap = Utils.getInstance().getVideoInfoFromBanner(tabBannerDTO.getBannerActionURL());
                    if (videoMap != null) {
                        if (videoMap.get("VideoId") != null) {
                            if (VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).
                                    isVideoAvailableInDB(videoMap.get("VideoId"))) {
                                VideoDTO videoDTO = VaultDatabaseHelper.getInstance(mContext.getApplicationContext())
                                        .getVideoDataByVideoId(videoMap.get("VideoId"));
                                if (Utils.isInternetAvailable(mContext)) {
                                    if (videoDTO != null) {
                                        if (videoDTO.getVideoLongUrl() != null) {
                                            String videoCategory = GlobalConstants.FEATURED;
                                            Intent intent = new Intent(mContext,
                                                    VideoInfoActivity.class);
                                            intent.putExtra(GlobalConstants.KEY_CATEGORY, videoCategory);
                                            intent.putExtra(GlobalConstants.PLAYLIST_REF_ID, videoDTO.
                                                    getPlaylistReferenceId());
                                            intent.putExtra(GlobalConstants.VIDEO_OBJ, videoDTO);
                                            startActivity(intent);
                                            mContext.overridePendingTransition(R.anim.slide_up_video_info,
                                                    R.anim.nochange);
                                        }
                                    } else {
                                        ((HomeScreen) mContext).showToastMessage(GlobalConstants.MSG_NO_INFO_AVAILABLE);
                                    }
                                } else {
                                    ((HomeScreen) mContext).showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
                                }
                            } else {
                                //Make an API call to get video data
                                pDialog = new ProgressDialog(mContext, R.style.CustomDialogTheme);
                                pDialog.show();
                                pDialog.setContentView(Utils.getInstance().setViewToProgressDialog(getActivity()));
                                pDialog.setCanceledOnTouchOutside(false);
                                pDialog.setCancelable(false);

                                mVideoDataTaskModel = AppController.getInstance().getModelFacade().getRemoteModel()
                                        .getVideoDataTaskModel();
                                mVideoDataTaskModel.registerView(HomeFragment.this);

                                mVideoDataTaskModel.setProgressDialog(pDialog);
                                mVideoDataTaskModel.loadVideoData(videoMap);

                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onClick(FilterSubtypesAdapter.BannerViewHolder viewHolder, int position) {

        viewHolder.imageViewBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bannerClick();
            }
        });

    }

    /**
     * Class used for get the updated data from server.
     * This is a pull to refresh functionality using Async task.
     */
    private class PullRefreshTask extends AsyncTask<Void, Void, ArrayList<VideoDTO>> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mRecyclerView != null) {
                mRecyclerView.setEnabled(false);
                mRecyclerView.addOnItemTouchListener(disable);
            }

            refreshLayout.setRefreshing(true);

        }

        @SuppressWarnings("ResultOfMethodCallIgnored")
        @Override
        protected ArrayList<VideoDTO> doInBackground(Void... params) {
            ArrayList<VideoDTO> arrList = new ArrayList<>();
            String url;
            try {
                //Update Banner Data
                TabBannerDTO serverObj = AppController.getInstance().getServiceManager().
                        getVaultService().getTabBannerDataById(tabBannerDTO.getTabBannerId(),
                        tabBannerDTO.getTabKeyword(), tabBannerDTO.getTabId());
                if (tabBannerDTO.getTabDataModified() != serverObj.getTabDataModified()) {
                    VaultDatabaseHelper.getInstance(context.getApplicationContext()).updateTabData(serverObj);

                    url = GlobalConstants.FEATURED_API_URL + "userId=" + AppController.getInstance().
                            getModelFacade().getLocalModel().getUserId();
                    arrList.clear();
                    arrList.addAll(AppController.getInstance().getServiceManager().getVaultService().getVideosListFromServer(url));
                    VaultDatabaseHelper.getInstance(getActivity().getApplicationContext()).removeRecordsByTab(GlobalConstants.OKF_FEATURED);
                    VaultDatabaseHelper.getInstance(getActivity().getApplicationContext()).insertVideosInDatabase(arrList);

                    url = GlobalConstants.GET_TRENDING_PLAYLIST_URL + "userId=" + AppController.getInstance().
                            getModelFacade().getLocalModel().getUserId();
                    trendingArraylist.clear();
                    trendingArraylist.addAll(AppController.getInstance().getServiceManager().getVaultService().
                            getVideosListFromServer(url));

                    VaultDatabaseHelper.getInstance(getActivity().getApplicationContext()).
                            removeTrendingVideoRecords();
                    VaultDatabaseHelper.getInstance(getActivity().getApplicationContext()).
                            insertTrendingVideosInDatabase(trendingArraylist);

                }
                if (serverObj != null) {
                    if ((tabBannerDTO.getBannerModified() != serverObj.getBannerModified()) ||
                            (tabBannerDTO.getBannerCreated() != serverObj.getBannerCreated())) {

                        VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).
                                updateTabBannerData(serverObj);

                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return arrList;
        }

        @Override
        protected void onPostExecute(final ArrayList<VideoDTO> result) {
            super.onPostExecute(result);
            try {
                if (result != null) {
                    mRecyclerViewItems.clear();
                    mRecyclerViewItems.addAll(VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).getVideoList(GlobalConstants.OKF_FEATURED));
                    Collections.sort(mRecyclerViewItems, new Comparator<VideoDTO>() {

                        @Override
                        public int compare(VideoDTO lhs, VideoDTO rhs) {

                            return lhs.getVideoName().toLowerCase()
                                    .compareTo(rhs.getVideoName().toLowerCase());
                        }
                    });

                    // ------- update BannerImage---------------------


                    if (trendingArraylist.size() > 0) {
                        mRecyclerViewItems.add(0, new VideoDTO());
                    } else {
                        mRecyclerViewItems.remove(0);
                    }

                    if (tabBannerDTO != null) {
                        tabBannerDTO = VaultDatabaseHelper.getInstance(mContext.getApplicationContext())
                                .getLocalTabBannerDataByTabId(tabBannerDTO.getTabId());
                        VideoDTO videoDTOBanner = new VideoDTO();
                        videoDTOBanner.setVideoStillUrl(tabBannerDTO.getBannerURL());
                        if (tabBannerDTO.isBannerActive()) {
                            mRecyclerViewItems.add(1, videoDTOBanner);
                            AppController.getInstance().getModelFacade().getLocalModel().setBannerActivated(true);
                        } else {
                            mRecyclerViewItems.add(1, new VideoDTO());
                            AppController.getInstance().getModelFacade().getLocalModel().setBannerActivated(false);
                        }

                    }
                    for (int i = 0; i < mRecyclerViewItems.size(); i++) {
                        if ((i + 1) % 3 == 0) {
                            {
                                VideoDTO videoAdMob = new VideoDTO();
                                videoAdMob.setVideoName(getRandomId());
                                mRecyclerViewItems.add(i, videoAdMob);
                            }
                        }
                    }
                    if (adapter != null) {
                        if (VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).getTrendingVideoCount() > 0) {
                            trendingArraylist.clear();
                            trendingArraylist.addAll(VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).getAllTrendingVideoList());
                        }
                        adapter = new FilterSubtypesAdapter(mContext, mRecyclerViewItems, trendingArraylist,
                                HomeFragment.this);
                        mRecyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        refreshLayout.setRefreshing(false);
                        mRecyclerView.removeOnItemTouchListener(disable);
                    }


                } else {
                    ((HomeScreen) mContext).showToastMessage(GlobalConstants.MSG_CONNECTION_TIMEOUT);

                }
                refreshLayout.setRefreshing(false);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Method used for get the random ad mob unit ids.
     * @return The ad mob id.
     */
    private String getRandomId() {
        return (mContext.getResources().getString(R.string.ad_units_featured));
    }


    /**
     * Method used for get the list of feature list, Horizontal pager from data base.
     * @param view The reference of View.
     */
    private void setPagerAdapter(View view) {
        try {
            mRecyclerView = view.findViewById(R.id.card_recycler_view);

            getFeatureDataFromDataBase();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    /**
     * This broadcast class used for show the home screen data
     * Which we are getting from Service at first time app launch.
     */
    public class HomeResponseReceiver extends BroadcastReceiver {

        public static final String ACTION_RESP =
                "Message Processed";

        @Override
        public void onReceive(Context context, Intent intent) {

            try {
                mRecyclerViewItems.clear();
                mRecyclerViewItems.addAll(VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).getVideoList(GlobalConstants.OKF_FEATURED));

                if (VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).getTrendingVideoCount() > 0) {
                    trendingArraylist.clear();
                    trendingArraylist.addAll(VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).getAllTrendingVideoList());
                }

                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }

                Collections.sort(mRecyclerViewItems, new Comparator<VideoDTO>() {

                    @Override
                    public int compare(VideoDTO lhs, VideoDTO rhs) {

                        return lhs.getVideoName().toLowerCase()
                                .compareTo(rhs.getVideoName().toLowerCase());
                    }
                });

                tabBannerDTO = VaultDatabaseHelper.getInstance(getActivity()).getLocalTabBannerDataByTabId(3);
                if (trendingArraylist.size() > 0) {
                    mRecyclerViewItems.add(0, new VideoDTO());
                } else {
                    mRecyclerViewItems.remove(0);
                }

                if (tabBannerDTO != null) {
                    VideoDTO videoDTOBanner = new VideoDTO();

                    videoDTOBanner.setVideoStillUrl(tabBannerDTO.getBannerURL());
                    if (tabBannerDTO.isBannerActive()) {
                        mRecyclerViewItems.add(1, videoDTOBanner);
                        AppController.getInstance().getModelFacade().getLocalModel().setBannerActivated(true);
                    } else {
                        mRecyclerViewItems.add(1, new VideoDTO());
                        AppController.getInstance().getModelFacade().getLocalModel().setBannerActivated(false);
                    }
                }

                for (int i = 0; i < mRecyclerViewItems.size(); i++) {
                    if ((i + 1) % 3 == 0) {
                        {
                            VideoDTO videoAdMob = new VideoDTO();
                            videoAdMob.setVideoName(getRandomId());
                            mRecyclerViewItems.add(i, videoAdMob);
                        }
                    }

                }
                adapter = new FilterSubtypesAdapter(mContext, mRecyclerViewItems, trendingArraylist, HomeFragment.this);
                mRecyclerView.setAdapter(adapter);
                mRecyclerView.setHasFixedSize(true);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                mRecyclerView.setLayoutManager(layoutManager);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Method used for get the home tab data from data base.
     */
    private void getFeatureDataFromDataBase() {


        try {
            if (progressBar != null) {
                progressBar.setVisibility(View.VISIBLE);
            }

            if (VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).getVideoCount() > 0) {
                mRecyclerViewItems.clear();
                mRecyclerViewItems.addAll(VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).
                        getVideoList(GlobalConstants.OKF_FEATURED));

            }

            if (VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).getTrendingVideoCount() > 0) {
                trendingArraylist.clear();
                trendingArraylist.addAll(VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).getAllTrendingVideoList());
            }


            Collections.sort(mRecyclerViewItems, new Comparator<VideoDTO>() {

                @Override
                public int compare(VideoDTO lhs, VideoDTO rhs) {
                    return lhs.getVideoName().toLowerCase()
                            .compareTo(rhs.getVideoName().toLowerCase());
                }
            });

            Log.d("featuredVideoList","featuredVideoList doInBackground : " + mRecyclerViewItems.size());
            if (trendingArraylist.size() > 0) {
                mRecyclerViewItems.add(0, new VideoDTO());
            } else {
                mRecyclerViewItems.remove(0);
            }

            VideoDTO videoDTOBanner = new VideoDTO();
            if(tabBannerDTO != null) {
                videoDTOBanner.setVideoStillUrl(tabBannerDTO.getBannerURL());
                if (tabBannerDTO.isBannerActive()) {
                    mRecyclerViewItems.add(1, videoDTOBanner);
                    AppController.getInstance().getModelFacade().getLocalModel().setBannerActivated(true);
                } else {
                    // mRecyclerViewItems.remove(1);
                    mRecyclerViewItems.add(1, new VideoDTO());
                    AppController.getInstance().getModelFacade().getLocalModel().setBannerActivated(false);
                }
            }

            for (int i = 0; i < mRecyclerViewItems.size(); i++) {
                if ((i + 1) % 3 == 0) {
                    {
                        VideoDTO videoAdMob = new VideoDTO();
                        videoAdMob.setVideoName(getRandomId());
                        mRecyclerViewItems.add(i, videoAdMob);
                    }
                }

            }

            if (progressBar != null) {
                progressBar.setVisibility(View.GONE);
            }
            adapter = new FilterSubtypesAdapter(mContext, mRecyclerViewItems, trendingArraylist,
                    HomeFragment.this);
            mRecyclerView.setAdapter(adapter);
            mRecyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
            mRecyclerView.setLayoutManager(layoutManager);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (receiver != null) {
                getActivity().unregisterReceiver(receiver);
                receiver = null;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method used for play the video of push notification and social sharing.
     * @param videoId For playing a particular video.
     */
    private void playFacebookVideo(String videoId) {

        try {
            if (AppController.getInstance().getModelFacade().getLocalModel().isDefaultLogin()) {
                Log.i("Skip mode","start service");
                AppController.getInstance().getModelFacade().getLocalModel().setDefaultLogin(false);
                SharedPreferences prefs = mContext.getSharedPreferences(getResources()
                        .getString(R.string.pref_package_name), Context.MODE_PRIVATE);
                prefs.edit().putLong(GlobalConstants.PREF_VAULT_USER_ID_LONG, GlobalConstants.DEFAULT_USER_ID).apply();
                prefs.edit().putBoolean(GlobalConstants.PREF_VAULT_SKIP_LOGIN, true).apply();
               // VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).removeAllRecords();
                Intent intent = new Intent(mContext, TrendingFeaturedVideoService.class);
                mContext.startService(intent);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Utils.loadDataFromServer(mContext);
                    }
                }).start();
            }
            if (VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).isVideoAvailableInDB(videoId)) {
                VideoDTO videoDTO = VaultDatabaseHelper.getInstance(mContext.getApplicationContext())
                        .getVideoDataByVideoId(videoId);
                AppController.getInstance().getModelFacade().getLocalModel().setVideoId(null);
                if (Utils.isInternetAvailable(mContext)) {
                    if (videoDTO != null) {
                        if (videoDTO.getVideoLongUrl() != null) {
                            //  if (videoDTO.getVideoLongUrl().length() > 0 && !videoDTO.getVideoLongUrl().toLowerCase().equals("none")) {
                            String videoCategory = GlobalConstants.FEATURED;
                            Intent intent = new Intent(mContext,
                                    VideoInfoActivity.class);
                            intent.putExtra(GlobalConstants.KEY_CATEGORY, videoCategory);
                            intent.putExtra(GlobalConstants.PLAYLIST_REF_ID, videoDTO.getPlaylistReferenceId());
                            intent.putExtra(GlobalConstants.VIDEO_OBJ, videoDTO);
                            startActivity(intent);
                            mContext.overridePendingTransition(R.anim.slide_up_video_info, R.anim.nochange);
                        }
                    } else {
                        ((HomeScreen) mContext).showToastMessage(GlobalConstants.MSG_NO_INFO_AVAILABLE);
                    }
                }
            } else {
                Log.d("is video available:", "is video available: " + VaultDatabaseHelper.getInstance(mContext
                        .getApplicationContext()).isVideoAvailableInDB(videoId));
                try {

                    VideoPlayTask videoPlayTask = new VideoPlayTask(mContext, pDialog);
                    videoPlayTask.execute(videoId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            AppController.getInstance().getModelFacade().getLocalModel().setVideoId(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
