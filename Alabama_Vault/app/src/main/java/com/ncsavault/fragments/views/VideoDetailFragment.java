package com.ncsavault.fragments.views;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import applicationId.R;

import com.baoyz.widget.PullRefreshLayout;
import com.ncsavault.adapters.VideoDetailAdapter;
import com.ncsavault.controllers.AppController;

import com.ncsavault.customviews.RecyclerViewDisable;
import com.ncsavault.database.VaultDatabaseHelper;
import com.ncsavault.dto.VideoDTO;
import com.ncsavault.globalconstants.GlobalConstants;
import com.ncsavault.service.TrendingFeaturedVideoService;
import com.ncsavault.utils.Utils;
import com.ncsavault.views.HomeScreen;
import com.ncsavault.views.LoginEmailActivity;
import com.ncsavault.views.VideoInfoActivity;
import com.ncsavault.views.VideoSearchActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Class used for show the detail of video
 * Video name, video description and video length
 * Also we can make a favorite video.
 */

public class VideoDetailFragment extends Fragment implements VideoDetailAdapter.VideoClickListener {

    @SuppressLint("StaticFieldLeak")
    private static Context mContext;
    private RecyclerView mRecyclerView;
    private VideoDetailAdapter videoDetailAdapter;
    private final ArrayList<VideoDTO> videoDtoArrayList = new ArrayList<>();
    private boolean isFavoriteChecked;
    private PullRefreshLayout refreshLayout;
    private long playlistId = 0;
    private TextView tvNoRecordFound;
    private RecyclerView.OnItemTouchListener disable;
    private VideoResponseReceiver receiver;
    private String playlistName = "";


