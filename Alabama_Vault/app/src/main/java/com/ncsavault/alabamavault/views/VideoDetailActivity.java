package com.ncsavault.alabamavault.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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

import com.baoyz.widget.PullRefreshLayout;
import com.ncsavault.alabamavault.R;
import com.ncsavault.alabamavault.adapters.SavedVideoAdapter;
import com.ncsavault.alabamavault.adapters.VideoDetailAdapter;
import com.ncsavault.alabamavault.controllers.AppController;
import com.ncsavault.alabamavault.database.VaultDatabaseHelper;
import com.ncsavault.alabamavault.dto.VideoDTO;
import com.ncsavault.alabamavault.fragments.views.VideoDetailFragment;
import com.ncsavault.alabamavault.globalconstants.GlobalConstants;
import com.ncsavault.alabamavault.service.TrendingFeaturedVideoService;
import com.ncsavault.alabamavault.utils.Utils;

import java.util.ArrayList;

public class VideoDetailActivity extends AppCompatActivity implements VideoDetailAdapter.VideoClickListener {

    private static Context mContext;
    private RecyclerView mRecyclerView;
    private ProgressBar progressBar;
    private VideoDetailAdapter videoDetailAdapter;
    private ArrayList<VideoDTO> videoDtoArrayList = new ArrayList<>();
    AsyncTask<Void, Void, Void> mPostTask;
    private boolean isFavoriteChecked;
    private String postResult;
    private PullRefreshLayout refreshLayout;
    PullRefreshTask pullTask;
    long playlistId = 0;
    private TextView tvNoRecoredFound;
    Animation animation;

