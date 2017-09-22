package com.ncsavault.alabamavault.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ncsavault.alabamavault.R;
import com.ncsavault.alabamavault.controllers.AppController;
import com.ncsavault.alabamavault.database.VaultDatabaseHelper;
import com.ncsavault.alabamavault.dto.PlaylistDto;
import com.ncsavault.alabamavault.dto.VideoDTO;
import com.ncsavault.alabamavault.utils.Utils;
import com.ncsavault.alabamavault.views.VideoSearchActivity;
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
import java.util.Locale;

/**
 * Created by krunal.boxey on 8/16/2017.
 */

public class VideoSearchAdapter extends RecyclerView.Adapter<VideoSearchAdapter.SearchVideoViewHolder> {

    Context context;
    ArrayList<Object> objects;

    ArrayList<Object> filteredObjects;
    public DisplayImageOptions options;
    SearchVideoClickListener searchVideoClickListener;

    public interface SearchVideoClickListener {
        void onClick(VideoSearchAdapter.SearchVideoViewHolder videoViewHolder, int position);
    }

    public VideoSearchAdapter(Context context, ArrayList<Object> objects, SearchVideoClickListener searchVideoClickListener) {
        this.context = context;
        this.searchVideoClickListener = searchVideoClickListener;
        this.objects = objects;
        this.filteredObjects = new ArrayList<Object>();
        this.filteredObjects.addAll(objects);

//        File cacheDir = StorageUtils.getCacheDirectory(context);
//        ImageLoaderConfiguration config;
//        config = new ImageLoaderConfiguration.Builder(context)
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


    }


