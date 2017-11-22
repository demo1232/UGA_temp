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
import com.ncsavault.dto.VideoDTO;
import com.ncsavault.utils.Utils;
import java.util.ArrayList;

/**
 * Class using for to show the list of video,Thumbnail and video Name
 * After click of playlist item from playlist screen.
 * Used this class in VideoDetailFragment
 */

public class VideoDetailAdapter extends RecyclerView.Adapter<VideoDetailAdapter.VideoViewHolder> {

    private final Context mContext;
    private ArrayList<VideoDTO> mVideoDtoArrayList = new ArrayList<>();
    private final VideoClickListener mVideoClickListener;
    private int displayWidth = 0;

    /**
     * This is custom interface to used that click the items of list.
     */
    public interface VideoClickListener {
        void onClick(VideoDetailAdapter.VideoViewHolder videoViewHolder, int position);
    }

    /**
     * Constructor
     * @param context Get the reference of Activity
     * @param videoDtoArrayList Get the list of video data
     * @param videoClickListener Click the list of video items
     */
    public VideoDetailAdapter(Activity context, ArrayList<VideoDTO> videoDtoArrayList,
                              VideoClickListener videoClickListener) {
        super();
        mContext = context;
        mVideoDtoArrayList = videoDtoArrayList;
        mVideoClickListener = videoClickListener;
        displayWidth =Utils.getScreenDimensions(context);
    }

    @Override
    public int getItemCount() {
        return mVideoDtoArrayList.size();
    }

    @Override
    public VideoDetailAdapter.VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.saved_video_layout_adapter, parent, false);
        return new VideoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final VideoDetailAdapter.VideoViewHolder viewHolder, int position) {

        VideoDTO newVideoDto = mVideoDtoArrayList.get(position);
        final String videoImageUrl = newVideoDto.getVideoStillUrl();
        final String videoName = newVideoDto.getVideoName();
        final String videoDescription = newVideoDto.getVideoShortDescription();
        final long videoDuration = newVideoDto.getVideoDuration();


        try {
         viewHolder.progressBar.setVisibility(View.VISIBLE);
        Glide.with(mContext)
                .load(videoImageUrl)
                .placeholder(R.drawable.vault)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target,
                                               boolean isFirstResource) {
                        viewHolder.progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        viewHolder.progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(viewHolder.videoImageView);

                    
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }


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

        if (VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).
                isFavorite(newVideoDto.getVideoId())) {
            viewHolder.savedVideoImageView.setImageResource(R.drawable.saved_video_img);
        } else {
            viewHolder.savedVideoImageView.setImageResource(R.drawable.video_save);
        }
        int aspectHeight = ((displayWidth/2) * 9) / 16;

        RelativeLayout.LayoutParams mainLayout = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                aspectHeight);
        viewHolder.videoImageView.setLayoutParams(mainLayout);
        mVideoClickListener.onClick(viewHolder, position);

    }

    /**
     * Inner class we have to implemented for to
     * initialize the items of video list using view holder here.
     */
    public class VideoViewHolder extends RecyclerView.ViewHolder {

        private final ImageView videoImageView;
        public final ImageView savedVideoImageView;
        private final TextView videoNameTextView, videoDescriptionTextView, videoDurationTextView;
        private final ProgressBar progressBar;
        public final LinearLayout videoRelativeLayout;
        public final LinearLayout mLayoutSavedImage;

        public VideoViewHolder(View view) {
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


}
