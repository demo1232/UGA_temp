package com.ncsavault.alabamavault.fragments.views;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.baoyz.widget.PullRefreshLayout;
import com.ncsavault.alabamavault.AsyncTask.PullRefreshTask;
import com.ncsavault.alabamavault.R;
import com.ncsavault.alabamavault.adapters.SavedVideoAdapter;
import com.ncsavault.alabamavault.adapters.VideoContentListAdapter;
import com.ncsavault.alabamavault.adapters.VideoDetailAdapter;
import com.ncsavault.alabamavault.controllers.AppController;
import com.ncsavault.alabamavault.database.VaultDatabaseHelper;
import com.ncsavault.alabamavault.dto.VideoDTO;
import com.ncsavault.alabamavault.globalconstants.GlobalConstants;
import com.ncsavault.alabamavault.service.TrendingFeaturedVideoService;
import com.ncsavault.alabamavault.utils.Utils;
import com.ncsavault.alabamavault.views.HomeScreen;
import com.ncsavault.alabamavault.views.LoginEmailActivity;
import com.ncsavault.alabamavault.views.VideoInfoActivity;
import com.ncsavault.alabamavault.views.VideoSearchActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by gauravkumar.singh on 6/12/2017.
 */

public class SavedVideoFragment extends Fragment implements SavedVideoAdapter.SavedClickListener {

    private static Context mContext;
    RecyclerView mRecyclerView;
    private ArrayList<VideoDTO> favoriteVideoList = new ArrayList<>();
    private SavedVideoAdapter savedVideoAdapter;
    private PullRefreshLayout refreshLayout;
    PullRefreshTask pullTask;
    private ProgressBar progressBar;
    private TextView tvNoRecoredFound;
    private RelativeLayout savedViewLayout,savedLoginLayout;
    SharedPreferences prefs;
    private Button tvLoginButton;
    AsyncTask<Void, Void, Void> mPostTask;
    private boolean isFavoriteChecked;
    private String postResult;

