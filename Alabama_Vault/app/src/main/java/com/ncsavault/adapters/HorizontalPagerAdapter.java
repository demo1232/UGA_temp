package com.ncsavault.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import applicationId.R;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.ncsavault.dto.VideoDTO;
import com.ncsavault.fragments.views.VideoDetailFragment;
import com.ncsavault.globalconstants.GlobalConstants;
import com.ncsavault.utils.Utils;
import com.ncsavault.views.HomeScreen;
import com.ncsavault.views.VideoInfoActivity;
import java.util.ArrayList;

/**
 * Class using for to show the list of trending videos inside the HorizontalPagerAdapter
 * Used this class in HomeFragment
 */

public class HorizontalPagerAdapter extends PagerAdapter {

    private final Context context;
    private ArrayList<VideoDTO> trendingVideosList = new ArrayList<>();
    private int displayWidth = 0;

    /**
     * Constructor
     * @param context Get the reference of Activity here
     * @param trendingVideosList Get the list of trending video
     */
    public HorizontalPagerAdapter(Activity context, ArrayList<VideoDTO> trendingVideosList) {
        this.context = context;
        this.trendingVideosList = trendingVideosList;
       displayWidth = Utils.getScreenDimensions(context);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        try {
            @SuppressLint("InflateParams") View view = LayoutInflater.from(context).inflate(R.layout.item_cover, null);
            final RelativeLayout relativeLayoutMain = view.findViewById(R.id.linMain);
            final ImageView imageCover = view.findViewById(R.id.imageCover);
            final ProgressBar progressBar = view.findViewById(R.id.progress_bar_trending_videos);
            relativeLayoutMain.setTag(position);

            try {
              progressBar.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(trendingVideosList.get(position).getVideoStillUrl())
                    .placeholder(R.drawable.vault)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            progressBar.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            progressBar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(imageCover);
             } catch (Exception e) {
                e.printStackTrace();
                Log.e("HorizontalPagerAdapter", "Exception Horizontal " + e.getMessage());
            }

            int aspectHeight = (displayWidth * 9) / 16;
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    aspectHeight);
            imageCover.setLayoutParams(lp);

            imageCover.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Utils.isInternetAvailable(context)) {
                        if (trendingVideosList.get(position).getVideoLongUrl() != null) {
                            if (trendingVideosList.get(position).getVideoLongUrl().length() > 0
                                    && !trendingVideosList.get(position).getVideoLongUrl().toLowerCase().equals("none")) {
                                String videoCategory = GlobalConstants.FEATURED;
                                Intent intent = new Intent(context, VideoInfoActivity.class);
                                intent.putExtra(GlobalConstants.KEY_CATEGORY, videoCategory);
                                intent.putExtra(GlobalConstants.VIDEO_OBJ, trendingVideosList.get(position));
                                GlobalConstants.LIST_FRAGMENT = new VideoDetailFragment();
                                GlobalConstants.LIST_ITEM_POSITION = position;
                                context.startActivity(intent);
                                ((HomeScreen) context).overridePendingTransition(R.anim.slide_up_video_info, R.anim.nochange);
                            } else {
                                ((HomeScreen) context).showToastMessage(GlobalConstants.MSG_NO_INFO_AVAILABLE);
                            }
                        } else {
                            ((HomeScreen) context).showToastMessage(GlobalConstants.MSG_NO_INFO_AVAILABLE);
                        }
                    } else {
                        ((HomeScreen) context).showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
                    }

                }
            });

            container.addView(view);
            return view;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        if (trendingVideosList.size() > 0) {
            return trendingVideosList.size();
        }
        return 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == object);
    }

}