    /**
     * Method used for create a new instance of Saved screen.
     * @param context The reference of Context.
     * @param playlistId Get the playlist id and show the list of particular playlist.
     * @param playlistName Get the playlist name to show the top of the screen.
     * @return The new instance of fragment.
     */
    public static Fragment newInstance(Context context, long playlistId, String playlistName) {
        Fragment videoDetailFragment = new VideoDetailFragment();
        mContext = context;
        Bundle args = new Bundle();
        args.putLong("playlistId", playlistId);
        args.putString("playlist_name", playlistName);
        videoDetailFragment.setArguments(args);
        return videoDetailFragment;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.saved_video_fragment_layout, container, false);

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
                if(!videoDtoArrayList.isEmpty())
                {
                    videoDtoArrayList.clear();
                }
                PullRefreshTask pullTask = new PullRefreshTask();
                pullTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        }
    };


    @SuppressWarnings("deprecation")
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = view.findViewById(R.id.saved_video_recycler_view);
        disable = new RecyclerViewDisable();
        tvNoRecordFound = view.findViewById(R.id.tv_no_record_found);
        ProgressBar progressBar = view.findViewById(R.id.progressbar);

        IntentFilter filter = new IntentFilter(VideoResponseReceiver.ACTION_RESP);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new VideoResponseReceiver();
        getActivity().registerReceiver(receiver, filter);

        setToolbarIcons();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            progressBar.setIndeterminateDrawable(getResources().getDrawable(R.drawable.circle_progress_bar_lower));
        else
            progressBar.setIndeterminateDrawable(ResourcesCompat.getDrawable(getResources(),
                    R.drawable.progress_large_material, null));

        refreshLayout = view.findViewById(R.id.refresh_layout);

        refreshLayout.setRefreshStyle(PullRefreshLayout.STYLE_RING);
        refreshLayout.setEnabled(false);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            playlistId = bundle.getLong("playlistId", 0);
            playlistName = bundle.getString("playlist_name");
            ((HomeScreen) mContext).textViewToolbarText2.setText(playlistName);
            ((HomeScreen) mContext).textViewToolbarText2.setAllCaps(true);
             Typeface faceNormal = Typeface.createFromAsset(mContext.getAssets(), "fonts/OpenSans-Regular.ttf");
            ((HomeScreen) getActivity()).textViewToolbarText2.setTypeface(faceNormal);
        }

        ((HomeScreen) mContext).imageViewSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, VideoSearchActivity.class);
                intent.putExtra("Fragment", "VideoDetailFragment");
                intent.putExtra("playlistId", playlistId);
                intent.putExtra("playlist_name", playlistName);
                mContext.startActivity(intent);
            }
        });

        ((HomeScreen) mContext).imageViewBackNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        getVideoDataFromDataBase(playlistId);

    }

    /**
     * Method used for set the toolbar icons and text in video screen.
     */
    private void setToolbarIcons() {
        ((HomeScreen) mContext).linearLayoutToolbarText.setVisibility(View.VISIBLE);
        ((HomeScreen) mContext).imageViewSearch.setVisibility(View.VISIBLE);
        ((HomeScreen) mContext).textViewEdit.setVisibility(View.INVISIBLE);
        ((HomeScreen) mContext).imageViewBackNavigation.setVisibility(View.VISIBLE);
        ((HomeScreen) mContext).textViewToolbarText1.setVisibility(View.GONE);
    }

    /**
     * Method used for get the video detail from database
     */
    private void getVideoDataFromDataBase(final long playlistId) {

        final AsyncTask<Void, Void, ArrayList<VideoDTO>> mDbTask = new AsyncTask<Void, Void, ArrayList<VideoDTO>>() {

            @Override
            protected ArrayList<VideoDTO> doInBackground(Void... params) {

                try {

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

                if (videoDtoArrayList.size() == 0) {
                    tvNoRecordFound.setVisibility(View.VISIBLE);
                    tvNoRecordFound.setText(GlobalConstants.NO_VIDEO_FOUND);
                } else {
                    tvNoRecordFound.setVisibility(View.GONE);
                }

                Collections.sort(videoDtoArrayList, new Comparator<VideoDTO>() {
                    @Override
                    public int compare(VideoDTO lhs, VideoDTO rhs) {
                        return lhs.getVideoName().toLowerCase()
                                .compareTo(rhs.getVideoName().toLowerCase());
                    }
                });


                videoDetailAdapter = new VideoDetailAdapter(getActivity(), videoDtoArrayList, VideoDetailFragment.this);
                mRecyclerView.setHasFixedSize(true);
                LinearLayoutManager llm = new LinearLayoutManager(mContext);
                llm.setOrientation(LinearLayoutManager.VERTICAL);
                mRecyclerView.setLayoutManager(llm);
                mRecyclerView.setAdapter(videoDetailAdapter);

            }
        };

        mDbTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    @Override
    public void onClick(final VideoDetailAdapter.VideoViewHolder videoViewHolder, final int pos) {

        videoViewHolder.videoRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("position : ", "position : " + pos);
                if (Utils.isInternetAvailable(mContext)) {
                    if (videoDtoArrayList.get(pos).getVideoLongUrl() != null) {
                        if (videoDtoArrayList.get(pos).getVideoLongUrl().length() > 0
                                && !videoDtoArrayList.get(pos).getVideoLongUrl().toLowerCase().equals("none")) {
                            String videoCategory = GlobalConstants.FEATURED;
                            Intent intent = new Intent(mContext, VideoInfoActivity.class);
                            intent.putExtra(GlobalConstants.KEY_CATEGORY, videoCategory);
                            intent.putExtra(GlobalConstants.VIDEO_OBJ, videoDtoArrayList.get(pos));
                            GlobalConstants.LIST_FRAGMENT = new VideoDetailFragment();
                            GlobalConstants.LIST_ITEM_POSITION = pos;
                            startActivity(intent);
                            ((HomeScreen) mContext).overridePendingTransition(R.anim.slide_up_video_info, R.anim.nochange);
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
        });


        videoViewHolder.mLayoutSavedImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (videoDtoArrayList.get(pos).isVideoIsFavorite() && ((videoDtoArrayList.get(pos)
                        .getVideoLongUrl().length() == 0 || videoDtoArrayList.get(pos).getVideoLongUrl()
                        .toLowerCase().equals("none")))) {
                    markFavoriteStatus(videoViewHolder, pos);
                } else {
                    if (videoDtoArrayList.get(pos).getVideoLongUrl().length() > 0 && !videoDtoArrayList
                            .get(pos).getVideoLongUrl().toLowerCase().equals("none")) {
                        markFavoriteStatus(videoViewHolder, pos);
                    } else {
                        videoViewHolder.savedVideoImageView.setImageResource(R.drawable.video_save);
                    }
                }

                videoDetailAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * Method used for make it particular video favorite.
     * @param viewHolder Class reference of view holder
     * @param pos Set the position of particular video item.
     */
    private void markFavoriteStatus(final VideoDetailAdapter.VideoViewHolder viewHolder, final int pos) {
        if (Utils.isInternetAvailable(mContext)) {
            if (AppController.getInstance().getModelFacade().getLocalModel().getUserId() ==
                    GlobalConstants.DEFAULT_USER_ID) {
                viewHolder.savedVideoImageView.setImageResource(R.drawable.video_save);
                showConfirmLoginDialog();
            } else {
                Log.d("favorite position : ","favorite position : " + pos);
                if (videoDtoArrayList.get(pos).isVideoIsFavorite()) {
                    isFavoriteChecked = false;
                    VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).setFavoriteFlag
                            (0, videoDtoArrayList.get(pos).getVideoId());
                    videoDtoArrayList.get(pos).setVideoIsFavorite(false);
                    viewHolder.savedVideoImageView.setImageResource(R.drawable.video_save);
                } else {
                    isFavoriteChecked = true;
                    VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).setFavoriteFlag
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
                            Log.d("favourite position", "favourite position" + pos);
                            if (isFavoriteChecked) {
                                VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).setFavoriteFlag(1,
                                        videoDtoArrayList.get(pos).getVideoId());

                            } else {
                                VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).setFavoriteFlag
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
            ((HomeScreen) mContext).showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
            viewHolder.savedVideoImageView.setImageResource(R.drawable.video_save);
        }
    }

    /**
     * Method used for show the confirmation dialog box
     * If user are not login in app.
     */
    @SuppressWarnings("deprecation")
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

                        SharedPreferences prefs = mContext.getSharedPreferences(mContext.getResources()
                                        .getString(R.string.pref_package_name),
                                Context.MODE_PRIVATE);
                        prefs.edit().putLong(GlobalConstants.PREF_VAULT_USER_ID_LONG, 0).apply();

                        Intent intent = new Intent(mContext, LoginEmailActivity.class);
                        mContext.startActivity(intent);
                        ((HomeScreen) mContext).finish();

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
        positiveButton.setTextColor(mContext.getResources().getColor(R.color.app_theme_color));
        Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        negativeButton.setTextColor(mContext.getResources().getColor(R.color.app_theme_color));
        negativeButton.setAllCaps(false);
    }

    /**
     * Class used to get the updated data from server using Async task
     * Used for to handle the pull to refresh functionality.
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

        @Override
        protected ArrayList<VideoDTO> doInBackground(Void... params) {
            try {
                SharedPreferences pref = AppController.getInstance().getApplicationContext().
                        getSharedPreferences(mContext.getResources()
                                .getString(R.string.pref_package_name), Context.MODE_PRIVATE);
                long userId = pref.getLong(GlobalConstants.PREF_VAULT_USER_ID_LONG, 0);

                String url = GlobalConstants.PLAYLIST_VIDEO_URL + "userid=" + userId + "&playlistid=" + playlistId;
                videoDtoArrayList.addAll(AppController.getInstance().getServiceManager().
                        getVaultService().getNewVideoData(url));

                for (VideoDTO videoDTO : videoDtoArrayList) {
                    VideoDTO localVideoDTO = VaultDatabaseHelper.getInstance(mContext)
                            .getVideoDtoByPlaylistId(videoDTO.getPlaylistId());

                    if (localVideoDTO != null) {
                        if (localVideoDTO.getVedioList_modified() != videoDTO.getVedioList_modified()) {


                            VaultDatabaseHelper.getInstance(mContext).
                                    removeVideoByPlaylistId(playlistId);
                            VaultDatabaseHelper.getInstance(mContext).
                                    insertVideosInDatabase(videoDtoArrayList);
                        }
                    } else {
                        VaultDatabaseHelper.getInstance(mContext).
                                insertVideosInDatabase(videoDtoArrayList);
                    }

                }

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
                    tvNoRecordFound.setText(GlobalConstants.NO_VIDEO_FOUND);
                } else {
                    tvNoRecordFound.setVisibility(View.GONE);
                }

                Collections.sort(videoDtoArrayList, new Comparator<VideoDTO>() {
                    @Override
                    public int compare(VideoDTO lhs, VideoDTO rhs) {
                        return lhs.getVideoName().toLowerCase()
                                .compareTo(rhs.getVideoName().toLowerCase());
                    }
                });


                videoDetailAdapter = new VideoDetailAdapter(getActivity(), videoDtoArrayList, VideoDetailFragment.this);
                mRecyclerView.setHasFixedSize(true);
                LinearLayoutManager llm = new LinearLayoutManager(mContext);
                llm.setOrientation(LinearLayoutManager.VERTICAL);
                mRecyclerView.setLayoutManager(llm);
                mRecyclerView.setAdapter(videoDetailAdapter);
                mRecyclerView.removeOnItemTouchListener(disable);
                refreshLayout.setRefreshing(false);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This broadcast receiver class used for show the video detail when service will be start
     * at the time of app launch.
     */
    public class VideoResponseReceiver extends BroadcastReceiver {

        public static final String ACTION_RESP =
                "Message Processed";

        @Override
        public void onReceive(Context context, Intent intent) {

            try {
                videoDtoArrayList.clear();
                videoDtoArrayList.addAll(VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).
                        getVideoDataByPlaylistId(playlistId));

                if (videoDtoArrayList.size() == 0) {
                    tvNoRecordFound.setVisibility(View.VISIBLE);
                    tvNoRecordFound.setText(GlobalConstants.NO_VIDEO_FOUND);
                } else {
                    tvNoRecordFound.setVisibility(View.GONE);
                }

                Collections.sort(videoDtoArrayList, new Comparator<VideoDTO>() {
                    @Override
                    public int compare(VideoDTO lhs, VideoDTO rhs) {
                        return lhs.getVideoName().toLowerCase()
                                .compareTo(rhs.getVideoName().toLowerCase());
                    }
                });


                videoDetailAdapter = new VideoDetailAdapter(getActivity(), videoDtoArrayList, VideoDetailFragment.this);
                mRecyclerView.setHasFixedSize(true);
                LinearLayoutManager llm = new LinearLayoutManager(mContext);
                llm.setOrientation(LinearLayoutManager.VERTICAL);
                mRecyclerView.setLayoutManager(llm);
                mRecyclerView.setAdapter(videoDetailAdapter);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}