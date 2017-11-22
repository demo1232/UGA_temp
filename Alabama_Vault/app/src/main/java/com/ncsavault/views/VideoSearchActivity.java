package com.ncsavault.views;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import applicationId.R;
import com.ncsavault.adapters.VideoSearchAdapter;
import com.ncsavault.controllers.AppController;
import com.ncsavault.database.VaultDatabaseHelper;
import com.ncsavault.dto.PlaylistDto;
import com.ncsavault.dto.VideoDTO;
import com.ncsavault.fragments.views.VideoDetailFragment;
import com.ncsavault.globalconstants.GlobalConstants;
import com.ncsavault.service.TrendingFeaturedVideoService;
import com.ncsavault.utils.Utils;

import java.util.ArrayList;

/**
 * VideoSearchActivity is used for searching playlist and videos
 */
public class VideoSearchActivity extends AppCompatActivity implements VideoSearchAdapter.SearchVideoClickListener {

    private RecyclerView recyclerViewSearch;
    private VideoSearchAdapter videoSearchAdapter;

    private EditText editTextSearch;
    private ImageView imageViewBackNavigation;

    private TextView textViewNoVideoFound;
    private ArrayList<Object> objects;
    private Animation animation;
    private Context mContext;
    private boolean isFavoriteChecked;
    private String fragment = "";
    private String searchName = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_search);
        initComponents();
    }

    /**
     * Method is used to initialize components
     */
    private void initComponents() {
        mContext = this;
        fragment = getIntent().getStringExtra("Fragment");
        Intent intent = getIntent();
        if (intent != null) {
            if (fragment.equalsIgnoreCase("PlayListFragment")) {
                searchName = intent.getStringExtra("tab_name");
            }else if (fragment.equalsIgnoreCase("VideoDetailFragment")) {
                searchName = intent.getStringExtra("playlist_name");
            }else if (fragment.equalsIgnoreCase("SavedVideoFragment") || fragment.equalsIgnoreCase("HomeFragment") ) {
                searchName = getResources().getString(R.string.app_name);
            }
        }
        initializeToolbar();

        imageViewBackNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        getVideoDataFromDataBase(fragment);
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                objects = new ArrayList<>();
                if (s.toString() != null) {
                    if (videoSearchAdapter != null) {
                        objects = videoSearchAdapter.filter(s.toString());
                    }
                    if (objects.size() > 0) {
                        textViewNoVideoFound.setVisibility(View.GONE);
                        recyclerViewSearch.setAdapter(videoSearchAdapter);
                        videoSearchAdapter.notifyDataSetChanged();
                        recyclerViewSearch.setHasFixedSize(true);
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(VideoSearchActivity.this);
                        recyclerViewSearch.setLayoutManager(layoutManager);
                    } else {
                        textViewNoVideoFound.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        AppController.getInstance().setCurrentActivity(this);
    }

    /**
     * Method is used to show toolbar items
     */
    private void initializeToolbar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setNavigationIcon(ContextCompat.getDrawable(VideoSearchActivity.this,R.drawable.back));
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appBar);
        appBarLayout.setExpanded(false, true);

        mToolbar.setTitle("");
        mToolbar.setSubtitle("");

        recyclerViewSearch = (RecyclerView) findViewById(R.id.recycler_view_search);
        textViewNoVideoFound = (TextView) findViewById(R.id.textView_recordsNotFound);
        editTextSearch = mToolbar.findViewById(R.id.editText_search);
        editTextSearch.setHint("Search "+searchName.toUpperCase());
        ImageView imageViewSearch = mToolbar.findViewById(R.id.image_view_search);
        LinearLayout linearLayoutToolbarText = mToolbar.findViewById(R.id.ll_toolbarText);
        editTextSearch.setVisibility(View.VISIBLE);
        imageViewSearch.setVisibility(View.INVISIBLE);
        linearLayoutToolbarText.setVisibility(View.INVISIBLE);
        imageViewBackNavigation = mToolbar.findViewById(R.id.image_view_back);
        imageViewBackNavigation.setVisibility(View.VISIBLE);
    }

    /**
     * Method is used to get videos, playlist from local database for search results
     * @param fragment name of fragment
     */
    private void getVideoDataFromDataBase(final String fragment) {
        final AsyncTask<Void, Void, Void> mDbTask = new AsyncTask<Void, Void, Void>() {


            @Override
            protected Void doInBackground(Void... params) {

                try {
                    if (fragment.equalsIgnoreCase("HomeFragment")) {
                        objects = new ArrayList<>();
                        objects.addAll((VaultDatabaseHelper.getInstance(VideoSearchActivity.this).getAllVideoList()));

                    } else if (fragment.equalsIgnoreCase("PlayListFragment")) {
                        long categoryId = getIntent().getLongExtra("categoryId", 0);
                        objects = new ArrayList<>();
                        objects.addAll(VaultDatabaseHelper.getInstance(VideoSearchActivity.this).getLocalPlaylistDataByCategoriesTab(categoryId));
                    } else if (fragment.equalsIgnoreCase("SavedVideoFragment")) {
                        objects = new ArrayList<>();
                        objects.addAll(VaultDatabaseHelper.getInstance(VideoSearchActivity.this).getFavouriteVideosArrayList());
                    } else if (fragment.equalsIgnoreCase("VideoDetailFragment")) {
                        long playListId = getIntent().getLongExtra("playlistId", 0);
                        objects = new ArrayList<>();
                        objects.addAll((VaultDatabaseHelper.getInstance(VideoSearchActivity.this).getVideoDataByPlaylistId(playListId)));
                    }


                    videoSearchAdapter = new VideoSearchAdapter(VideoSearchActivity.this, objects, VideoSearchActivity.this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                // ------- addBannerImage---------------------

            }
        };

        mDbTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    @Override
    public void onClick(final VideoSearchAdapter.SearchVideoViewHolder videoViewHolder, final int position) {
        videoViewHolder.videoRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.isInternetAvailable(VideoSearchActivity.this)) {

                    if (objects.get(position) instanceof VideoDTO) {
                        VideoDTO videoDTO = (VideoDTO) objects.get(position);
                        if (videoDTO.getVideoLongUrl() != null) {
                            if (videoDTO.getVideoLongUrl().length() > 0
                                    && !videoDTO.getVideoLongUrl().toLowerCase().equals("none")) {
                                String videoCategory = GlobalConstants.FEATURED;
                                Intent intent = new Intent(VideoSearchActivity.this, VideoInfoActivity.class);
                                intent.putExtra(GlobalConstants.KEY_CATEGORY, videoCategory);
                                intent.putExtra(GlobalConstants.VIDEO_OBJ, videoDTO);
                                GlobalConstants.LIST_FRAGMENT = new VideoDetailFragment();
                                GlobalConstants.LIST_ITEM_POSITION = position;
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_up_video_info,
                                        R.anim.nochange);
                            } else {
                                showToastMessage(GlobalConstants.MSG_NO_INFO_AVAILABLE);
                            }
                        } else {
                            showToastMessage(GlobalConstants.MSG_NO_INFO_AVAILABLE);
                        }
                    } else if (objects.get(position) instanceof PlaylistDto) {
                        PlaylistDto playlistDto = (PlaylistDto) objects.get(position);
                        Intent intent = new Intent(VideoSearchActivity.this, VideoDetailActivity.class);
                        intent.putExtra("playlistId", playlistDto.getPlaylistId());
                        intent.putExtra("playlistName", playlistDto.getPlaylistName());
                        finish();
                        startActivity(intent);


                       /* VideoDetailFragment videoFragment = (VideoDetailFragment) VideoDetailFragment.newInstance(VideoSearchActivity.this, playlistDto.getPlaylistId());
                        FragmentManager manager = getSupportFragmentManager();
                        FragmentTransaction transaction = manager.beginTransaction();
                        transaction.replace(R.id.container, videoFragment);
                        transaction.addToBackStack(videoFragment.getClass().getName());
                        transaction.commit();*/
                    }

                } else {
                    showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
                }
            }
        });

        videoViewHolder.mLayoutSavedImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!fragment.equalsIgnoreCase("SavedVideoFragment")) {
                    if (Utils.isInternetAvailable(VideoSearchActivity.this)) {

                        if (objects.get(position) instanceof VideoDTO) {
                            VideoDTO videoDTO = (VideoDTO) objects.get(position);
                            if (videoDTO.isVideoIsFavorite() && ((videoDTO
                                    .getVideoLongUrl().length() == 0 || videoDTO.getVideoLongUrl()
                                    .toLowerCase().equals("none")))) {
                                markFavoriteStatus(videoViewHolder, videoDTO);
                            } else {
                                if (videoDTO.getVideoLongUrl().length() > 0 && !videoDTO.getVideoLongUrl().toLowerCase().equals("none")) {
                                    markFavoriteStatus(videoViewHolder, videoDTO);
                                } else {
                                    //gk ((MainActivity) context).showToastMessage(GlobalConstants.MSG_NO_INFO_AVAILABLE);
                                    videoViewHolder.savedVideoImageView.setImageResource(R.drawable.video_save);
                                }
                            }
                            videoSearchAdapter.notifyDataSetChanged();

                        }
                    }else{
                        showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
                    }
                }
            }
        });
    }

    /**
     * Method is used to show toast message
     * @param message set the toast message.
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
                animation = AnimationUtils.loadAnimation(VideoSearchActivity.this,
                        R.anim.abc_fade_out);

                text.setAnimation(animation);
                text.setVisibility(View.GONE);
            }
        }, 2000);
    }

    /**
     * Method is used to save video locally and make video status favourite
     * @param viewHolder the reference of view holder class
     * @param videoDTO of selected video
     */
    private void markFavoriteStatus(final VideoSearchAdapter.SearchVideoViewHolder viewHolder, final VideoDTO videoDTO) {
        if (Utils.isInternetAvailable(mContext)) {
            if (AppController.getInstance().getModelFacade().getLocalModel().getUserId() ==
                    GlobalConstants.DEFAULT_USER_ID) {
                viewHolder.savedVideoImageView.setImageResource(R.drawable.video_save);
                showConfirmLoginDialog();
            } else {
//                Log.d("favorite position : " + pos);
                if (videoDTO.isVideoIsFavorite()) {
                    isFavoriteChecked = false;
                    VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).setFavoriteFlag
                            (0, videoDTO.getVideoId());
                    videoDTO.setVideoIsFavorite(false);
                    viewHolder.savedVideoImageView.setImageResource(R.drawable.video_save);
                } else {
                    isFavoriteChecked = true;
                    VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).setFavoriteFlag
                            (1, videoDTO.getVideoId());
                    videoDTO.setVideoIsFavorite(true);
                    viewHolder.savedVideoImageView.setImageResource(R.drawable.saved_video_img);
                }

                AsyncTask<Void, Void, Void> mPostTask = new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... params) {
                        try {
                            String postResult = AppController.getInstance().getServiceManager().getVaultService().
                                    postFavoriteStatus(AppController.getInstance().getModelFacade().getLocalModel()
                                                    .getUserId(), videoDTO.getVideoId(),
                                            videoDTO.getPlaylistId(),
                                            isFavoriteChecked);
                            Log.d("result","result"+postResult);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        try {

                            if (isFavoriteChecked) {
                                VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).setFavoriteFlag(1, videoDTO.getVideoId());

                            } else {
                                VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).setFavoriteFlag
                                        (0, videoDTO.getVideoId());
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
     *  Method is used to show Login Dialog
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

                        SharedPreferences prefs = mContext.
                                getSharedPreferences(getResources().getString(R.string.pref_package_name),
                                Context.MODE_PRIVATE);
                        prefs.edit().putLong(GlobalConstants.PREF_VAULT_USER_ID_LONG, 0).apply();
//                        prefs.edit().putBoolean(GlobalConstants.PREF_PULL_OPTION_HEADER, false).commit();

                        Intent intent = new Intent(mContext, LoginEmailActivity.class);
                        startActivity(intent);
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
        Button PositiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        PositiveButton.setAllCaps(false);
        PositiveButton.setTextColor(ContextCompat.getColor(VideoSearchActivity.this,R.color.app_theme_color));
        Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        negativeButton.setTextColor(ContextCompat.getColor(VideoSearchActivity.this,R.color.app_theme_color));
        negativeButton.setAllCaps(false);
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