    @Override
    public VideoSearchAdapter.SearchVideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.saved_video_layout_adapter, parent, false);
        VideoSearchAdapter.SearchVideoViewHolder viewHolder = new VideoSearchAdapter.SearchVideoViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final SearchVideoViewHolder viewHolder, int position) {
        VideoDTO newVideoDto = null;
        PlaylistDto playlistDto = null;
        if (objects.get(position) instanceof VideoDTO) {
            newVideoDto = (VideoDTO) objects.get(position);
            final String videoImageUrl = newVideoDto.getVideoStillUrl();
            String videoName = newVideoDto.getVideoName();
            String videDescription = newVideoDto.getVideoShortDescription();
            long videoDuration = newVideoDto.getVideoDuration();


            try{
            com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(videoImageUrl,
                    viewHolder.videoImageView, options, new ImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String s, View view) {
                            viewHolder.progressBar.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onLoadingFailed(String s, View view, FailReason failReason) {
                            viewHolder.progressBar.setVisibility(View.GONE);
                           // viewHolder.videoImageView.setImageResource(R.drawable.vault);
                        }

                        @Override
                        public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                            viewHolder.progressBar.setVisibility(View.GONE);
                            if(videoImageUrl== null){
                              //  viewHolder.videoImageView.setImageResource(R.drawable.vault);
                            }
                        }

                        @Override
                        public void onLoadingCancelled(String s, View view) {
                            viewHolder.progressBar.setVisibility(View.GONE);
                           // viewHolder.videoImageView.setImageResource(R.drawable.vault);
                        }
                    });
        }catch (OutOfMemoryError e)
        {
            e.printStackTrace();
        }

            viewHolder.videoNameTextView.setText(videoName);
            viewHolder.videoDescriptionTextView.setText(videDescription);

            if (videoDuration != 0) {
                viewHolder.videoDurationTextView.setText(Utils.convertSecondsToHMmSs(videoDuration));
            }

            if (newVideoDto.isVideoIsFavorite()) {
                viewHolder.savedVideoImageView.setImageResource(R.drawable.saved_video_img);
            } else {
                viewHolder.savedVideoImageView.setImageResource(R.drawable.video_save);
            }

            if (VaultDatabaseHelper.getInstance(context.getApplicationContext()).
                    isFavorite(newVideoDto.getVideoId())) {
                viewHolder.savedVideoImageView.setImageResource(R.drawable.saved_video_img);
            } else {
                viewHolder.savedVideoImageView.setImageResource(R.drawable.video_save);
            }


        } else if (objects.get(position) instanceof PlaylistDto) {

            playlistDto = (PlaylistDto) objects.get(position);
            String videoImageUrl = playlistDto.getPlaylistThumbnailUrl();
            String playlistName = playlistDto.getPlaylistName();
            String playListDescription = playlistDto.getPlaylistShortDescription();

            com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(videoImageUrl,
                    viewHolder.videoImageView, options, new ImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String s, View view) {
                            viewHolder.progressBar.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onLoadingFailed(String s, View view, FailReason failReason) {
                            viewHolder.progressBar.setVisibility(View.GONE);
                            //viewHolder.videoImageView.setImageResource(R.drawable.alabama_vault_logo);
                        }

                        @Override
                        public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                            viewHolder.progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onLoadingCancelled(String s, View view) {
                            viewHolder.progressBar.setVisibility(View.GONE);
                            //viewHolder.videoImageView.setImageResource(R.drawable.alabama_vault_logo);
                        }
                    });

            viewHolder.videoNameTextView.setText(playlistName);
            viewHolder.videoDescriptionTextView.setText(playListDescription);


            viewHolder.videoDurationTextView.setVisibility(View.GONE);
            viewHolder.savedVideoImageView.setVisibility(View.GONE);
        }

        searchVideoClickListener.onClick(viewHolder, position);

    }


    @Override
    public int getItemCount() {
        return objects.size();
    }

    public class SearchVideoViewHolder extends RecyclerView.ViewHolder {

        public ImageView videoImageView, savedVideoImageView;
        TextView videoNameTextView, videoDescriptionTextView, videoDurationTextView;
        private ProgressBar progressBar;
        public LinearLayout videoRelativeLayout;
        public LinearLayout mLayoutSavedImage;

        public SearchVideoViewHolder(View view) {
            super(view);
            videoImageView = (ImageView) view.findViewById(R.id.imgVideoThumbNail);
            savedVideoImageView = (ImageView) view.findViewById(R.id.save_video_image);
            videoNameTextView = (TextView) view.findViewById(R.id.tv_video_name);
            videoDescriptionTextView = (TextView) view.findViewById(R.id.tv_video_description);
            videoDurationTextView = (TextView) view.findViewById(R.id.tv_video_duration);
            progressBar = (ProgressBar) view.findViewById(R.id.progressbar);
            mLayoutSavedImage = (LinearLayout) view.findViewById(R.id.layout_saved_image);
//            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
//                progressBar.setIndeterminateDrawable(context.getResources().getDrawable(R.drawable
//                        .circle_progress_bar_lower));
//            } else {
//                System.out.println("progress bar not showing ");
//                progressBar.setIndeterminateDrawable(ResourcesCompat.getDrawable(context.getResources(),
//                        R.drawable.progress_large_material, null));
//            }

            videoRelativeLayout = (LinearLayout) view.findViewById(R.id.save_video_main_layout);

        }
    }


    /**
     * This methos is use to filter the lisview according to text-------
     *
     * @param charText
     */
    public ArrayList<Object> filter(String charText) {
        try {
            charText = charText.toLowerCase(Locale.getDefault());
            objects.clear();
            if (charText.length() == 0) {
//                objects.addAll(filteredObjects);
            } else {
                for (Object object : filteredObjects) {
                    if (object instanceof VideoDTO) {
                        VideoDTO videoDTO = (VideoDTO) object;
                        if ((videoDTO.getVideoShortDescription().toLowerCase(Locale.getDefault())
                                .contains(charText) ||
                                videoDTO.getVideoName().toLowerCase(Locale.getDefault()).contains(charText) ||
                                videoDTO.getVideoTags().toLowerCase(Locale.getDefault()).contains(charText))) {

                            objects.add(videoDTO);
                        }
                    } else if (object instanceof PlaylistDto) {
                        PlaylistDto playlistDto = (PlaylistDto) object;
                        if ((playlistDto.getPlaylistShortDescription().toLowerCase(Locale.getDefault())
                                .contains(charText) ||
                                playlistDto.getPlaylistName().toLowerCase(Locale.getDefault()).contains(charText) ||
                                playlistDto.getPlaylistTags().toLowerCase(Locale.getDefault()).contains(charText))) {

                            objects.add(playlistDto);
                        }

                    }

                }
            }
            notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return objects;
    }
}
