package com.ncsavault.fragments.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.baoyz.widget.PullRefreshLayout;
import com.ncsavault.adapters.SavedVideoAdapter;
import com.ncsavault.controllers.AppController;
import com.ncsavault.customviews.RecyclerViewDisable;
import com.ncsavault.database.VaultDatabaseHelper;
import com.ncsavault.dto.VideoDTO;
import com.ncsavault.globalconstants.GlobalConstants;
import com.ncsavault.utils.Utils;
import com.ncsavault.views.HomeScreen;
import com.ncsavault.views.LoginEmailActivity;
import com.ncsavault.views.VideoInfoActivity;
import com.ncsavault.views.VideoSearchActivity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import applicationId.R;

/**
 * Class used for Saved the favorite video list.
 * Also we can play this video from the saved list.
 */

public class SavedVideoFragment extends Fragment implements SavedVideoAdapter.SavedClickListener {

    @SuppressLint("StaticFieldLeak")
    private static Context mContext;
    private RecyclerView mRecyclerView;
    private ArrayList<VideoDTO> favoriteVideoList = new ArrayList<>();
    private SavedVideoAdapter savedVideoAdapter;
    private PullRefreshLayout refreshLayout;
    private TextView tvNoRecordFound;
    private Button tvLoginButton;
    private RecyclerView.OnItemTouchListener disable;

    /**
     * Method used for create a new instance of Saved screen.
     * @param context The reference of Context.
     * @return The instance of fragment.
     */
    public static Fragment newInstance(Context context) {
        Fragment frag = new SavedVideoFragment();

        mContext = context;

        return frag;
    }

