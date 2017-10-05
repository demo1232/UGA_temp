package com.ncsavault.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Build;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.NativeExpressAdView;
import applicationId.R;
import com.ncsavault.controllers.AppController;
import com.ncsavault.dto.PlaylistDto;
import com.ncsavault.dto.TopTenVideoDto;
import com.ncsavault.dto.VideoDTO;
import com.ncsavault.views.HomeScreen;
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
import java.util.List;

/**
 * Created by gauravkumar.singh on 14/08/17.
 */
public class PlaylistDataAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<TopTenVideoDto> albumList;
    public static final int TYPE_LIST_DATA = 2;
    public static final int TYPE_AD = 3;
    private ArrayList<PlaylistDto> mPlaylistDtoArrayList = new ArrayList<>();
    public DisplayImageOptions options;
    PlaylistDataClickListener mPlaylistDataClickListener;

    public interface PlaylistDataClickListener
    {
        void onClick(MyViewHolder viewHolder,long playlistId);
    }

    public PlaylistDataAdapter(Context mContext, PlaylistDataClickListener playlistDataClickListener,
                               ArrayList<PlaylistDto> playlistDtoArrayList) {
        this.mContext = mContext;
        this.mPlaylistDtoArrayList = playlistDtoArrayList;
        mPlaylistDataClickListener = playlistDataClickListener;

//        File cacheDir = StorageUtils.getCacheDirectory(mContext);
//        ImageLoaderConfiguration config;
//        config = new ImageLoaderConfiguration.Builder(mContext)
//                .threadPoolSize(3) // default
//                .denyCacheImageMultipleSizesInMemory()
//                .diskCache(new UnlimitedDiscCache(cacheDir))
//                .build();
//        ImageLoader.getInstance().init(config);
//        ImageLoader.getInstance().init(config);
        options = new DisplayImageOptions.Builder()
                .cacheOnDisk(true).resetViewBeforeLoading(true)
                .cacheInMemory(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)
                .build();


        getScreenDimensions();
    }

    public  class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView mVideoNumber, playlistName;
        public ImageView thumbnail;
        ProgressBar progressBar;

        public MyViewHolder(View view) {
            super(view);
            playlistName = (TextView) view.findViewById(R.id.video_name);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            progressBar = (ProgressBar) view.findViewById(R.id.progressbar);
            /*if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                progressBar.setIndeterminateDrawable(mContext.getResources().getDrawable(R.drawable.circle_progress_bar_lower));
            } else {
                System.out.println("progress bar not showing ");
                progressBar.setIndeterminateDrawable(ResourcesCompat.getDrawable(mContext.getResources(), R.drawable.progress_large_material, null));
            }*/
        }
    }

    public class NativeAdsViewHolder extends RecyclerView.ViewHolder{

        public AdView adView;
        public RelativeLayout adViewLayout;

        public NativeAdsViewHolder (View itemView) {
            super(itemView);
           // adView = (AdView) itemView.findViewById(R.id.adView);
            adViewLayout = (RelativeLayout) itemView.findViewById(R.id.adView_layout);
        }

    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = null;
        if(viewType== TYPE_LIST_DATA) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.album_card, parent, false);

            return new MyViewHolder(itemView);
        }else if(viewType== TYPE_AD)
        {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.inline_playlist_ads, parent, false);

            return new NativeAdsViewHolder(view);
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        if ((position+1) % 5 == 0) {
            return TYPE_AD;
        }
        return TYPE_LIST_DATA;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        int viewType = getItemViewType(position);
        switch (viewType) {
            case TYPE_LIST_DATA:
                displayPlaylistData(holder,position);
                break;
            case TYPE_AD:
                adMobBannerAdvertising(holder,position);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mPlaylistDtoArrayList.size();
    }

    private void displayPlaylistData(RecyclerView.ViewHolder holder,int position)
    {
        final MyViewHolder viewHolder = (MyViewHolder)holder;
        final String playlistImageUrl = mPlaylistDtoArrayList.get(position).getPlaylistThumbnailUrl();
        String playlistName = mPlaylistDtoArrayList.get(position).getPlaylistName();
        long playlistId = mPlaylistDtoArrayList.get(position).getPlaylistId();


        try{
        com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(playlistImageUrl,
                viewHolder.thumbnail, options, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String s, View view) {
                        viewHolder.progressBar.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onLoadingFailed(String s, View view, FailReason failReason) {
                        viewHolder.progressBar.setVisibility(View.GONE);
                       // viewHolder.thumbnail.setImageResource(R.drawable.vault);
                    }

                    @Override
                    public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                        viewHolder.progressBar.setVisibility(View.GONE);
                        if (playlistImageUrl == null) {
                          //  viewHolder.thumbnail.setImageResource(R.drawable.vault);
                        }
                    }

                    @Override
                    public void onLoadingCancelled(String s, View view) {
                        viewHolder.progressBar.setVisibility(View.GONE);
                       // viewHolder.thumbnail.setImageResource(R.drawable.vault);
                    }
                });
    }catch (OutOfMemoryError e)
    {
        e.printStackTrace();
    }

//        int aspectHeight = (displayWidth * 16) / 9;
//
//        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
//                aspectHeight);
//        //lp.setMargins(30,0,30,0);
//        viewHolder.thumbnail.setLayoutParams(lp);

        viewHolder.playlistName.setText(playlistName);
        mPlaylistDataClickListener.onClick(viewHolder,playlistId);

    }

    public void adMobBannerAdvertising(RecyclerView.ViewHolder holder,int position) {

        NativeAdsViewHolder vhHeader = (NativeAdsViewHolder)holder;
        PlaylistDto playlistDto = mPlaylistDtoArrayList.get(position);
        NativeExpressAdView mAdView = new NativeExpressAdView(mContext);

        mAdView.setAdSize(new AdSize(340,80));
        mAdView.setAdUnitId(playlistDto.getPlaylistName());
        vhHeader.adViewLayout.addView(mAdView);
        AdRequest request = new AdRequest.Builder()
                .addTestDevice("20B52AAB529851184340334B73A36E8B")
                .build();
        mAdView.loadAd(request);
        // Load the Native Express ad.
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

}
