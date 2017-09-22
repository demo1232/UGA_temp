package com.ncsavault.alabamavault.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.ncsavault.alabamavault.R;
import com.ncsavault.alabamavault.dto.CatagoriesTabDao;
import com.ncsavault.alabamavault.utils.ImageLoaderController;
import com.ncsavault.alabamavault.views.HomeScreen;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by gauravkumar.singh on 8/4/2017.
 */

public class CatagoriesAdapter extends RecyclerView.Adapter<CatagoriesAdapter.CatagoriesAdapterViewHolder> {

    public Context mContext;
    private OnClickInterface mOnClickInterface;
    public ArrayList<CatagoriesTabDao> mCatagoriesTabList = new ArrayList<>();
    public DisplayImageOptions options;


    public interface OnClickInterface
    {
        void onClick(CatagoriesAdapterViewHolder v,long tabPosition);
    }


    public CatagoriesAdapter(Context context, OnClickInterface onClickInterface,ArrayList<CatagoriesTabDao> CatagoriesTabList) {
        super();
        this.mContext = context;
        mOnClickInterface = onClickInterface;
        mCatagoriesTabList = CatagoriesTabList;

//        File cacheDir = StorageUtils.getCacheDirectory(mContext);
//        ImageLoaderConfiguration config;
//        config = new ImageLoaderConfiguration.Builder(mContext)
//                .threadPoolSize(3) // default
//                .denyCacheImageMultipleSizesInMemory()
//                .diskCache(new UnlimitedDiscCache(cacheDir))
//                .build();
//        ImageLoader.getInstance().init(config);

        options = new DisplayImageOptions.Builder()
                .cacheOnDisk(true).resetViewBeforeLoading(true)
                .cacheInMemory(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)
                .build();

        getScreenDimensions();
    }

    @Override
    public int getItemCount() {
        return mCatagoriesTabList.size();
    }

    @Override
    public CatagoriesAdapter.CatagoriesAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.catagories_tab_layout, parent, false);
        CatagoriesAdapterViewHolder viewHolder = new CatagoriesAdapterViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final CatagoriesAdapterViewHolder viewHolder, int position) {

        //CatagoriesTabDao catagoriesTabDao = mCatagoriesTabList.get(position);
        final String catagoriesTabImageUrl = mCatagoriesTabList.get(position).getCategoriesUrl();
        String catagoriesTabName = mCatagoriesTabList.get(position).getCategoriesName();
        long categoriesId = mCatagoriesTabList.get(position).getCategoriesId();

//        com.android.volley.toolbox.ImageLoader volleyImageLoader =
//                ImageLoaderController.getInstance(mContext).getImageLoader();
//
//        volleyImageLoader.get(catagoriesTabImageUrl,
//                com.android.volley.toolbox.ImageLoader.getImageListener(viewHolder.playlistImageView,
//                R.drawable.vault, R.drawable.vault));

//        volleyImageLoader.get(catagoriesTabImageUrl, new com.android.volley.toolbox.ImageLoader.ImageListener() {
//            @Override
//            public void onResponse(com.android.volley.toolbox.ImageLoader.ImageContainer response, boolean isImmediate) {
//                if (response != null) {
//                    Bitmap avatar = response.getBitmap();
//                    viewHolder.playlistImageView.setImageBitmap(avatar);
//                    viewHolder.progressBar.setVisibility(View.GONE);
//                }
//            }
//
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                viewHolder.progressBar.setVisibility(View.GONE);
//                try {
//                    viewHolder.playlistImageView.setImageResource(R.drawable.vault);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//
//
//        });

        try {
            ImageLoader.getInstance().displayImage(catagoriesTabImageUrl,
                    viewHolder.playlistImageView, options, new ImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String s, View view) {
                            viewHolder.progressBar.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onLoadingFailed(String s, View view, FailReason failReason) {
                            viewHolder.progressBar.setVisibility(View.GONE);
                            //  viewHolder.playlistImageView.setImageResource(R.drawable.vault);
                        }

                        @Override
                        public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                            viewHolder.progressBar.setVisibility(View.GONE);
                            if (catagoriesTabImageUrl == null) {
                                // viewHolder.playlistImageView.setImageResource(R.drawable.vault);
                            }
                        }

                        @Override
                        public void onLoadingCancelled(String s, View view) {
                            viewHolder.progressBar.setVisibility(View.GONE);
                            // viewHolder.playlistImageView.setImageResource(R.drawable.vault);
                        }
                    });

            int aspectHeight = (displayWidth * 8) / 16;

            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    aspectHeight);
            viewHolder.playlistImageView.setLayoutParams(lp);
        }catch (OutOfMemoryError e)
        {
            e.printStackTrace();
        }

        viewHolder.playlistTabNametextView.setText(catagoriesTabName);

        mOnClickInterface.onClick(viewHolder,categoriesId);
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



    public class CatagoriesAdapterViewHolder extends RecyclerView.ViewHolder {

        public ImageView playlistImageView;
        TextView playlistTabNametextView;
        private ProgressBar progressBar;
        private RelativeLayout playlistLayout;

        public CatagoriesAdapterViewHolder(View view) {
            super(view);
            playlistImageView = (ImageView) view.findViewById(R.id.tv_playlist_image);
            playlistTabNametextView = (TextView) view.findViewById(R.id.tv_playlist_name);
            progressBar = (ProgressBar) view.findViewById(R.id.progressbar);
           // progressBar.setVisibility(View.VISIBLE);
            playlistLayout = (RelativeLayout)  view.findViewById(R.id.playlist_layout);
//            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
//                progressBar.setIndeterminateDrawable(mContext.getResources().getDrawable(R.drawable.circle_progress_bar_lower));
//            } else {
//                System.out.println("progress bar not showing ");
//                progressBar.setIndeterminateDrawable(ResourcesCompat.getDrawable(mContext.getResources(), R.drawable.progress_large_material, null));
//            }

        }
    }


}

