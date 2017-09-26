package com.ncsavault.alabamavault.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.ncsavault.alabamavault.R;
import com.ncsavault.alabamavault.controllers.AppController;
import com.ncsavault.alabamavault.database.VaultDatabaseHelper;
import com.ncsavault.alabamavault.dto.VideoDTO;
import com.ncsavault.alabamavault.utils.ImageLoaderController;
import com.ncsavault.alabamavault.utils.Utils;
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
import java.util.concurrent.TimeUnit;

/**
 * Created by gauravkumar.singh on 6/12/2017.
 */

public class SavedVideoAdapter extends RecyclerView.Adapter<SavedVideoAdapter.SavedVideoViewHolder> {

     private Context mContext;
     private ArrayList<VideoDTO> mFavoriteVideoList = new ArrayList<>();
    public DisplayImageOptions options;
    private SavedClickListener mSavedClickListener;

    public interface SavedClickListener
    {
        void onClick(SavedVideoAdapter.SavedVideoViewHolder videoViewHolder,int position);
    }
    public SavedVideoAdapter(Context context,ArrayList<VideoDTO> favoriteVideoList,
                             SavedClickListener savedClickListener, DisplayImageOptions displayImageOptions ) {
        super();
        this.mContext = context;
        mFavoriteVideoList = favoriteVideoList;
        mSavedClickListener = savedClickListener;
        options = displayImageOptions;

//        File cacheDir = StorageUtils.getCacheDirectory(mContext);
//        ImageLoaderConfiguration config;
//        config = new ImageLoaderConfiguration.Builder(mContext)
//                .threadPoolSize(3) // default
//                .denyCacheImageMultipleSizesInMemory()
//                .diskCache(new UnlimitedDiscCache(cacheDir))
//                .build();
//        ImageLoader.getInstance().init(config);

//        options = new DisplayImageOptions.Builder()
//                .cacheOnDisk(true).resetViewBeforeLoading(true)
//                .cacheInMemory(true)
//                .bitmapConfig(Bitmap.Config.RGB_565)
//                .imageScaleType(ImageScaleType.EXACTLY)
//                .build();

    }

    @Override
    public int getItemCount() {
        return mFavoriteVideoList.size();
    }

    @Override
    public SavedVideoAdapter.SavedVideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.saved_video_layout_adapter, parent, false);
        SavedVideoViewHolder viewHolder = new SavedVideoViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final SavedVideoAdapter.SavedVideoViewHolder viewHolder, int position) {

        VideoDTO newVideoDto = mFavoriteVideoList.get(position);
        System.out.println("tab is saved");
        final String videoImageUrl = newVideoDto.getVideoStillUrl();
        String videoName = newVideoDto.getVideoName();
        String videDescription = newVideoDto.getVideoShortDescription();
        long videoDuration = newVideoDto.getVideoDuration();

        try {
            ImageLoader.getInstance().displayImage(videoImageUrl,
                    viewHolder.videoImageView, options, new ImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String s, View view) {
                            viewHolder.progressBar.setVisibility(View.VISIBLE);
                            // viewHolder.videoImageView.setImageResource(R.drawable.vault);

                        }

                        @Override
                        public void onLoadingFailed(String s, View view, FailReason failReason) {
                            viewHolder.progressBar.setVisibility(View.GONE);
                            // viewHolder.videoImageView.setImageResource(R.drawable.vault);
                        }

                        @Override
                        public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                            viewHolder.progressBar.setVisibility(View.GONE);
                            if (videoImageUrl == null) {
                                //  viewHolder.videoImageView.setImageResource(R.drawable.vault);
                            }
                        }

                        @Override
                        public void onLoadingCancelled(String s, View view) {
                            viewHolder.progressBar.setVisibility(View.GONE);
                        }
                    });
        }catch (Exception error)
        {
            error.printStackTrace();
            System.out.println("Exception Saved "+error.getMessage());
        }

        viewHolder.videoNameTextView.setText(videoName);
        viewHolder.videoDescriptionTextView.setText(videDescription);

        if(videoDuration != 0) {
            viewHolder.videoDurationTextView.setText(Utils.convertSecondsToHMmSs(videoDuration));
        }

        if (newVideoDto.isVideoIsFavorite())
            viewHolder.savedVideoImageView.setImageResource(R.drawable.saved_video_img);
        else
            viewHolder.savedVideoImageView.setImageResource(R.drawable.video_save);

        if (VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).isFavorite(newVideoDto.getVideoId()))
            viewHolder.savedVideoImageView.setImageResource(R.drawable.saved_video_img);
        else
            viewHolder.savedVideoImageView.setImageResource(R.drawable.video_save);

        mSavedClickListener.onClick(viewHolder,position);

    }



    public static class SavedVideoViewHolder extends RecyclerView.ViewHolder {

        public ImageView videoImageView,savedVideoImageView;
        TextView videoNameTextView,videoDescriptionTextView,videoDurationTextView;
        private ProgressBar progressBar;
        public LinearLayout videoRelativeLayout;
        public LinearLayout mLayoutSavedImage;

        public SavedVideoViewHolder(View view) {
            super(view);
            videoImageView = (ImageView) view.findViewById(R.id.imgVideoThumbNail);
            savedVideoImageView = (ImageView) view.findViewById(R.id.save_video_image);
            videoNameTextView = (TextView) view.findViewById(R.id.tv_video_name);
            videoDescriptionTextView = (TextView) view.findViewById(R.id.tv_video_description);
            videoDurationTextView = (TextView) view.findViewById(R.id.tv_video_duration);
            progressBar = (ProgressBar) view.findViewById(R.id.progressbar);
            progressBar.setVisibility(View.VISIBLE);
            mLayoutSavedImage = (LinearLayout) view.findViewById(R.id.layout_saved_image);

//            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
//                progressBar.setIndeterminateDrawable(mContext.getResources().getDrawable(R.drawable
//                        .circle_progress_bar_lower));
//            } else {
//                System.out.println("progress bar not showing ");
//                progressBar.setIndeterminateDrawable(ResourcesCompat.getDrawable(mContext.getResources(),
//                        R.drawable.progress_large_material, null));
//            }

            videoRelativeLayout = (LinearLayout)view.findViewById(R.id.save_video_main_layout);

        }
    }

    public String convertSecondsToHMmSs(long millis) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);
        String duration = "";
        if (minutes > 60) {
            // convert the minutes to Hours:Minutes:Seconds
        } else {
            /*
             * if (minutes < 10) duration += "0" + minutes; else duration +=
			 * minutes;
			 */
            duration += minutes;
            if (seconds < 10)
                duration += ":0" + seconds;
            else
                duration += ":" + seconds;
        }
        return duration;
    }
}