    @SuppressWarnings("UnusedAssignment")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.saved_video_fragment_layout, container, false);

    }

    /**
     * Method used for handle the click event of view.
     */
    private void initListener() {
        tvLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences pref = mContext.getSharedPreferences(mContext.getResources()
                                .getString(R.string.pref_package_name),
                        Context.MODE_PRIVATE);
                pref.edit().putLong(GlobalConstants.PREF_VAULT_USER_ID_LONG, 0).apply();
                pref.edit().putString(GlobalConstants.PREF_VAULT_USER_NAME, "").apply();
                pref.edit().putString(GlobalConstants.PREF_VAULT_USER_EMAIL, "").apply();

                VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).removeAllRecords();
                Intent intent = new Intent(mContext, LoginEmailActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                ((HomeScreen) mContext).finish();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        if (refreshLayout != null) {
            refreshLayout.setEnabled(true);
            refreshLayout.setOnRefreshListener(refreshListener);
        }

        if (GlobalConstants.IS_RETURNED_FROM_PLAYER) {
            if (mContext != null) {
                favoriteVideoList.clear();
                favoriteVideoList.addAll(VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).
                        getFavouriteVideosArrayList());
                Log.d("size","favoriteVideoList size onResume: " + favoriteVideoList.size());
                savedVideoAdapter = new SavedVideoAdapter(getActivity(), favoriteVideoList,
                        SavedVideoFragment.this);
                mRecyclerView.setAdapter(savedVideoAdapter);
                GlobalConstants.IS_RETURNED_FROM_PLAYER = false;
                if (favoriteVideoList.size() == 0) {
                    tvNoRecordFound.setVisibility(View.VISIBLE);
                    tvNoRecordFound.setText(GlobalConstants.NO_RECORDS_FOUND);
                } else {
                    tvNoRecordFound.setVisibility(View.GONE);
                }
            }
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
                if(!favoriteVideoList.isEmpty())
                {
                    favoriteVideoList.clear();
                }
                PullRefreshTask pullTask = new PullRefreshTask();
                pullTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        }
    };

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = view.findViewById(R.id.saved_video_recycler_view);
        disable = new RecyclerViewDisable();
        tvNoRecordFound = view.findViewById(R.id.tv_no_record_found);
        RelativeLayout savedViewLayout = view.findViewById(R.id.saved_view_layout);
        RelativeLayout savedLoginLayout = view.findViewById(R.id.saved_login_layout);
        tvLoginButton = view.findViewById(R.id.tv_login);

        refreshLayout = view.findViewById(R.id.refresh_layout);

        refreshLayout.setRefreshStyle(PullRefreshLayout.STYLE_RING);
        refreshLayout.setEnabled(false);

        SharedPreferences prefs = mContext.getSharedPreferences(mContext.getResources()
                .getString(R.string.pref_package_name), Context.MODE_PRIVATE);
        long userId = prefs.getLong(GlobalConstants.PREF_VAULT_USER_ID_LONG, 0);

        setToolbarIcons();

        if (userId == GlobalConstants.DEFAULT_USER_ID) {
            savedLoginLayout.setVisibility(View.VISIBLE);
            savedViewLayout.setVisibility(View.GONE);
            ((HomeScreen) mContext).imageViewSearch.setVisibility(View.INVISIBLE);
        } else {
            ((HomeScreen) mContext).imageViewSearch.setVisibility(View.VISIBLE);
            savedLoginLayout.setVisibility(View.GONE);
            savedViewLayout.setVisibility(View.VISIBLE);
        }


        ((HomeScreen) getActivity()).imageViewSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, VideoSearchActivity.class);
                intent.putExtra("Fragment", "SavedVideoFragment");
                mContext.startActivity(intent);
            }
        });

        initListener();
        getFavoriteDataFromDataBase();

    }

    /**
     * Method used for set the toolbar icons and text in Saved video screen.
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
     * Get the favorite video from database
     */
    private void getFavoriteDataFromDataBase() {
        try {

            try {
                favoriteVideoList.clear();
                favoriteVideoList.addAll(VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).
                        getFavouriteVideosArrayList());
                Log.d("size","favoriteVideoList size getFavoriteDataFromDataBase : " +
                        favoriteVideoList.size());
                Collections.sort(favoriteVideoList, new Comparator<VideoDTO>() {

                    @Override
                    public int compare(VideoDTO lhs, VideoDTO rhs) {

                        return lhs.getVideoName().toLowerCase()
                                .compareTo(rhs.getVideoName().toLowerCase());
                    }
                });

                savedVideoAdapter = new SavedVideoAdapter(getActivity(), favoriteVideoList,
                        SavedVideoFragment.this);
                if (favoriteVideoList.size() == 0) {
                    tvNoRecordFound.setVisibility(View.VISIBLE);
                    tvNoRecordFound.setText(GlobalConstants.NO_RECORDS_FOUND);
                } else {
                    tvNoRecordFound.setVisibility(View.GONE);
                }


                mRecyclerView.setHasFixedSize(true);
                LinearLayoutManager llm = new LinearLayoutManager(mContext);
                llm.setOrientation(LinearLayoutManager.VERTICAL);
                mRecyclerView.setLayoutManager(llm);
                mRecyclerView.setItemAnimator(new DefaultItemAnimator());
                mRecyclerView.setAdapter(savedVideoAdapter);
            } catch (Exception e) {
                e.printStackTrace();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(final SavedVideoAdapter.SavedVideoViewHolder viewHolder, final int pos) {

        viewHolder.videoRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("position : ","position : " + pos);
                if (Utils.isInternetAvailable(mContext)) {
                    if (favoriteVideoList.get(pos).getVideoLongUrl() != null) {
                        if (favoriteVideoList.get(pos).getVideoLongUrl().length() > 0
                                && !favoriteVideoList.get(pos).getVideoLongUrl().toLowerCase().equals("none")) {
                            String videoCategory = GlobalConstants.FEATURED;
                            GlobalConstants.IS_RETURNED_FROM_PLAYER = true;
                            Intent intent = new Intent(mContext, VideoInfoActivity.class);
                            intent.putExtra(GlobalConstants.KEY_CATEGORY, videoCategory);
                            intent.putExtra(GlobalConstants.VIDEO_OBJ, favoriteVideoList.get(pos));
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

        viewHolder.mLayoutSavedImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savedVideoAdapter.notifyDataSetChanged();
            }
        });
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
                try {
                    String url = GlobalConstants.FAVORITE_API_URL + "userId=" + userId;

                     favoriteVideoList.addAll(AppController.getInstance().getServiceManager().getVaultService()
                            .getVideosListFromServer(url));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return favoriteVideoList;
        }

        @Override
        protected void onPostExecute(final ArrayList<VideoDTO> result) {
            super.onPostExecute(result);

            try {
                VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).setAllFavoriteStatusToFalse();
                for (VideoDTO vidDto : result) {
                    if (VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).
                            checkVideoAvailability(vidDto.getVideoId())) {
                        VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).
                                setFavoriteFlag(1, vidDto.getVideoId());
                    }
                }
                favoriteVideoList.clear();
                favoriteVideoList.addAll(VaultDatabaseHelper.getInstance(mContext.getApplicationContext())
                        .getFavouriteVideosArrayList());

                Collections.sort(favoriteVideoList, new Comparator<VideoDTO>() {

                    @Override
                    public int compare(VideoDTO lhs, VideoDTO rhs) {

                        return lhs.getVideoName().toLowerCase()
                                .compareTo(rhs.getVideoName().toLowerCase());
                    }
                });


                savedVideoAdapter = new SavedVideoAdapter(getActivity(), favoriteVideoList,
                        SavedVideoFragment.this);
                mRecyclerView.setHasFixedSize(true);
                LinearLayoutManager llm = new LinearLayoutManager(mContext);
                llm.setOrientation(LinearLayoutManager.VERTICAL);
                mRecyclerView.setLayoutManager(llm);
                mRecyclerView.setAdapter(savedVideoAdapter);
                mRecyclerView.removeOnItemTouchListener(disable);
                refreshLayout.setRefreshing(false);

                if (favoriteVideoList.size() == 0) {
                    tvNoRecordFound.setVisibility(View.VISIBLE);
                    tvNoRecordFound.setText(GlobalConstants.NO_RECORDS_FOUND);
                } else {
                    tvNoRecordFound.setVisibility(View.GONE);
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}

