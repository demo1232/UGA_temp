package com.ncsavault.alabamavault.views;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ncsavault.alabamavault.R;
import com.ncsavault.alabamavault.adapters.FilterSubtypesAdapter;
import com.ncsavault.alabamavault.adapters.VideoDetailAdapter;
import com.ncsavault.alabamavault.adapters.VideoSearchAdapter;
import com.ncsavault.alabamavault.controllers.AppController;
import com.ncsavault.alabamavault.database.VaultDatabaseHelper;
import com.ncsavault.alabamavault.dto.PlaylistDto;
import com.ncsavault.alabamavault.dto.VideoDTO;
import com.ncsavault.alabamavault.fragments.views.HomeFragment;
import com.ncsavault.alabamavault.fragments.views.VideoDetailFragment;
import com.ncsavault.alabamavault.globalconstants.GlobalConstants;
import com.ncsavault.alabamavault.service.TrendingFeaturedVideoService;
import com.ncsavault.alabamavault.utils.Utils;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class VideoSearchActivity extends AppCompatActivity implements VideoSearchAdapter.SearchVideoClickListener {

    RecyclerView recyclerViewSearch;
    VideoSearchAdapter videoSearchAdapter;
    public Toolbar mToolbar;

    public ImageView imageviewSearch;
    public EditText editTextSearch;
    public ImageView imageviewLogo;
    public ImageView imageViewBackNavigation;

    public LinearLayout linearLayoutToolbarText;

    TextView textviewNoVideoFound;
    ArrayList<Object> objects;
    Animation animation;
    Context mContext;
    private boolean isFavoriteChecked;
    private String postResult;
    AsyncTask<Void, Void, Void> mPostTask;
    String fragment = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_search);
        initComponents();
    }

    private void initComponents() {
        mContext = this;
//        Utils.hideKeyboard(VideoSearchActivity.this);

        //toolbar.setNavigationIcon(R.drawable.ic_toolbar);

        initializeToolbar();

        imageViewBackNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        fragment = getIntent().getStringExtra("Fragment");


//        setHint(fragment);


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
                objects = new ArrayList<Object>();
                if (s.toString() != null) {
                    if (videoSearchAdapter != null) {
                        objects = videoSearchAdapter.filter(s.toString());
                    }
                    if (objects.size() > 0) {
                        textviewNoVideoFound.setVisibility(View.GONE);
                        recyclerViewSearch.setAdapter(videoSearchAdapter);
                        videoSearchAdapter.notifyDataSetChanged();
                        recyclerViewSearch.setHasFixedSize(true);
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(VideoSearchActivity.this);
                        recyclerViewSearch.setLayoutManager(layoutManager);
                    } else {
                        textviewNoVideoFound.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        AppController.getInstance().setCurrentActivity(this);
    }

    private void initializeToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.back));
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appBar);
        appBarLayout.setExpanded(false, true);

        mToolbar.setTitle("");
        mToolbar.setSubtitle("");

        recyclerViewSearch = (RecyclerView) findViewById(R.id.recycler_view_search);
        textviewNoVideoFound = (TextView) findViewById(R.id.textView_recordsNotFound);
        editTextSearch = (EditText) mToolbar.findViewById(R.id.editText_search);
        imageviewSearch = (ImageView) mToolbar.findViewById(R.id.imageview_search);
        linearLayoutToolbarText=(LinearLayout) mToolbar.findViewById(R.id.ll_toolbarText);
        editTextSearch.setVisibility(View.VISIBLE);
        imageviewSearch.setVisibility(View.INVISIBLE);
        linearLayoutToolbarText.setVisibility(View.INVISIBLE);
        imageViewBackNavigation = (ImageView) mToolbar.findViewById(R.id.imageview_back);
        imageViewBackNavigation.setVisibility(View.VISIBLE);
    }


    private void getVideoDataFromDataBase(final String fragment) {
        final AsyncTask<Void, Void, Void> mDbTask = new AsyncTask<Void, Void, Void>() {


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(Void... params) {

                try {
                    if (fragment.equalsIgnoreCase("HomeFragment") || fragment.equalsIgnoreCase("VideoDetailFragment")) {
                        objects = new ArrayList<Object>();
                        objects.addAll((VaultDatabaseHelper.getInstance(VideoSearchActivity.this).getAllVideoList()));

                    } else if (fragment.equalsIgnoreCase("PlayListFragment")) {
                        objects = new ArrayList<Object>();
                        objects.addAll(VaultDatabaseHelper.getInstance(VideoSearchActivity.this).getAllLocalPlaylistTabData());
                    } else if (fragment.equalsIgnoreCase("SavedVideoFragment")) {
                        objects = new ArrayList<Object>();
                        objects.addAll(VaultDatabaseHelper.getInstance(VideoSearchActivity.this).getFavouriteVideosArrayList());
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
                } else {

                }
            }
        });
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
                animation = AnimationUtils.loadAnimation(VideoSearchActivity.this,
                        R.anim.abc_fade_out);

                text.setAnimation(animation);
                text.setVisibility(View.GONE);
            }
        }, 2000);
    }


    public void markFavoriteStatus(final VideoSearchAdapter.SearchVideoViewHolder viewHolder, final VideoDTO videoDTO) {
        if (Utils.isInternetAvailable(mContext)) {
            if (AppController.getInstance().getModelFacade().getLocalModel().getUserId() ==
                    GlobalConstants.DEFAULT_USER_ID) {
                viewHolder.savedVideoImageView.setImageResource(R.drawable.video_save);
                showConfirmLoginDialog(GlobalConstants.LOGIN_MESSAGE);
            } else {
//                System.out.println("favorite position : " + pos);
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
                                                    .getUserId(), videoDTO.getVideoId(),
                                            videoDTO.getPlaylistId(),
                                            isFavoriteChecked);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        try {
//                            System.out.println("favorite position 111 : " + pos);
                            if (isFavoriteChecked) {
                                VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).setFavoriteFlag(1,
                                        videoDTO.getVideoId());
                                // firebase analytics favoride video
//                                params.putString(FirebaseAnalytics.Param.ITEM_ID, arrayListVideoDTOs.get(pos).getVideoName());
//                                params.putString(FirebaseAnalytics.Param.ITEM_NAME, arrayListVideoDTOs.get(pos).getVideoName());
//                                params.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "video_favorite");
//                                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, params);

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
        Button nbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        nbutton.setAllCaps(false);
        nbutton.setTextColor(mContext.getResources().getColor(R.color.apptheme_color));
        Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        pbutton.setTextColor(mContext.getResources().getColor(R.color.apptheme_color));
        pbutton.setAllCaps(false);
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

    public void setHint(String fragment) {
        if (fragment.equalsIgnoreCase("HomeFragment") || fragment.equalsIgnoreCase("VideoDetailFragment")) {
            editTextSearch.setHint("Videos" + " Name " + "(" + "Users Input Filters" + ")");
            editTextSearch.setHintTextColor(mContext.getResources().getColor(R.color.apptheme_color));

        }else if (fragment.equalsIgnoreCase("PlayListFragment")) {
            editTextSearch.setHint("PlayList " + "(" + "Users Input Filters" + ")");
            editTextSearch.setHintTextColor(mContext.getResources().getColor(R.color.apptheme_color));

        }else if (fragment.equalsIgnoreCase("SavedVideoFragment")) {
            editTextSearch.setHint("Saved " + "(" + "Users Input Filters" + ")");
            editTextSearch.setHintTextColor(mContext.getResources().getColor(R.color.apptheme_color));
        }
    }

}
