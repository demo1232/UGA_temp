package com.ncsavault.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import applicationId.R;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.ncsavault.database.VaultDatabaseHelper;
import com.ncsavault.dto.PlaylistDto;
import com.ncsavault.dto.VideoDTO;
import com.ncsavault.utils.Utils;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Class using for to show the video list items after search videos in search box.
 * Used this class in VideoSearchActivity
 */

public class VideoSearchAdapter extends RecyclerView.Adapter<VideoSearchAdapter.SearchVideoViewHolder> {

    private final Activity context;
    private ArrayList<Object> objects = new ArrayList<>();
    private ArrayList<Object> filteredObjects = new ArrayList<>();
    private final SearchVideoClickListener searchVideoClickListener;
    private int displayWidth = 0;
    /**
     * This is custom interface to used that click the items of list.
     */
    public interface SearchVideoClickListener {
        void onClick(VideoSearchAdapter.SearchVideoViewHolder videoViewHolder, int position);
    }

    /**
     * Constructor
     * @param context Get the reference of Activity
     * @param objects Get the list of search video
     * @param searchVideoClickListener Click the search item
     */
    public VideoSearchAdapter(Activity context, ArrayList<Object> objects, SearchVideoClickListener searchVideoClickListener) {
        this.context = context;
        this.searchVideoClickListener = searchVideoClickListener;
        this.objects = objects;
        this.filteredObjects = new ArrayList<>();
        this.filteredObjects.addAll(objects);
        displayWidth =Utils.getScreenDimensions(context);
    }


    @Override
    public VideoSearchAdapter.SearchVideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.saved_video_layout_adapter, parent, false);

        return new SearchVideoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final SearchVideoViewHolder viewHolder, int position) {
        VideoDTO newVideoDto;
        PlaylistDto playlistDto;
        if (objects.get(position) instanceof VideoDTO) {
            newVideoDto = (VideoDTO) objects.get(position);
            final String videoImageUrl = newVideoDto.getVideoStillUrl();
            final String videoName = newVideoDto.getVideoName();
            final String videoDescription = newVideoDto.getVideoShortDescription();
            final long videoDuration = newVideoDto.getVideoDuration();

            try {
                viewHolder.progressBar.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(videoImageUrl)
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
                                                           Target<GlideDrawable> target, boolean isFromMemoryCache,
                                                           boolean isFirstResource) {
                                viewHolder.progressBar.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .into(viewHolder.videoImageView);

            } catch (Exception e) {
                e.printStackTrace();
            }

            int aspectHeight = ((displayWidth/2) * 9) / 16;

            RelativeLayout.LayoutParams mainLayout = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    aspectHeight);
            viewHolder.videoImageView.setLayoutParams(mainLayout);

            viewHolder.videoNameTextView.setText(videoName);
            viewHolder.videoDescriptionTextView.setText(videoDescription);

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

            try {
                viewHolder.progressBar.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(videoImageUrl)
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
                                                           Target<GlideDrawable> target, boolean isFromMemoryCache,
                                                           boolean isFirstResource) {
                                viewHolder.progressBar.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .into(viewHolder.videoImageView);

            } catch (Exception e) {
                e.printStackTrace();
            }

            int aspectHeight = ((displayWidth/2) * 9) / 16;

            RelativeLayout.LayoutParams mainLayout = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    aspectHeight);
            viewHolder.videoImageView.setLayoutParams(mainLayout);

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

    /**
     * Inner class we have to implemented for to
     * initialize the items of search video list using view holder here.
     */
    public class SearchVideoViewHolder extends RecyclerView.ViewHolder {

        private final ImageView videoImageView;
        public final ImageView savedVideoImageView;
        private final TextView videoNameTextView, videoDescriptionTextView, videoDurationTextView;
        private final ProgressBar progressBar;
        public final LinearLayout videoRelativeLayout;
        public final LinearLayout mLayoutSavedImage;

        public SearchVideoViewHolder(View view) {
            super(view);
            videoImageView = view.findViewById(R.id.imgVideoThumbNail);
            savedVideoImageView = view.findViewById(R.id.save_video_image);
            videoNameTextView = view.findViewById(R.id.tv_video_name);
            videoDescriptionTextView = view.findViewById(R.id.tv_video_description);
            videoDurationTextView = view.findViewById(R.id.tv_video_duration);
            progressBar = view.findViewById(R.id.progressbar);
            mLayoutSavedImage = view.findViewById(R.id.layout_saved_image);
            videoRelativeLayout = view.findViewById(R.id.save_video_main_layout);

        }
    }


    /**
     * This method is use to filter the list view according to text
     *
     * @param charText text to shown on video
     */
    public ArrayList<Object> filter(String charText) {
        try {
            charText = charText.toLowerCase(Locale.getDefault());
            objects.clear();
            if (charText.length() > 0) {
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
