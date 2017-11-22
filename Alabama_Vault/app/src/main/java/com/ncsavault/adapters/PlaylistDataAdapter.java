package com.ncsavault.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import applicationId.R;

import com.ncsavault.controllers.AppController;
import com.ncsavault.dto.PlaylistDto;
import com.ncsavault.utils.Utils;
import com.ncsavault.views.HomeScreen;

import java.util.ArrayList;

/**
 *  Class used for to showing the list of playlist data.
 *  And also we are showing Inline Ads after a particular position in a list.
 *  Used this class in PlaylistFragment
 */
public class PlaylistDataAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Activity mContext;
    public static final int TYPE_LIST_DATA = 2;
    public static final int TYPE_AD = 3;
    private ArrayList<PlaylistDto> mPlaylistDtoArrayList = new ArrayList<>();
    private final PlaylistDataClickListener mPlaylistDataClickListener;
    private int displayWidth = 0;

    /**
     * This is custom interface to used that click the items of recycler view.
     */
    public interface PlaylistDataClickListener
    {
        void onClick(MyViewHolder viewHolder,long playlistId, String playlistName);
    }

    /**
     * Constructor
     * @param mContext Ge the reference of Activity
     * @param playlistDataClickListener Click on playlist item
     * @param playlistDtoArrayList Get the list of playlist item.
     */
    public PlaylistDataAdapter(Activity mContext, PlaylistDataClickListener playlistDataClickListener,
                               ArrayList<PlaylistDto> playlistDtoArrayList) {
        this.mContext = mContext;
        this.mPlaylistDtoArrayList = playlistDtoArrayList;
        mPlaylistDataClickListener = playlistDataClickListener;
        displayWidth =Utils.getScreenDimensions(mContext);
    }

    /**
     * Inner class we have to implemented for to
     * initialize the items of playlist data using view holder here.
     */
    public class MyViewHolder extends RecyclerView.ViewHolder {

        public final TextView playlistName;
        public final ImageView thumbnail;
        final ProgressBar progressBar;
        public RelativeLayout relativeLayout;

        public MyViewHolder(View view) {
            super(view);
            playlistName = view.findViewById(R.id.video_name);
            thumbnail = view.findViewById(R.id.thumbnail);
            progressBar = view.findViewById(R.id.progressbar);
            relativeLayout = view.findViewById(R.id.main_layout);
        }
    }

    /**
     * Inner class we have to implemented for to
     * initialize the items of Inline Ads using view holder here.
     */
    public class NativeAdsViewHolder extends RecyclerView.ViewHolder {

        public final RelativeLayout adViewLayout;

        public NativeAdsViewHolder(View itemView) {
            super(itemView);

            adViewLayout = itemView.findViewById(R.id.adView_layout);
        }

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;
        if (viewType == TYPE_LIST_DATA) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.album_card, parent, false);

            return new MyViewHolder(itemView);
        } else if (viewType == TYPE_AD) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.inline_playlist_ads, parent, false);

            return new NativeAdsViewHolder(view);
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        if ((position + 1) % 7 == 0) {
            return TYPE_AD;
        }
        return TYPE_LIST_DATA;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        int viewType = getItemViewType(position);
        switch (viewType) {
            case TYPE_LIST_DATA:
                displayPlaylistData(holder, position);
                break;
            case TYPE_AD:
                adMobBannerAdvertising(holder, position);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mPlaylistDtoArrayList.size();
    }

    /**
     * Method used for to showing the list of playlist data.
     * @param holder Reference of view holder class
     * @param position Get the position of particular item
     */
    private void displayPlaylistData(RecyclerView.ViewHolder holder, int position) {
        final MyViewHolder viewHolder = (MyViewHolder) holder;
        final String playlistImageUrl = mPlaylistDtoArrayList.get(position).getPlaylistThumbnailUrl();
        String playlistName = mPlaylistDtoArrayList.get(position).getPlaylistName();
        long playlistId = mPlaylistDtoArrayList.get(position).getPlaylistId();


        try {
        viewHolder.progressBar.setVisibility(View.VISIBLE);
        Glide.with(mContext)
                .load(playlistImageUrl)
                .placeholder(R.drawable.vault)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model,
                                    Target<GlideDrawable> target, boolean isFirstResource) {
                        viewHolder.progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model,
                                   Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        viewHolder.progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(viewHolder.thumbnail);

               } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        int aspectHeight = ((displayWidth/2) * 9) / 16;

        RelativeLayout.LayoutParams mainLayout = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                aspectHeight);
        viewHolder.thumbnail.setLayoutParams(mainLayout);
        viewHolder.playlistName.setText(playlistName);
        mPlaylistDataClickListener.onClick(viewHolder,playlistId, playlistName);

    }

    /**
     * Method used for to showing the list of Inline ads after a particular position.
     * @param holder reference of view holder class
     * @param position Get the position of particular item
     */
    private void adMobBannerAdvertising(RecyclerView.ViewHolder holder, int position) {

        NativeAdsViewHolder vhHeader = (NativeAdsViewHolder) holder;
        PlaylistDto playlistDto = mPlaylistDtoArrayList.get(position);
        AdView mAdView = new AdView(mContext);

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
            wt = 395;
        }
        Log.i("Width ","111Width : "+wt +"111Height :"+ht);
        mAdView.setAdSize(new AdSize(wt,ht));
        mAdView.setAdUnitId(playlistDto.getPlaylistName());
        AdRequest request = new AdRequest.Builder()
                .build();
        mAdView.loadAd(request);
        vhHeader.adViewLayout.addView(mAdView);
    }

}