    public Toolbar mToolbar;
    public ImageView imageviewSearch;
    public EditText editTextSearch;
    public ImageView imageviewLogo;
    LinearLayout linearLayoutToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.saved_video_fragment_layout);
        initComponenents();

    }

    private void initComponenents() {
        mContext = this;
        linearLayoutToolBar = (LinearLayout) findViewById(R.id.container_toolbar);
        linearLayoutToolBar.setVisibility(View.VISIBLE);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setNavigationIcon(R.drawable.back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // Implemented by activity
            }
        });
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appBar);
        appBarLayout.setExpanded(false, true);
        //toolbar.setNavigationIcon(R.drawable.ic_toolbar);
        mToolbar.setTitle("");
        mToolbar.setSubtitle("");
        imageviewSearch = (ImageView) mToolbar.findViewById(R.id.imageview_search);
        editTextSearch = (EditText) mToolbar.findViewById(R.id.editText_search);
        editTextSearch.setVisibility(View.GONE);
        imageviewLogo = (ImageView) mToolbar.findViewById(R.id.imageview_logo);


        mRecyclerView = (RecyclerView) findViewById(R.id.saved_video_recycler_view);

        tvNoRecoredFound = (TextView) findViewById(R.id.tv_no_recored_found);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);

        imageviewSearch.setVisibility(View.VISIBLE);
        imageviewSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, VideoSearchActivity.class);
                intent.putExtra("Fragment", "VideoDetailFragment");
                mContext.startActivity(intent);
            }
        });

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            progressBar.setIndeterminateDrawable(getResources().getDrawable(R.drawable.circle_progress_bar_lower));
        } else {
            System.out.println("progress bar not showing ");
            progressBar.setIndeterminateDrawable(ResourcesCompat.getDrawable(mContext.getResources(), R.drawable.progress_large_material, null));
        }

        refreshLayout = (PullRefreshLayout) findViewById(R.id.refresh_layout);

        refreshLayout.setRefreshStyle(PullRefreshLayout.STYLE_RING);
        refreshLayout.setEnabled(false);

        playlistId = getIntent().getLongExtra("playlistId", 0);

        getVideoData(playlistId);

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


    private void getVideoData(final long playlistId) {
        final AsyncTask<Void, Void, ArrayList<VideoDTO>> mDbTask = new AsyncTask<Void, Void, ArrayList<VideoDTO>>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (progressBar != null) {
                    if (videoDtoArrayList.size() == 0) {
                        progressBar.setVisibility(View.VISIBLE);
                    } else {
                        progressBar.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            protected ArrayList<VideoDTO> doInBackground(Void... params) {

                try {
                    SharedPreferences pref = AppController.getInstance().getApplicationContext().
                            getSharedPreferences(GlobalConstants.PREF_PACKAGE_NAME, Context.MODE_PRIVATE);
                    long userId = pref.getLong(GlobalConstants.PREF_VAULT_USER_ID_LONG, 0);

                    String url = GlobalConstants.PLAYLIST_VIDEO_URL + "userid=" + userId + "&playlistid=" + playlistId;
                    videoDtoArrayList.clear();
                    videoDtoArrayList.addAll(AppController.getInstance().getServiceManager().getVaultService().getNewVideoData(url));
                    VaultDatabaseHelper.getInstance(mContext).insertVideosInDatabase(videoDtoArrayList);


                } catch (Exception e) {
                    e.printStackTrace();
                }
                return videoDtoArrayList;
            }

            @Override
            protected void onPostExecute(ArrayList<VideoDTO> result) {
                super.onPostExecute(result);

                if (progressBar != null) {
                    if (result.size() == 0) {
                        progressBar.setVisibility(View.VISIBLE);
                    } else {
                        progressBar.setVisibility(View.GONE);
                    }
                }

                if (result.size() == 0) {
                    tvNoRecoredFound.setVisibility(View.VISIBLE);
                    tvNoRecoredFound.setText(GlobalConstants.NO_VIDEOS_FOUND);
                } else {
                    tvNoRecoredFound.setVisibility(View.GONE);
                }


                videoDetailAdapter = new VideoDetailAdapter(mContext, result, VideoDetailActivity.this);
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

    PullRefreshLayout.OnRefreshListener refreshListener = new PullRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            if (!Utils.isInternetAvailable(mContext)) {
                refreshLayout.setEnabled(false);
                refreshLayout.setRefreshing(false);
            } else {
                refreshLayout.setEnabled(true);
                refreshLayout.setRefreshing(true);
                pullTask = new PullRefreshTask();
                pullTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        }
    };

    @Override
    public void onClick(final VideoDetailAdapter.VideoViewHolder videoViewHolder, final int position) {

        videoViewHolder.videoRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                System.out.println("position : " + position);
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
                        //gk ((MainActivity) context).showToastMessage(GlobalConstants.MSG_NO_INFO_AVAILABLE);
                        videoViewHolder.savedVideoImageView.setImageResource(R.drawable.video_save);
                    }
                }

                videoDetailAdapter.notifyDataSetChanged();
            }
        });

    }

    public void markFavoriteStatus(final VideoDetailAdapter.VideoViewHolder viewHolder, final int pos) {
        if (Utils.isInternetAvailable(mContext)) {
            if (AppController.getInstance().getModelFacade().getLocalModel().getUserId() ==
                    GlobalConstants.DEFAULT_USER_ID) {
                viewHolder.savedVideoImageView.setImageResource(R.drawable.video_save);
                showConfirmLoginDialog(GlobalConstants.LOGIN_MESSAGE);
            } else {
                System.out.println("favorite position : " + pos);
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

                mPostTask = new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                    }

                    @Override
                    protected Void doInBackground(Void... params) {
                        try {
                            postResult = AppController.getInstance().getServiceManager().getVaultService().
                                    postFavoriteStatus(AppController.getInstance().getModelFacade().getLocalModel()
                                                    .getUserId(), videoDtoArrayList.get(pos).getVideoId(),
                                            videoDtoArrayList.get(pos).getPlaylistId(),
                                            isFavoriteChecked);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        try {
                            System.out.println("favorite position 111 : " + pos);
                            if (isFavoriteChecked) {
                                VaultDatabaseHelper.getInstance(mContext).setFavoriteFlag(1,
                                        videoDtoArrayList.get(pos).getVideoId());
                                // firebase analytics favoride video
//                                params.putString(FirebaseAnalytics.Param.ITEM_ID, arrayListVideoDTOs.get(pos).getVideoName());
//                                params.putString(FirebaseAnalytics.Param.ITEM_NAME, arrayListVideoDTOs.get(pos).getVideoName());
//                                params.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "video_favorite");
//                                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, params);

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


    public void showConfirmLoginDialog(String message) {
        AlertDialog alertDialog = null;
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        alertDialogBuilder
                .setMessage(message);
        alertDialogBuilder.setTitle("Alert");
        alertDialogBuilder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        mContext.stopService(new Intent(mContext, TrendingFeaturedVideoService.class));

                        VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).removeAllRecords();

                        SharedPreferences prefs = mContext.getSharedPreferences(GlobalConstants.PREF_PACKAGE_NAME,
                                Context.MODE_PRIVATE);
                        prefs.edit().putLong(GlobalConstants.PREF_VAULT_USER_ID_LONG, 0).commit();
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
        Button nbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        nbutton.setAllCaps(false);
        nbutton.setTextColor(mContext.getResources().getColor(R.color.apptheme_color));
        Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        pbutton.setTextColor(mContext.getResources().getColor(R.color.apptheme_color));
        pbutton.setAllCaps(false);
    }


    public class PullRefreshTask extends AsyncTask<Void, Void, ArrayList<VideoDTO>> {

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
                        getSharedPreferences(GlobalConstants.PREF_PACKAGE_NAME, Context.MODE_PRIVATE);
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

                if (result.size() == 0) {
                    tvNoRecoredFound.setVisibility(View.VISIBLE);
                    tvNoRecoredFound.setText(GlobalConstants.NO_RECORDS_FOUND);
                } else {
                    tvNoRecoredFound.setVisibility(View.GONE);
                }
                videoDetailAdapter = new VideoDetailAdapter(mContext, result, VideoDetailActivity.this);
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

    public void showToastMessage(String message) {
        View includedLayout = findViewById(R.id.llToast);

        final TextView text = (TextView) includedLayout.findViewById(R.id.tv_toast_message);
        text.setText(message);

        animation = AnimationUtils.loadAnimation(this,
                R.anim.abc_fade_in);

        text.setAnimation(animation);
        text.setVisibility(View.VISIBLE);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
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
