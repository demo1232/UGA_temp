package com.ncsavault.alabamavault.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Build;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.PagerAdapter;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.android.volley.toolbox.ImageLoader;
import com.bumptech.glide.Glide;
import com.ncsavault.alabamavault.R;
import com.ncsavault.alabamavault.controllers.AppController;
import com.ncsavault.alabamavault.dto.VideoDTO;
import com.ncsavault.alabamavault.fragments.views.VideoDetailFragment;
import com.ncsavault.alabamavault.globalconstants.GlobalConstants;
import com.ncsavault.alabamavault.utils.Utils;
import com.ncsavault.alabamavault.views.HomeScreen;
import com.ncsavault.alabamavault.views.VideoDetailActivity;
import com.ncsavault.alabamavault.views.VideoInfoActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;


public class HorizontalPagerAdapter extends PagerAdapter {

    Context context;
    int adapterType = 1;
    public static final int ADAPTER_TYPE_TOP = 1;
    public static final int ADAPTER_TYPE_BOTTOM = 2;
    ArrayList<VideoDTO> trendingVideosList = new ArrayList<>();
    ImageLoader imageLoader;
    public static DisplayImageOptions options;

    public HorizontalPagerAdapter(Context context, ArrayList<VideoDTO> trendingVideosList) {
        this.context = context;
        this.trendingVideosList = trendingVideosList;
        options = new DisplayImageOptions.Builder()
                .cacheOnDisk(true)
                .resetViewBeforeLoading(true)
                .cacheInMemory(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)
                .build();
        imageLoader = AppController.getInstance().getImageLoader();
        getScreenDimensions();
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cover, null);
        try {

            RelativeLayout rrMain = (RelativeLayout) view.findViewById(R.id.linMain);
            ImageView imageCover = (ImageView) view.findViewById(R.id.imageCover);
            final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progess_bar_trendingvideos);
            rrMain.setTag(position);

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                progressBar.setIndeterminateDrawable(AppController.getInstance().getApplication().getResources().getDrawable(R.drawable.circle_progress_bar_lower));
            } else {
                progressBar.setIndeterminateDrawable(ResourcesCompat.getDrawable(AppController.getInstance().getApplication().getResources(), R.drawable.progress_large_material, null));
            }


            com.nostra13.universalimageloader.core.ImageLoader.getInstance().
                    displayImage(trendingVideosList.get(position).getVideoStillUrl(),
                            imageCover, options, new ImageLoadingListener() {
                                @Override
                                public void onLoadingStarted(String s, View view) {
                                    progressBar.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onLoadingFailed(String s, View view, FailReason failReason) {
                                    progressBar.setVisibility(View.GONE);
                                }

                                @Override
                                public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                                    progressBar.setVisibility(View.GONE);
                                }

                                @Override
                                public void onLoadingCancelled(String s, View view) {
                                    progressBar.setVisibility(View.GONE);
                                }
                            });

            int aspectHeight = (displayWidth * 9) / 16;

            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    aspectHeight);
            //lp.setMargins(30,0,30,0);
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

        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }

    private int displayHeight = 0, displayWidth = 0;

    public void getScreenDimensions() {

        Point size = new Point();
        WindowManager w = HomeScreen.activity.getWindowManager();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            w.getDefaultDisplay().getSize(size);
            displayHeight = size.y;
            displayWidth = size.x;
        } else {
            Display d = w.getDefaultDisplay();
            displayHeight = d.getHeight();
            displayWidth = d.getWidth();
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
//        return HomeScreen.listItems.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == object);
    }

}