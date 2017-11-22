package com.ncsavault.views;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import applicationId.R;

import com.baoyz.widget.PullRefreshLayout;
import com.ncsavault.adapters.VideoDetailAdapter;
import com.ncsavault.controllers.AppController;
import com.ncsavault.database.VaultDatabaseHelper;
import com.ncsavault.dto.VideoDTO;
import com.ncsavault.fragments.views.VideoDetailFragment;
import com.ncsavault.globalconstants.GlobalConstants;
import com.ncsavault.service.TrendingFeaturedVideoService;
import com.ncsavault.utils.Utils;

import java.util.ArrayList;

/**
 * VideoDetailActivity is used to show list of videos with video details
 */
public class VideoDetailActivity extends AppCompatActivity implements VideoDetailAdapter.VideoClickListener {

    private Context mContext;
    private RecyclerView mRecyclerView;
    private ProgressBar progressBar;
    private VideoDetailAdapter videoDetailAdapter;
    private final ArrayList<VideoDTO> videoDtoArrayList = new ArrayList<>();
    private boolean isFavoriteChecked;
    private PullRefreshLayout refreshLayout;
    private long playlistId = 0;
    private TextView tvNoRecordFound;
    private Animation animation;

    private ImageView imageViewSearch;
    private ImageView imageViewBackNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.saved_video_fragment_layout);
        initComponents();

    }

    /**
     * Method is used to initialize components
     */
    private void initComponents() {
        mContext = this;

        initializeToolbar();
        playlistId = getIntent().getLongExtra("playlistId", 0);

        imageViewSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, VideoSearchActivity.class);
                intent.putExtra("Fragment", "VideoDetailFragment");
                intent.putExtra("playlist_name",getIntent().getStringExtra("playlistName"));
                intent.putExtra("playlistId",playlistId);
                mContext.startActivity(intent);
            }
        });

        imageViewBackNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mRecyclerView = findViewById(R.id.saved_video_recycler_view);

        tvNoRecordFound = findViewById(R.id.tv_no_record_found);
        progressBar = findViewById(R.id.progressbar);


        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            progressBar.setIndeterminateDrawable(ContextCompat.getDrawable(VideoDetailActivity.this, R.drawable.circle_progress_bar_lower));
        } else {
            String tag = "VideoDetail";
            Log.d(tag, "progress bar not showing ");
            progressBar.setIndeterminateDrawable(ResourcesCompat.getDrawable(mContext.getResources(), R.drawable.progress_large_material, null));
        }

        refreshLayout = findViewById(R.id.refresh_layout);

        refreshLayout.setRefreshStyle(PullRefreshLayout.STYLE_RING);
        refreshLayout.setEnabled(false);

        getVideoData(playlistId);

    }

    /**
     * Method is used to show toolbar items
     */
    private void initializeToolbar() {
        LinearLayout linearLayoutToolBar = (LinearLayout) findViewById(R.id.container_toolbar);
        linearLayoutToolBar.setVisibility(View.VISIBLE);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appBar);
        appBarLayout.setExpanded(false, true);
        mToolbar.setTitle("");
        mToolbar.setSubtitle("");
        imageViewSearch = mToolbar.findViewById(R.id.image_view_search);
        imageViewBackNavigation = mToolbar.findViewById(R.id.image_view_back);
        EditText editTextSearch = mToolbar.findViewById(R.id.editText_search);
        TextView textViewEdit = mToolbar.findViewById(R.id.textview_edit);
        textViewEdit.setVisibility(View.INVISIBLE);
        editTextSearch.setVisibility(View.INVISIBLE);
        LinearLayout linearLayoutToolbarText = mToolbar.findViewById(R.id.ll_toolbarText);
        TextView textViewToolbarText1 = mToolbar.findViewById(R.id.textview_toolbar1);
        textViewToolbarText1.setVisibility(View.GONE);
        TextView textViewToolbarText2 = mToolbar.findViewById(R.id.textview_toolbar2);
        textViewToolbarText2.setText(getIntent().getStringExtra("playlistName"));
        textViewToolbarText2.setAllCaps(true);
        Typeface faceNormal = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Regular.ttf");
        textViewToolbarText2.setTypeface(faceNormal);
        imageViewSearch.setVisibility(View.VISIBLE);
        imageViewBackNavigation.setVisibility(View.VISIBLE);
        linearLayoutToolbarText.setVisibility(View.VISIBLE);

    }


    @Override
    public void onResume() {
        super.onResume();

        if (refreshLayout != null) {
            refreshLayout.setEnabled(true);
            refreshLayout.setOnRefreshListener(refreshListener);
        }

        if (videoDetailAdapter != null) {
            videoDetailAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Method is used to get videos of particular playlist and save those videos locally
     * @param playlistId id of playlist
     */
    private void getVideoData(final long playlistId) {
        final AsyncTask<Void, Void, ArrayList<VideoDTO>> mDbTask = new AsyncTask<Void, Void, ArrayList<VideoDTO>>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (progressBar != null) {
                    progressBar.setVisibility(View.VISIBLE);

                }
            }

            @Override
            protected ArrayList<VideoDTO> doInBackground(Void... params) {

                try {
//                    SharedPreferences pref = AppController.getInstance().getApplicationContext().
//                            getSharedPreferences(getResources().getString(R.string.pref_package_name), Context.MODE_PRIVATE);
//                    long userId = pref.getLong(GlobalConstants.PREF_VAULT_USER_ID_LONG, 0);
//
//                    String url = GlobalConstants.PLAYLIST_VIDEO_URL + "userid=" + userId + "&playlistid=" + playlistId;
//                    videoDtoArrayList.clear();
//                    videoDtoArrayList.addAll(AppController.getInstance().getServiceManager().getVaultService().getNewVideoData(url));
//                    VaultDatabaseHelper.getInstance(mContext).insertVideosInDatabase(videoDtoArrayList);

                    videoDtoArrayList.clear();
                    videoDtoArrayList.addAll(VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).
                            getVideoDataByPlaylistId(playlistId));


                } catch (Exception e) {
                    e.printStackTrace();
                }
                return videoDtoArrayList;
            }

            @Override
            protected void onPostExecute(ArrayList<VideoDTO> result) {
                super.onPostExecute(result);

                if (result.size() == 0) {
                    tvNoRecordFound.setVisibility(View.VISIBLE);
                    tvNoRecordFound.setText(GlobalConstants.NO_VIDEOS_FOUND);
                } else {
                    tvNoRecordFound.setVisibility(View.GONE);
                }
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);

                }


                videoDetailAdapter = new VideoDetailAdapter(VideoDetailActivity.this, result, VideoDetailActivity.this);
                mRecyclerView.setHasFixedSize(true);
                LinearLayoutManager llm = new LinearLayoutManager(mContext);
                llm.setOrientation(LinearLayoutManager.VERTICAL);
                mRecyclerView.setLayoutManager(llm);
                mRecyclerView.setAdapter(videoDetailAdapter);
                // ------- addBannerImage---------------------
            }
        };

        mDbTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * Method used for handle the call back of pull to refresh
     * And to call the PullRefreshTask.
     */
    private final PullRefreshLayout.OnRefreshListener refreshListener = new PullRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            if (!Utils.isInternetAvailable(mContext)) {
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
    public void onClick(final VideoDetailAdapter.VideoViewHolder videoViewHolder, final int position) {

        videoViewHolder.videoRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("position","position : " + position);
                if (Utils.isInternetAvailable(mContext)) {
                    if (videoDtoArrayList.get(position).getVideoLongUrl() != null) {
                        if (videoDtoArrayList.get(position).getVideoLongUrl().length() > 0
                                && !videoDtoArrayList.get(position).getVideoLongUrl().toLowerCase().equals("none")) {
                            String videoCategory = GlobalConstants.FEATURED;
                            Intent intent = new Intent(mContext, VideoInfoActivity.class);
                            intent.putExtra(GlobalConstants.KEY_CATEGORY, videoCategory);
                            intent.putExtra(GlobalConstants.VIDEO_OBJ, videoDtoArrayList.get(position));
                            GlobalConstants.LIST_FRAGMENT = new VideoDetailFragment();
                            GlobalConstants.LIST_ITEM_POSITION = position;
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_up_video_info, R.anim.nochange);
                        } else {
                            showToastMessage(GlobalConstants.MSG_NO_INFO_AVAILABLE);
                        }
                    } else {
                        showToastMessage(GlobalConstants.MSG_NO_INFO_AVAILABLE);
                    }
                } else {
                    showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
                }
            }
        });


        videoViewHolder.mLayoutSavedImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (videoDtoArrayList.get(position).isVideoIsFavorite() && ((videoDtoArrayList.get(position)
                        .getVideoLongUrl().length() == 0 || videoDtoArrayList.get(position).getVideoLongUrl()
                        .toLowerCase().equals("none")))) {
                    markFavoriteStatus(videoViewHolder, position);
                } else {
                    if (videoDtoArrayList.get(position).getVideoLongUrl().length() > 0 && !videoDtoArrayList
                            .get(position).getVideoLongUrl().toLowerCase().equals("none")) {
                        markFavoriteStatus(videoViewHolder, position);
                    } else {

                        videoViewHolder.savedVideoImageView.setImageResource(R.drawable.video_save);
                    }
                }

                videoDetailAdapter.notifyDataSetChanged();
            }
        });

    }

    /**
     * Method is used to save video locally and make video status favourite
     * @param viewHolder the reference of view Holder class
     * @param pos position of video
     */
    private void markFavoriteStatus(final VideoDetailAdapter.VideoViewHolder viewHolder, final int pos) {
        if (Utils.isInternetAvailable(mContext)) {
            if (AppController.getInstance().getModelFacade().getLocalModel().getUserId() ==
                    GlobalConstants.DEFAULT_USER_ID) {
                viewHolder.savedVideoImageView.setImageResource(R.drawable.video_save);
                showConfirmLoginDialog();
            } else {
                Log.d("pos","favorite position : " + pos);
                if (videoDtoArrayList.get(pos).isVideoIsFavorite()) {
                    isFavoriteChecked = false;
                    VaultDatabaseHelper.getInstance(mContext).setFavoriteFlag
                            (0, videoDtoArrayList.get(pos).getVideoId());
                    videoDtoArrayList.get(pos).setVideoIsFavorite(false);
                    viewHolder.savedVideoImageView.setImageResource(R.drawable.video_save);
                } else {
                    isFavoriteChecked = true;
                    VaultDatabaseHelper.getInstance(mContext).setFavoriteFlag
                            (1, videoDtoArrayList.get(pos).getVideoId());
                    videoDtoArrayList.get(pos).setVideoIsFavorite(true);
                    viewHolder.savedVideoImageView.setImageResource(R.drawable.saved_video_img);
                }

                AsyncTask<Void, Void, Void> mPostTask = new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... params) {
                        try {
                            String postResult = AppController.getInstance().getServiceManager().getVaultService().
                                    postFavoriteStatus(AppController.getInstance().getModelFacade().getLocalModel()
                                                    .getUserId(), videoDtoArrayList.get(pos).getVideoId(),
                                            videoDtoArrayList.get(pos).getPlaylistId(),
                                            isFavoriteChecked);
                            Log.d("result", "result" + postResult);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        try {
                            Log.d("pos","favorite position: " + pos);
                            if (isFavoriteChecked) {
                                VaultDatabaseHelper.getInstance(mContext).setFavoriteFlag(1,
                                        videoDtoArrayList.get(pos).getVideoId());

                            } else {
                                VaultDatabaseHelper.getInstance(mContext).setFavoriteFlag
                                        (0, videoDtoArrayList.get(pos).getVideoId());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };

                mPostTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } else {
            showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
            viewHolder.savedVideoImageView.setImageResource(R.drawable.video_save);
        }
    }

    /**
     * Method is used to show login dialog
     */
    private void showConfirmLoginDialog() {
        AlertDialog alertDialog;
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        alertDialogBuilder
                .setMessage(GlobalConstants.LOGIN_MESSAGE);
        alertDialogBuilder.setTitle("Alert");
        alertDialogBuilder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        mContext.stopService(new Intent(mContext, TrendingFeaturedVideoService.class));

                        VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).removeAllRecords();

                        SharedPreferences prefs = mContext.getSharedPreferences(getResources().getString(R.string.pref_package_name),
                                Context.MODE_PRIVATE);
                        prefs.edit().putLong(GlobalConstants.PREF_VAULT_USER_ID_LONG, 0).apply();
//                        prefs.edit().putBoolean(GlobalConstants.PREF_PULL_OPTION_HEADER, false).commit();

                        Intent intent = new Intent(mContext, LoginEmailActivity.class);
                        mContext.startActivity(intent);
                        finish();
//                        context.finish();
                    }
                });

        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });

        alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
        Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveButton.setAllCaps(false);
        positiveButton.setTextColor(ContextCompat.getColor(VideoDetailActivity.this, R.color.app_theme_color));
        Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        negativeButton.setTextColor(ContextCompat.getColor(VideoDetailActivity.this, R.color.app_theme_color));
        negativeButton.setAllCaps(false);
    }

    /**
     * Pull to refresh async task is used to get updated videos of particular playlist
     */
    private class PullRefreshTask extends AsyncTask<Void, Void, ArrayList<VideoDTO>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mRecyclerView != null) {
                mRecyclerView.setEnabled(false);
            }

            refreshLayout.setRefreshing(true);

        }

        @Override
        protected ArrayList<VideoDTO> doInBackground(Void... params) {
            try {
                SharedPreferences pref = AppController.getInstance().getApplicationContext().
                        getSharedPreferences(getResources().getString(R.string.pref_package_name), Context.MODE_PRIVATE);
                long userId = pref.getLong(GlobalConstants.PREF_VAULT_USER_ID_LONG, 0);

                String url = GlobalConstants.PLAYLIST_VIDEO_URL + "userid=" + userId + "&playlistid=" + playlistId;
                videoDtoArrayList.clear();
                videoDtoArrayList.addAll(AppController.getInstance().getServiceManager().
                        getVaultService().getNewVideoData(url));

            } catch (Exception e) {
                e.printStackTrace();
            }
            return videoDtoArrayList;
        }

        @Override
        protected void onPostExecute(final ArrayList<VideoDTO> result) {
            super.onPostExecute(result);
            try {

                videoDtoArrayList.clear();
                videoDtoArrayList.addAll(VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).
                        getVideoDataByPlaylistId(playlistId));

                if (videoDtoArrayList.size() == 0) {
                    tvNoRecordFound.setVisibility(View.VISIBLE);
                    tvNoRecordFound.setText(GlobalConstants.NO_RECORDS_FOUND);
                } else {
                    tvNoRecordFound.setVisibility(View.GONE);
                }
                videoDetailAdapter = new VideoDetailAdapter(VideoDetailActivity.this, videoDtoArrayList, VideoDetailActivity.this);
                mRecyclerView.setHasFixedSize(true);
                LinearLayoutManager llm = new LinearLayoutManager(mContext);
                llm.setOrientation(LinearLayoutManager.VERTICAL);
                mRecyclerView.setLayoutManager(llm);
                mRecyclerView.setAdapter(videoDetailAdapter);
                refreshLayout.setRefreshing(false);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Method is used to show toast message
     * @param message set the message.
     */
    @SuppressLint("PrivateResource")
    private void showToastMessage(String message) {
        View includedLayout = findViewById(R.id.llToast);

        final TextView text = includedLayout.findViewById(R.id.tv_toast_message);
        text.setText(message);

        animation = AnimationUtils.loadAnimation(this,
                R.anim.abc_fade_in);

        text.setAnimation(animation);
        text.setVisibility(View.VISIBLE);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @SuppressLint("PrivateResource")
            @Override
            public void run() {
                animation = AnimationUtils.loadAnimation(VideoDetailActivity.this,
                        R.anim.abc_fade_out);

                text.setAnimation(animation);
                text.setVisibility(View.GONE);
            }
        }, 2000);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
