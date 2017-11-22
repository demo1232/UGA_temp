package com.ncsavault.fragments.views;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.baoyz.widget.PullRefreshLayout;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;


import applicationId.R;

import com.ncsavault.adapters.PlaylistDataAdapter;
import com.ncsavault.controllers.AppController;
import com.ncsavault.customviews.RecyclerViewDisable;
import com.ncsavault.database.VaultDatabaseHelper;
import com.ncsavault.dto.PlaylistDto;
import com.ncsavault.dto.TabBannerDTO;
import com.ncsavault.dto.VideoDTO;
import com.ncsavault.globalconstants.GlobalConstants;
import com.ncsavault.models.BaseModel;
import com.ncsavault.models.VideoDataTaskModel;
import com.ncsavault.utils.Utils;
import com.ncsavault.views.AbstractView;
import com.ncsavault.views.HomeScreen;
import com.ncsavault.views.VideoInfoActivity;
import com.ncsavault.views.VideoSearchActivity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/**
 * Class used for show the list of playlist item and also show the inline ads.
 * We are showing banner top of the screen. Also we have to used pull to refresh functionality.
 */

public class PlaylistFragment extends Fragment implements PlaylistDataAdapter.PlaylistDataClickListener, AbstractView {

    @SuppressLint("StaticFieldLeak")
    private static Context mContext;
    private RecyclerView mRecyclerView;
    private PlaylistDataAdapter mAlbumsAdapter;
    private static final int TOTAL_CELLS_PER_ROW = 1;
    private ProgressBar progressBar;
    private final ArrayList<PlaylistDto> playlistDtoDataList = new ArrayList<>();
    private long tabId = 0;
    private TabBannerDTO tabBannerDTO = null;
    private ImageView bannerImageView;
    private LinearLayout bannerLayout;
    private VideoDataTaskModel mVideoDataTaskModel;
    @SuppressWarnings("deprecation")
    private ProgressDialog pDialog = null;
    private PullRefreshLayout refreshLayout;
    private RecyclerView.OnItemTouchListener disable;
    private PlaylistResponseReceiver receiver;
    private TextView noPlaylistFound;
    private  String tabName;

    /**
     * Method used for create a new instance of playlist fragment.
     * @param context The reference of Context.
     * @param tabId Set the tab id to show the list of particular playlist screen.
     * @param categoryName To show the tab name tap of the screen.
     * @return The value of fragment.
     */
    public static Fragment newInstance(Context context, long tabId, String categoryName) {
        Fragment playlistFragment = new PlaylistFragment();
        mContext = context;
        Bundle args = new Bundle();
        args.putLong("tab_id", tabId);
        args.putString("categoryName", categoryName);
        playlistFragment.setArguments(args);
        return playlistFragment;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            getActivity().unregisterReceiver(receiver);
            receiver = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (refreshLayout != null) {
            refreshLayout.setEnabled(true);
            refreshLayout.setOnRefreshListener(refreshListener);
        }
    }

    /**
     * Method used for handle the call back of pull to refresh
     * And to call the PullRefreshTask.
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
                if(!playlistDtoDataList.isEmpty())
                {
                    playlistDtoDataList.clear();
                }
                PullRefreshTask pullTask = new PullRefreshTask();
                pullTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.playlist_fragment_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        IntentFilter filter = new IntentFilter(PlaylistResponseReceiver.ACTION_RESP);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new PlaylistResponseReceiver();
        getActivity().registerReceiver(receiver, filter);

        initViews(view);
        initListener();

        tabBannerDTO = VaultDatabaseHelper.getInstance(getActivity()).getLocalTabBannerDataByTabId(tabId);

        if (tabBannerDTO != null) {
            showBannerImage(bannerImageView, tabBannerDTO);
        }

        getPlaylistDateFromDatabase();
        // getPlaylistData(tabId);

    }

    /**
     * Method used for get the ad mob unit id for a particular tab.
     * @param playlistDtoDataList Set the ad mob unit id on playlist data list.
     * @return The value of ad mob id.
     */
    private String getUnitId(ArrayList<PlaylistDto> playlistDtoDataList) {
        if (playlistDtoDataList != null && playlistDtoDataList.size() > 0) {
            if (playlistDtoDataList.get(0).getPlaylistReferenceId().toLowerCase().contains("featured")) {

                return mContext.getResources().getString(R.string.ad_units_featured);

            } else if (playlistDtoDataList.get(0).getPlaylistReferenceId().toLowerCase().contains("games")) {

                return mContext.getResources().getString(R.string.ad_units_games);

            } else if (playlistDtoDataList.get(0).getPlaylistReferenceId().toLowerCase().contains("player")) {

                return mContext.getResources().getString(R.string.ad_units_player);

            } else if (playlistDtoDataList.get(0).getPlaylistReferenceId().toLowerCase().contains("opponent")) {

                return mContext.getResources().getString(R.string.ad_units_oppontents);

            } else if (playlistDtoDataList.get(0).getPlaylistReferenceId().toLowerCase().contains("coach")) {

                return mContext.getResources().getString(R.string.ad_units_coachesera);
            }
        }
        return null;
    }