    public static Fragment newInstance(Context context) {
        Fragment frag = new SavedVideoFragment();

        mContext = context;
        Bundle args = new Bundle();
        return frag;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.saved_video_fragment_layout, container, false);


    }



    private void initListener()
    {
        tvLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences pref = mContext.getSharedPreferences(GlobalConstants.PREF_PACKAGE_NAME,
                        Context.MODE_PRIVATE);
                pref.edit().putLong(GlobalConstants.PREF_VAULT_USER_ID_LONG, 0).apply();
                pref.edit().putString(GlobalConstants.PREF_VAULT_USER_NAME, "").apply();
                pref.edit().putString(GlobalConstants.PREF_VAULT_USER_EMAIL, "").apply();

                VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).removeAllRecords();
                Intent intent = new Intent(mContext, LoginEmailActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                ((HomeScreen)mContext).finish();
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
                System.out.println("favoriteVideoList size onResume: " + favoriteVideoList.size());
                savedVideoAdapter = new SavedVideoAdapter(mContext, favoriteVideoList, SavedVideoFragment.this);
                mRecyclerView.setAdapter(savedVideoAdapter);
                savedVideoAdapter.notifyDataSetChanged();
                GlobalConstants.IS_RETURNED_FROM_PLAYER = false;
            }
        }
    }

    PullRefreshLayout.OnRefreshListener refreshListener = new PullRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            if (!Utils.isInternetAvailable(mContext.getApplicationContext())) {
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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.saved_video_recycler_view);
        tvNoRecoredFound = (TextView) view.findViewById(R.id.tv_no_recored_found);
        progressBar = (ProgressBar) view.findViewById(R.id.progressbar);
        savedViewLayout = (RelativeLayout) view.findViewById(R.id.saved_view_layout);
        savedLoginLayout = (RelativeLayout) view.findViewById(R.id.saved_login_layout);
        tvLoginButton = (Button) view.findViewById(R.id.tv_login);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            progressBar.setIndeterminateDrawable(mContext.getResources().getDrawable(R.drawable.circle_progress_bar_lower));
        } else {
            System.out.println("progress bar not showing ");
            progressBar.setIndeterminateDrawable(ResourcesCompat.getDrawable(mContext.getResources(),
                    R.drawable.progress_large_material, null));
        }
        refreshLayout = (PullRefreshLayout) view.findViewById(R.id.refresh_layout);

        refreshLayout.setRefreshStyle(PullRefreshLayout.STYLE_RING);
        refreshLayout.setEnabled(false);

        prefs = mContext.getSharedPreferences(GlobalConstants.PREF_PACKAGE_NAME, Context.MODE_PRIVATE);
        long userId = prefs.getLong(GlobalConstants.PREF_VAULT_USER_ID_LONG, 0);

        if(userId == GlobalConstants.DEFAULT_USER_ID)
        {
            savedLoginLayout.setVisibility(View.VISIBLE);
            savedViewLayout.setVisibility(View.GONE);
        }else
        {
            savedLoginLayout.setVisibility(View.GONE);
            savedViewLayout.setVisibility(View.VISIBLE);
        }

        setToolbarIcons();

        ((HomeScreen)getActivity()).imageViewSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, VideoSearchActivity.class);
                intent.putExtra("Fragment", "SavedVideoFragment");
                mContext.startActivity(intent);
            }
        });

        initListener();
        getFavoriteDataFromDataBase();

    }

    private void setToolbarIcons() {
        ((HomeScreen)mContext).imageViewSearch.setVisibility(View.VISIBLE);
        ((HomeScreen)mContext).imageViewLogo.setVisibility(View.VISIBLE);
        ((HomeScreen)mContext).textViewEdit.setVisibility(View.INVISIBLE);
        ((HomeScreen)mContext).imageViewBackNavigation.setVisibility(View.INVISIBLE);
    }

    /**
     * Get the favorite video from database
     */
    private void getFavoriteDataFromDataBase() {
        try {

            AsyncTask<Void, Void, ArrayList<VideoDTO>> mDbTask = new AsyncTask<Void, Void, ArrayList<VideoDTO>>() {

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();

                    if(progressBar != null)
                    {
                        progressBar.setVisibility(View.VISIBLE);
                    }

                    if (savedVideoAdapter != null) {
                        savedVideoAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                protected ArrayList<VideoDTO> doInBackground(Void... params) {
                    try {
                        favoriteVideoList.clear();
                        favoriteVideoList.addAll(VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).
                                getFavouriteVideosArrayList());
                        System.out.println("favoriteVideoList size getFavoriteDataFromDataBase : " +
                                favoriteVideoList.size());
                        Collections.sort(favoriteVideoList, new Comparator<VideoDTO>() {

                            @Override
                            public int compare(VideoDTO lhs, VideoDTO rhs) {
                                // TODO Auto-generated method stub
                                return lhs.getVideoName().toLowerCase()
                                        .compareTo(rhs.getVideoName().toLowerCase());
                            }
                        });

                        savedVideoAdapter  = new SavedVideoAdapter(mContext,favoriteVideoList, SavedVideoFragment.this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return favoriteVideoList;
                }

                @Override
                protected void onPostExecute(ArrayList<VideoDTO> list) {
                    super.onPostExecute(list);

                    if(progressBar != null)
                    {
                        progressBar.setVisibility(View.GONE);
                    }

                    if(list.size() == 0)
                    {
                        tvNoRecoredFound.setVisibility(View.VISIBLE);
                        tvNoRecoredFound.setText(GlobalConstants.NO_RECORDS_FOUND);
                    }else
                    {
                        tvNoRecoredFound.setVisibility(View.GONE);
                    }


                    mRecyclerView.setHasFixedSize(true);
                    LinearLayoutManager llm = new LinearLayoutManager(mContext);
                    llm.setOrientation(LinearLayoutManager.VERTICAL);
                    mRecyclerView.setLayoutManager(llm);
                    mRecyclerView.setAdapter(savedVideoAdapter);
                    savedVideoAdapter.notifyDataSetChanged();

                }
            };

            mDbTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            ///////////////////////////////////////////////////////////////////////////////////////////////////////////////


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(final SavedVideoAdapter.SavedVideoViewHolder viewHolder, final int pos) {

        viewHolder.videoRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("position : " + pos);
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
                            ((HomeScreen)mContext).overridePendingTransition(R.anim.slide_up_video_info, R.anim.nochange);
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

                if (favoriteVideoList.get(pos).isVideoIsFavorite() && ((favoriteVideoList.get(pos)
                        .getVideoLongUrl().length() == 0 || favoriteVideoList.get(pos).getVideoLongUrl()
                        .toLowerCase().equals("none")))) {
                    markFavoriteStatus(viewHolder,pos);
                } else {
                    if (favoriteVideoList.get(pos).getVideoLongUrl().length() > 0 && !favoriteVideoList
                            .get(pos).getVideoLongUrl().toLowerCase().equals("none")) {
                        markFavoriteStatus(viewHolder,pos);
                    } else {
                        //gk ((MainActivity) context).showToastMessage(GlobalConstants.MSG_NO_INFO_AVAILABLE);
                        viewHolder.savedVideoImageView.setImageResource(R.drawable.video_save);
                    }
                }

                savedVideoAdapter.notifyDataSetChanged();
            }
        });
    }

    public void markFavoriteStatus(final SavedVideoAdapter.SavedVideoViewHolder viewHolder, final int pos) {
        if (Utils.isInternetAvailable(mContext)) {
            if (AppController.getInstance().getModelFacade().getLocalModel().getUserId() ==
                    GlobalConstants.DEFAULT_USER_ID) {
                viewHolder.savedVideoImageView.setBackgroundResource(R.drawable.video_save);
                showConfirmLoginDialog(GlobalConstants.LOGIN_MESSAGE);
            } else {
                System.out.println("favorite position : " + pos);
                if (favoriteVideoList.get(pos).isVideoIsFavorite()) {
                    isFavoriteChecked = false;

                    VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).setFavoriteFlag
                            (0, favoriteVideoList.get(pos).getVideoId());
                    favoriteVideoList.get(pos).setVideoIsFavorite(false);
                    viewHolder.savedVideoImageView.setImageResource(R.drawable.video_save);
//                    favoriteVideoList.remove(pos);
//                    savedVideoAdapter.notifyDataSetChanged();
//                    if(favoriteVideoList.size() == 0)
//                    {
//                        tvNoRecoredFound.setText(GlobalConstants.NO_RECORDS_FOUND);
//                    }

                } else {
                    isFavoriteChecked = true;
                    VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).setFavoriteFlag
                            (1, favoriteVideoList.get(pos).getVideoId());
                    favoriteVideoList.get(pos).setVideoIsFavorite(true);
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
                            long userId = AppController.getInstance().getModelFacade().getLocalModel().getUserId();
                            postResult = AppController.getInstance().getServiceManager().getVaultService().
                                    postFavoriteStatus(userId, favoriteVideoList.get(pos).getVideoId(), favoriteVideoList.get(pos).getPlaylistId(),
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
                                VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).setFavoriteFlag(1,
                                        favoriteVideoList.get(pos).getVideoId());
                                // firebase analytics favoride video
//                                params.putString(FirebaseAnalytics.Param.ITEM_ID, arrayListVideoDTOs.get(pos).getVideoName());
//                                params.putString(FirebaseAnalytics.Param.ITEM_NAME, arrayListVideoDTOs.get(pos).getVideoName());
//                                params.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "video_favorite");
//                                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, params);

                            }else{
                                VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).setFavoriteFlag
                                        (0, favoriteVideoList.get(pos).getVideoId());
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
            viewHolder.savedVideoImageView.setBackgroundResource(R.drawable.video_save);
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
                        ((HomeScreen)mContext).finish();
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
                try {
                    String url = GlobalConstants.FAVORITE_API_URL + "userId=" + userId;

                    favoriteVideoList.clear();
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
                        // TODO Auto-generated method stub
                        return lhs.getVideoName().toLowerCase()
                                .compareTo(rhs.getVideoName().toLowerCase());
                    }
                });


                savedVideoAdapter  = new SavedVideoAdapter(mContext,favoriteVideoList,SavedVideoFragment.this);
                mRecyclerView.setHasFixedSize(true);
                LinearLayoutManager llm = new LinearLayoutManager(mContext);
                llm.setOrientation(LinearLayoutManager.VERTICAL);
                mRecyclerView.setLayoutManager(llm);
                mRecyclerView.setAdapter(savedVideoAdapter);
                refreshLayout.setRefreshing(false);

                if(favoriteVideoList.size() == 0)
                {
                    tvNoRecoredFound.setVisibility(View.VISIBLE);
                    tvNoRecoredFound.setText(GlobalConstants.NO_RECORDS_FOUND);
                }else
                {
                    tvNoRecoredFound.setVisibility(View.GONE);
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



}

