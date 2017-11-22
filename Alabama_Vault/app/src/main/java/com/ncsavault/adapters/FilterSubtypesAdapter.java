package com.ncsavault.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import applicationId.R;
import com.ncsavault.controllers.AppController;
import com.ncsavault.database.VaultDatabaseHelper;
import com.ncsavault.dto.VideoDTO;
import com.ncsavault.fragments.views.VideoDetailFragment;
import com.ncsavault.globalconstants.GlobalConstants;
import com.ncsavault.service.TrendingFeaturedVideoService;
import com.ncsavault.utils.Utils;
import com.ncsavault.views.HomeScreen;
import com.ncsavault.views.LoginEmailActivity;
import com.ncsavault.views.VideoInfoActivity;

import java.util.ArrayList;
import java.util.List;

import me.crosswall.lib.coverflow.CoverFlow;
import me.crosswall.lib.coverflow.core.PagerContainer;


/**
 * This class is a base adapter of Home fragment.
 * Here we are using Horizontal view
 * Custom Banner
 * List of Featured tab Videos.
 * And Inline ads.
 * Used this class in HomeFragment
 */

@SuppressWarnings("deprecation")
public class FilterSubtypesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final String TAG = "FilterSubtypesAdapter";
    private final Activity mContext;
    private final List<VideoDTO> albumList;
    // Header view type
    private final int HEADER_VIEW = 0;
    private final int BANNER_VIEW = 1;
    private final int TYPE_LOW = 2;
    private final int TYPE_HIGH = 3;
    private ArrayList<VideoDTO> trendingVideoList = new ArrayList<>();
    private final BannerClickListener bannerClickListener;
    private boolean isFavoriteChecked;
    private String postResult;
    private int displayWidth = 0;


    /**
     * Constrictor
     * @param mContext Get the reference of Activity here.
     * @param albumList Get the list of videos
     * @param trendingVideoList Get the list of trending videos
     * @param bannerClickListener Click on banner image.
     */
    public FilterSubtypesAdapter(Activity mContext, List<VideoDTO> albumList,
                                 ArrayList<VideoDTO> trendingVideoList,
                                 BannerClickListener bannerClickListener) {
        this.mContext = mContext;
        this.albumList = albumList;
        this.trendingVideoList = trendingVideoList;
        displayWidth = Utils.getScreenDimensions(mContext);
        this.bannerClickListener = bannerClickListener;

    }

    /**
     * This is custom interface to used that click the items of recycler view.
     */
    public interface BannerClickListener {
        void onClick(FilterSubtypesAdapter.BannerViewHolder videoViewHolder, int position);
    }


    /**
     * Inner class we have to implemented for to
     * initialize the items of Horizontal adapter using view holder here.
     */
    public class VHHeader extends RecyclerView.ViewHolder {

        private final ViewPager pager;
        private final TextView topTenText;
        private final ConstraintLayout horizontal_layout;


        /**
         * Constructor
         * @param itemView use the reference of itemView and initialize item.
         */
        public VHHeader(View itemView) {
            super(itemView);
            PagerContainer pagerContainer = itemView.findViewById(R.id.pager_container);
            pagerContainer.setOverlapEnabled(true);
            pager = pagerContainer.getViewPager();
            topTenText = itemView.findViewById(R.id.textView_videoName_top);
            horizontal_layout = itemView.findViewById(R.id.horizontal_layout);

        }

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == HEADER_VIEW) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_view, parent, false);

            return new VHHeader(view);
        } else if (viewType == BANNER_VIEW) {

            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.featured_banner_layout, parent, false);

            return new BannerViewHolder(mContext, view);
        } else if (viewType == TYPE_HIGH) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.native_inline_ads_small_scrren_layout, parent, false);

            return new SubtypeViewHolder(view);

        } else if (viewType == TYPE_LOW) {

            view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.home_screen_menu_item, parent, false);

            return new MyViewHolder(view);
        }

        return null;

    }


    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        int viewType = getItemViewType(position);
        switch (viewType) {
            case TYPE_LOW:

                try {
                    final MyViewHolder viewHolder = (MyViewHolder) holder;
                    final VideoDTO videoDTO = albumList.get(position);

                    if (videoDTO.isVideoIsFavorite()) {
                        viewHolder.savedImage.setImageResource(R.drawable.saved_video_img);
                    } else {
                        viewHolder.savedImage.setImageResource(R.drawable.video_save);
                    }

                    if (VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).
                            isFavorite(videoDTO.getVideoId())) {
                        viewHolder.savedImage.setImageResource(R.drawable.saved_video_img);
                    } else {
                        viewHolder.savedImage.setImageResource(R.drawable.video_save);
                    }

                    try {
                    viewHolder.progressBar.setVisibility(View.VISIBLE);
                    Glide.with(mContext)
                            .load(videoDTO.getVideoStillUrl())
                            .placeholder(R.drawable.vault)
                            .listener(new RequestListener<String, GlideDrawable>() {
                                @Override
                                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                    viewHolder.progressBar.setVisibility(View.GONE);
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                    viewHolder.progressBar.setVisibility(View.GONE);
                                    return false;
                                }
                            })
                            .into(viewHolder.videoImage);
                                          
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(TAG, "Exception TYPE_LOW " + e.getMessage());
                    }

                    int aspectHeight = (displayWidth * 9) / 16;


                    RelativeLayout.LayoutParams mainLayout = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                            aspectHeight);
                    viewHolder.mainFeaturedLayout.setLayoutParams(mainLayout);

                    viewHolder.mVideoName.setText(videoDTO.getVideoName());

                    viewHolder.videoImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (Utils.isInternetAvailable(mContext)) {
                                if (videoDTO.getVideoLongUrl() != null) {
                                    if (videoDTO.getVideoLongUrl().length() > 0
                                            && !videoDTO.getVideoLongUrl().toLowerCase().equals("none")) {
                                        String videoCategory = GlobalConstants.FEATURED;
                                        Intent intent = new Intent(mContext, VideoInfoActivity.class);
                                        intent.putExtra(GlobalConstants.KEY_CATEGORY, videoCategory);
                                        intent.putExtra(GlobalConstants.VIDEO_OBJ, videoDTO);
                                        GlobalConstants.LIST_FRAGMENT = new VideoDetailFragment();
                                        GlobalConstants.LIST_ITEM_POSITION = position;
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
                    });

                    viewHolder.savedImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (videoDTO.isVideoIsFavorite() && ((videoDTO
                                    .getVideoLongUrl().length() == 0 || videoDTO.getVideoLongUrl()
                                    .toLowerCase().equals("none")))) {
                                markFavoriteStatus(viewHolder, position);
                            } else {
                                if (videoDTO.getVideoLongUrl().length() > 0 && !videoDTO.getVideoLongUrl().toLowerCase().
                                        equals("none")) {
                                    markFavoriteStatus(viewHolder, position);
                                } else {
                                    ((HomeScreen) mContext).showToastMessage(GlobalConstants.MSG_NO_INFO_AVAILABLE);
                                    viewHolder.savedImage.setImageResource(R.drawable.video_save);
                                }
                            }

                            notifyDataSetChanged();
                        }
                    });


                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            case TYPE_HIGH:
                adMobBannerAdvertising(holder, position);
                break;
            case HEADER_VIEW:
                setHorizontalPager(holder);
                break;
            case BANNER_VIEW:
                setBanner(holder, position);
                break;
        }

    }

    /**
     * Method used to show the list of videos and thumbnail
     * @param holder reference of view holder
     * @param position Get the position of banner.
     */
    private void setBanner(RecyclerView.ViewHolder holder, int position) {
        final BannerViewHolder viewHolder = (BannerViewHolder) holder;
        if (AppController.getInstance().getModelFacade().getLocalModel().isBannerActivated()) {
            final VideoDTO videoDTO = albumList.get(position);
            viewHolder.imageViewBanner.setVisibility(View.VISIBLE);
            viewHolder.bannerLayout.setVisibility(View.VISIBLE);

            try {
           viewHolder.progressBar.setVisibility(View.VISIBLE);
            Glide.with(mContext)
                    .load(videoDTO.getVideoStillUrl())
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            viewHolder.progressBar.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            viewHolder.progressBar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(viewHolder.imageViewBanner);
                          
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("Exception","Exception banner " + e.getMessage());
            }

            bannerClickListener.onClick((BannerViewHolder) holder, position);
        } else {
            viewHolder.imageViewBanner.setVisibility(View.GONE);
            viewHolder.bannerLayout.setVisibility(View.GONE);
        }

    }


    @Override
    public int getItemViewType(int position) {

        if (isPositionHeader(position))
            return HEADER_VIEW;

        if (isPositionBanner(position))
            return BANNER_VIEW;

        if ((position + 1) % 3 == 0)
            return TYPE_HIGH;

        return TYPE_LOW;
    }

    /**
     * Method used to set the Horizontal view first position
     * @param position set the value of position.
     * @return position
     */
    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    /**
     * Method used to set the Horizontal view second position
     * @param position set the value of position.
     * @return position
     */
    private boolean isPositionBanner(int position) {
        return position == 1;
    }

    /**
     * Method used for show the Horizontal video list in view pager
     * @param holder reference of view holder class
     */
    private void setHorizontalPager(final RecyclerView.ViewHolder holder) {
        final VHHeader vhHeader = (VHHeader) holder;

        float margin;
        int screenSize = mContext.getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;
        if (screenSize == Configuration.SCREENLAYOUT_SIZE_NORMAL) {
            margin = (float) (-displayWidth / 3.8);
        } else {
            margin = (float) (-displayWidth / 3.9);
        }

        new CoverFlow.Builder().with(vhHeader.pager)
                .scale(0.2f)
                .pagerMargin(margin)
                .spaceSize(0f)
                .build();
        Log.d(TAG, "pager" + vhHeader.pager.getWidth());


        try {
            if (trendingVideoList.size() > 0) {
                vhHeader.topTenText.setText(trendingVideoList.get(0).getPlaylistShortDescription());
            } else {
                vhHeader.horizontal_layout.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        HorizontalPagerAdapter adapter = new HorizontalPagerAdapter(mContext, trendingVideoList);
        vhHeader.pager.setAdapter(adapter);
        vhHeader.pager.setOffscreenPageLimit(adapter.getCount());
        vhHeader.pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            private int index = 0;

            @Override
            public void onPageSelected(int position) {
                index = position;
                Log.d(TAG, "index" + index);
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }


    @Override
    public int getItemCount() {
        return albumList.size();
    }


    /**
     * Inner class we have to implemented for to
     * initialize the items of Videos list using view holder here.
     */
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView mVideoName;
        private final ImageView videoImage;
        private final ImageView savedImage;
        private final ProgressBar progressBar;
        private final RelativeLayout mainFeaturedLayout;

        /**
         * Constructor
         * @param view use the reference of view and initialize item.
         */
        public MyViewHolder(View view) {
            super(view);
            mVideoName = view.findViewById(R.id.video_name);
            videoImage = view.findViewById(R.id.video_image);
            savedImage = view.findViewById(R.id.saved_image_view);
            mainFeaturedLayout = view.findViewById(R.id.main_featured_layout);
            progressBar = view.findViewById(R.id.progressbar);

        }
    }

    /**
     * Inner class we have to implemented for to
     * initialize the items of Inline Ads using view holder .
     */
    public static class SubtypeViewHolder extends RecyclerView.ViewHolder {

        private final RelativeLayout adViewLayout;

        /**
         * Constructor
         * @param itemView use the reference of view and initialize item.
         */
        public SubtypeViewHolder(View itemView) {
            super(itemView);
            adViewLayout = itemView.findViewById(R.id.adView_layout);
        }

    }

    /**
     * Inner class we have to implemented for to
     * initialize the items of Custom Banner using view holder .
     */
    public static class BannerViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageViewBanner;
        private final LinearLayout bannerLayout;
        private final ProgressBar progressBar;
        private final TextView featuredTextView;
        private final Activity mContext;

        public BannerViewHolder(Activity context, View itemView) {
            super(itemView);
            this.mContext = context;
            imageViewBanner = itemView.findViewById(R.id.image_view_banner);
            bannerLayout = itemView.findViewById(R.id.banner_layout);
            progressBar = itemView.findViewById(R.id.progressbar);
            imageViewBanner = itemView.findViewById(R.id.image_view_banner);
            featuredTextView = itemView.findViewById(R.id.textView_featured);
            Typeface face = Typeface.createFromAsset(mContext.getAssets(), "fonts/OpenSans-Bold.ttf");
            featuredTextView.setTypeface(face);
        }

    }

    /**
     * Method used for show the Inline ads after a particular position inside the list.
     * @param holder reference of view holder class
     * @param position Get the position of particular item
     */
    private void adMobBannerAdvertising(RecyclerView.ViewHolder holder, int position) {
        SubtypeViewHolder vhHeader = (SubtypeViewHolder) holder;
        if (position == 2) {
            vhHeader.adViewLayout.setVisibility(View.GONE);
        } else {
            vhHeader.adViewLayout.setVisibility(View.VISIBLE);
            VideoDTO videoAdMob = albumList.get(position);
            AdView mAdView = new AdView(mContext);
            Log.d("unit id get : ","unit id get : " + videoAdMob.getVideoName());
            int ht = 32;
            int wt = 320;
            if (Utils.getScreenHeight(mContext) <= 400) {
                ht = 32;
            } else if (Utils.getScreenHeight(mContext) > 400 ||Utils.getScreenHeight(mContext) <=720){
                ht = 50;
            }else if(Utils.getScreenHeight(mContext) > 720 ){
                ht = 90;
            }
            if(Utils.getScreenDimensions(mContext) <= 720)
            {
                wt = 340;
            }else
            {
                wt = 390;
            }
            Log.i("Width ","111Width : "+wt +"111Height :"+ht);
            mAdView.setAdSize(new AdSize(wt,ht));
            mAdView.setAdUnitId(videoAdMob.getVideoName());
            vhHeader.adViewLayout.addView(mAdView);
            AdRequest request = new AdRequest.Builder()
                    .addTestDevice(mContext.getResources().getString(R.string.test_device_id))
                    .build();

            mAdView.loadAd(request);
        }

    }


    /**
     *  Method used for to make a Favorite video
     * @param viewHolder reference of view holder class
     * @param pos Get the position to make a favorite
     */
    private void markFavoriteStatus(final MyViewHolder viewHolder, final int pos) {
        if (Utils.isInternetAvailable(mContext)) {
            if (AppController.getInstance().getModelFacade().getLocalModel().getUserId() ==
                    GlobalConstants.DEFAULT_USER_ID) {
                viewHolder.savedImage.setImageResource(R.drawable.video_save);
                showConfirmLoginDialog();
            } else {
                Log.d("favorite position : ","favorite position : " + pos);
                if (albumList.get(pos).isVideoIsFavorite()) {
                    isFavoriteChecked = false;
                    VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).setFavoriteFlag
                            (0, albumList.get(pos).getVideoId());
                    albumList.get(pos).setVideoIsFavorite(false);
                    viewHolder.savedImage.setImageResource(R.drawable.video_save);
                } else {
                    isFavoriteChecked = true;
                    VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).setFavoriteFlag
                            (1, albumList.get(pos).getVideoId());
                    albumList.get(pos).setVideoIsFavorite(true);
                    viewHolder.savedImage.setImageResource(R.drawable.saved_video_img);
                }

                AsyncTask<Void, String, String> mPostTask = new AsyncTask<Void, String, String>() {

                    @Override
                    protected String doInBackground(Void... params) {
                        try {
                            postResult = AppController.getInstance().getServiceManager().getVaultService().
                                    postFavoriteStatus(AppController.getInstance().getModelFacade().getLocalModel()
                                                    .getUserId(), albumList.get(pos).getVideoId(),
                                            albumList.get(pos).getPlaylistId(),
                                            isFavoriteChecked);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return postResult;
                    }

                    @Override
                    protected void onPostExecute(String result) {
                        try {
                            Log.d(TAG, "favorite position 111 : " + pos);
                            if (isFavoriteChecked) {
                                VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).setFavoriteFlag(1,
                                        albumList.get(pos).getVideoId());
                            } else {
                                VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).setFavoriteFlag
                                        (0, albumList.get(pos).getVideoId());
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
            viewHolder.savedImage.setImageResource(R.drawable.video_save);
        }
    }

    /**
     * Method used for show alert dialog box for login to app.
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

                        SharedPreferences prefs = mContext.getSharedPreferences(mContext.getResources().getString(R.string.pref_package_name),
                                Context.MODE_PRIVATE);
                        prefs.edit().putLong(GlobalConstants.PREF_VAULT_USER_ID_LONG, 0).apply();

                        Intent intent = new Intent(mContext, LoginEmailActivity.class);
                        mContext.startActivity(intent);
                        mContext.finish();

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


}