    /**
     * Method used for initialize the component of playlist screen.
     * @param view The reference of View.
     */
    private void initViews(View view) {
        mRecyclerView = view.findViewById(R.id.recycler_view);
        disable = new RecyclerViewDisable();
        bannerImageView = view.findViewById(R.id.img_banner);
        bannerLayout = view.findViewById(R.id.sync_banner_layout);
        progressBar = view.findViewById(R.id.progressbar);
        noPlaylistFound = view.findViewById(R.id.tv_no_playlist_found);

        refreshLayout = view.findViewById(R.id.refresh_layout);

        refreshLayout.setRefreshStyle(PullRefreshLayout.STYLE_RING);
        refreshLayout.setEnabled(false);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            tabId = bundle.getLong("tab_id", 0);
            tabName = bundle.getString("categoryName");
            ((HomeScreen) getActivity()).textViewToolbarText2.setText(tabName);
             Typeface faceNormal = Typeface.createFromAsset(getActivity().getAssets(), "fonts/OpenSans-Regular.ttf");
            ((HomeScreen) getActivity()).textViewToolbarText2.setTypeface(faceNormal);
        }


        setToolbarIcons();
        ((HomeScreen) getActivity()).imageViewSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, VideoSearchActivity.class);
                intent.putExtra("Fragment", "PlaylistFragment");
                intent.putExtra("categoryId", tabId);
                intent.putExtra("tab_name", tabName);
                mContext.startActivity(intent);
            }
        });
        ((HomeScreen) mContext).imageViewBackNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
    }

    /**
     * Method used for set the toolbar icons and text in playlist screen.
     */
    private void setToolbarIcons() {
        ((HomeScreen) mContext).linearLayoutToolbarText.setVisibility(View.VISIBLE);
        ((HomeScreen) mContext).imageViewSearch.setVisibility(View.VISIBLE);
        ((HomeScreen) mContext).textViewEdit.setVisibility(View.INVISIBLE);
        ((HomeScreen) mContext).imageViewBackNavigation.setVisibility(View.VISIBLE);
        ((HomeScreen) mContext).textViewToolbarText1.setVisibility(View.GONE);
    }

    /**
     * Method used for handle the click event of all the views
     */
    private void initListener() {
        bannerImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bannerClick();
            }
        });
    }

    /**
     * Method used for click on banner.
     * Also check the condition isHyperlinkActive or not.
     */
    @SuppressWarnings("deprecation")
    private void bannerClick() {
        if (tabBannerDTO != null) {
            if (tabBannerDTO.isBannerActive()) {
                if (tabBannerDTO.isHyperlinkActive() && tabBannerDTO.getBannerActionURL().length() > 0) {
                    //Start the ActionUrl in Browser
                    Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(tabBannerDTO.getBannerActionURL()));
                    startActivity(intent);
                } else if (!tabBannerDTO.isHyperlinkActive() && tabBannerDTO.getBannerActionURL().length() > 0) {
                    //The ActionUrl has DeepLink associated with it
                    HashMap videoMap = Utils.getInstance().getVideoInfoFromBanner(tabBannerDTO.getBannerActionURL());
                    if (videoMap != null) {
                        if (videoMap.get("VideoId") != null) {
                            if (VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).
                                    isVideoAvailableInDB(videoMap.get("VideoId").toString())) {
                                VideoDTO videoDTO = VaultDatabaseHelper.getInstance(mContext.getApplicationContext())
                                        .getVideoDataByVideoId(videoMap.get("VideoId").toString());
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
                                            ((HomeScreen) mContext).overridePendingTransition(R.anim.slide_up_video_info,
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
                                mVideoDataTaskModel.registerView(PlaylistFragment.this);

                                mVideoDataTaskModel.setProgressDialog(pDialog);
                                //noinspection unchecked
                                mVideoDataTaskModel.loadVideoData(videoMap);

                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onClick(PlaylistDataAdapter.MyViewHolder viewHolder, final long playlistId, final String playListName) {

        viewHolder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VideoDetailFragment videoFragment = (VideoDetailFragment) VideoDetailFragment.newInstance(mContext, playlistId, playListName);
                FragmentManager manager = ((HomeScreen) mContext).getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.container, videoFragment, videoFragment.getClass().getName());
                transaction.addToBackStack(videoFragment.getClass().getName());
                transaction.commit();
            }
        });

    }

    @Override
    public void update() {
        try {
            ((HomeScreen) mContext).runOnUiThread(new Runnable() {
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
                                        ((HomeScreen) mContext).overridePendingTransition(R.anim.slide_up_video_info,
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
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private final int spanCount;
        private final int spacing;
        private final boolean includeEdge;

        public GridSpacingItemDecoration(int spacing) {
            this.spanCount = 2;
            this.spacing = spacing;
            this.includeEdge = true;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx() {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, r.getDisplayMetrics()));
    }

    /**
     * Method used for get the playlist data from data base.
     */
    private void getPlaylistDateFromDatabase() {

        final AsyncTask<Void, Void, ArrayList<PlaylistDto>> mDbTask = new AsyncTask<Void, Void, ArrayList<PlaylistDto>>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (progressBar != null) {
                    progressBar.setVisibility(View.VISIBLE);
                }
            }

            @Override
            protected ArrayList<PlaylistDto> doInBackground(Void... params) {

                try {
                    playlistDtoDataList.clear();
                    playlistDtoDataList.addAll(VaultDatabaseHelper.getInstance(mContext.getApplicationContext())
                            .getLocalPlaylistDataByCategoriesTab(tabId));


                } catch (Exception e) {
                    e.printStackTrace();
                }
                return playlistDtoDataList;
            }

            @Override
            protected void onPostExecute(ArrayList<PlaylistDto> result) {
                super.onPostExecute(result);
                Log.i("PLaylist Fragment : ","12345 onPostExecute on getPlaylistDateFromDatabase");
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);

                }
                if (playlistDtoDataList.size() >0) {
                    noPlaylistFound.setVisibility(View.GONE);
                }else
                {
                    noPlaylistFound.setVisibility(View.VISIBLE);
                }
                Collections.sort(playlistDtoDataList, new Comparator<PlaylistDto>() {

                    @Override
                    public int compare(PlaylistDto lhs, PlaylistDto rhs) {

                        return lhs.getPlaylistName().toLowerCase()
                                .compareTo(rhs.getPlaylistName().toLowerCase());
                    }
                });

                for (int j = 0; j < playlistDtoDataList.size(); j++) {
                    if ((j + 1) % 7 == 0) {
                        String adUnitVault = getUnitId(playlistDtoDataList);
                        PlaylistDto playlistDto = new PlaylistDto();
                        playlistDto.setPlaylistName(adUnitVault);
                        playlistDtoDataList.add(j, playlistDto);
                    }
                }

                mAlbumsAdapter = new PlaylistDataAdapter(getActivity(), PlaylistFragment.this, playlistDtoDataList);
                GridLayoutManager mLayoutManager = new GridLayoutManager(mContext, 2);
                mRecyclerView.setLayoutManager(mLayoutManager);
                mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(dpToPx()));
                mRecyclerView.setItemAnimator(new DefaultItemAnimator());
                mRecyclerView.setAdapter(mAlbumsAdapter);
                mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        switch (mAlbumsAdapter.getItemViewType(position)) {
                            case PlaylistDataAdapter.TYPE_LIST_DATA:
                                return TOTAL_CELLS_PER_ROW;
                            case PlaylistDataAdapter.TYPE_AD:
                                return 2;
                            default:
                                return 2;
                        }
                    }
                });

            }
        };

        mDbTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * Method used for show the banner image.
     * @param bannerCacheableImageView Set the banner image.
     * @param tabBannerDTO Get the value of banner data from tab banner dto.
     */
    private void showBannerImage(final ImageView bannerCacheableImageView, TabBannerDTO tabBannerDTO) {
        if (tabBannerDTO != null)
            if (tabBannerDTO.isBannerActive()) {
                bannerLayout.setVisibility(View.VISIBLE);

                try {
                    bannerCacheableImageView.setVisibility(View.VISIBLE);
                    Glide.with(mContext)
                            .load(tabBannerDTO.getBannerURL())
                            .listener(new RequestListener<String, GlideDrawable>() {
                                @Override
                                public boolean onException(Exception e, String model, Target<GlideDrawable> target,
                                                           boolean isFirstResource) {
                                    bannerCacheableImageView.setVisibility(View.GONE);
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(GlideDrawable resource, String model,
                                                               Target<GlideDrawable> target, boolean
                                                                       isFromMemoryCache, boolean isFirstResource) {
                                    bannerCacheableImageView.setVisibility(View.VISIBLE);
                                    return false;
                                }
                            })
                            .into(bannerCacheableImageView);
                } catch (Exception error) {
                    error.printStackTrace();
                    Log.e("Utils", "Exception Saved " + error.getMessage());
                }
              } else {
                bannerCacheableImageView.setVisibility(View.GONE);
                bannerLayout.setVisibility(View.GONE);
            }
    }

    /**
     * Class used to get the updated data from server using Async task
     * Used for to handle the pull to refresh functionality.
     */
    public class PullRefreshTask extends AsyncTask<Void, Void, ArrayList<PlaylistDto>> {

        public boolean isBannerUpdated = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mRecyclerView != null) {
                mRecyclerView.setEnabled(false);
                mRecyclerView.addOnItemTouchListener(disable);
            }

            refreshLayout.setRefreshing(true);

        }

        @SuppressWarnings({"deprecation", "ResultOfMethodCallIgnored"})
        @Override
        protected ArrayList<PlaylistDto> doInBackground(Void... params) {
            try {
                SharedPreferences pref = AppController.getInstance().getApplicationContext().
                        getSharedPreferences(mContext.getResources()
                                .getString(R.string.pref_package_name), Context.MODE_PRIVATE);
                long userId = pref.getLong(GlobalConstants.PREF_VAULT_USER_ID_LONG, 0);

                //Update Banner Data
                if (tabBannerDTO != null) {
                    TabBannerDTO serverObj = AppController.getInstance().getServiceManager().
                            getVaultService().getTabBannerDataById(tabBannerDTO.getTabBannerId(),
                            tabBannerDTO.getTabKeyword(), tabBannerDTO.getTabId());

                    if (serverObj != null) {
                        if ((tabBannerDTO.getBannerModified() != serverObj.getBannerModified()) ||
                                (tabBannerDTO.getBannerCreated() != serverObj.getBannerCreated())) {

                            VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).
                                    updateTabBannerData(serverObj);
                            isBannerUpdated = true;
                        }
                    }
                }

                String url = GlobalConstants.CATEGORIES_PLAYLIST_URL + "userid=" + userId + "&nav_tab_id=" + tabId;

                playlistDtoDataList.addAll(AppController.getInstance().getServiceManager().getVaultService().getPlaylistData(url));

                Collections.sort(playlistDtoDataList, new Comparator<PlaylistDto>() {

                    @Override
                    public int compare(PlaylistDto lhs, PlaylistDto rhs) {
                        return lhs.getPlaylistName().toLowerCase()
                                .compareTo(rhs.getPlaylistName().toLowerCase());
                    }
                });

                if(playlistDtoDataList.size()>0) {
                    for (PlaylistDto playlistDto : playlistDtoDataList) {
                        PlaylistDto localPlaylistDto = VaultDatabaseHelper.getInstance(mContext)
                                .getLocalPlaylistDataByPlaylistId(playlistDto.getPlaylistId());

                        if (localPlaylistDto != null) {
                            if (localPlaylistDto.getPlaylist_modified() != playlistDto.getPlaylist_modified()) {
                                VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).removePlaylistTabData(tabId);
                                VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).insertPlaylistTabData
                                        (playlistDtoDataList, tabId);
                            }
                        } else {
                            VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).insertPlaylistTabData
                                    (playlistDtoDataList, tabId);
                        }
                    }
                }else
                {
                    VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).removePlaylistTabData(tabId);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return playlistDtoDataList;
        }

        @Override
        protected void onPostExecute(final ArrayList<PlaylistDto> result) {
            super.onPostExecute(result);
            try {
                Log.i("PLaylist Fragment : ","12345 onPostExecute on pull refresh");
                playlistDtoDataList.clear();
                playlistDtoDataList.addAll(VaultDatabaseHelper.getInstance(mContext.getApplicationContext())
                        .getLocalPlaylistDataByCategoriesTab(tabId));
                if (progressBar != null && progressBar.getVisibility() == View.VISIBLE) {
                    progressBar.setVisibility(View.GONE);
                }
                if (playlistDtoDataList.size() >0) {
                    noPlaylistFound.setVisibility(View.GONE);
                }else
                {
                    noPlaylistFound.setVisibility(View.VISIBLE);
                }

                if (playlistDtoDataList != null) {
                    if (playlistDtoDataList.size() > 0) {
                        for (int j = 0; j < playlistDtoDataList.size(); j++) {
                            if ((j + 1) % 7 == 0) {
                                String adUnitVault = getUnitId(playlistDtoDataList);
                                PlaylistDto playlistDto = new PlaylistDto();
                                playlistDto.setPlaylistName(adUnitVault);
                                playlistDtoDataList.add(j, playlistDto);
                            }
                        }

                        mAlbumsAdapter = new PlaylistDataAdapter(getActivity(), PlaylistFragment.this, playlistDtoDataList);
                        mAlbumsAdapter.notifyDataSetChanged();
                        mRecyclerView.removeOnItemTouchListener(disable);
                        refreshLayout.setRefreshing(false);
                        mRecyclerView.setAdapter(mAlbumsAdapter);
                    }
                    // ------- update BannerImage---------------------
                    if (isBannerUpdated)
                        if (tabBannerDTO != null) {
                            tabBannerDTO = VaultDatabaseHelper.getInstance(mContext.getApplicationContext())
                                    .getLocalTabBannerDataByTabId(tabBannerDTO.getTabId());
                            if (tabBannerDTO != null)
                                showBannerImage(bannerImageView, tabBannerDTO);
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
     * This broadcast receiver class used for show the playlist data when service will be start
     * at the time of app launch.
     */
    public class PlaylistResponseReceiver extends BroadcastReceiver {

        public static final String ACTION_RESP =
                "Message Processed";

        @Override
        public void onReceive(Context context, Intent intent) {

            try {
                Log.i("PLaylist Fragment : ","12345 PlaylistResponseReceiver");
                playlistDtoDataList.clear();
                playlistDtoDataList.addAll(VaultDatabaseHelper.getInstance(mContext.getApplicationContext())
                        .getLocalPlaylistDataByCategoriesTab(tabId));
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }

                if (playlistDtoDataList.size() >0) {
                    noPlaylistFound.setVisibility(View.GONE);
                }else
                {
                    noPlaylistFound.setVisibility(View.VISIBLE);
                }

                try {
                    Collections.sort(playlistDtoDataList, new Comparator<PlaylistDto>() {

                        @Override
                        public int compare(PlaylistDto lhs, PlaylistDto rhs) {
                            return lhs.getPlaylistName().toLowerCase()
                                    .compareTo(rhs.getPlaylistName().toLowerCase());
                        }
                    });

                    if (playlistDtoDataList != null) {
                        if (playlistDtoDataList.size() > 0) {
                            for (int j = 0; j < playlistDtoDataList.size(); j++) {
                                if ((j + 1) % 7 == 0) {
                                    String adUnitVault = getUnitId(playlistDtoDataList);
                                    PlaylistDto playlistDto = new PlaylistDto();
                                    playlistDto.setPlaylistName(adUnitVault);
                                    playlistDtoDataList.add(j, playlistDto);
                                }
                            }

                            mAlbumsAdapter = new PlaylistDataAdapter(getActivity(), PlaylistFragment.this, playlistDtoDataList);
                            mRecyclerView.setAdapter(mAlbumsAdapter);
                            mRecyclerView.removeOnItemTouchListener(disable);
                            refreshLayout.setRefreshing(false);
                        }
                        // ------- update BannerImage---------------------
                        tabBannerDTO = VaultDatabaseHelper.getInstance(mContext.getApplicationContext())
                                .getLocalTabBannerDataByTabId(tabBannerDTO.getTabId());
                        if (tabBannerDTO != null)
                            showBannerImage(bannerImageView, tabBannerDTO);

                    } else {
                        ((HomeScreen) mContext).showToastMessage(GlobalConstants.MSG_CONNECTION_TIMEOUT);
                        refreshLayout.setRefreshing(false);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
