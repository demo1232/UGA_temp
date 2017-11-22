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
 *  Class used for to showing the list of Favorite Videos.
 *  Used this class in SavedVideosFragment
 */

public class SavedVideoAdapter extends RecyclerView.Adapter<SavedVideoAdapter.SavedVideoViewHolder> {

    private final Activity mContext;
    private ArrayList<VideoDTO> mFavoriteVideoList = new ArrayList<>();
    private final SavedClickListener mSavedClickListener;
    private int displayWidth = 0;
    /**
     * This is custom interface to used that click the items of list.
     */
    public interface SavedClickListener {
        void onClick(SavedVideoAdapter.SavedVideoViewHolder videoViewHolder, int position);
    }

    /**
     * Constructor
     * @param context Get the reference of Activity
     * @param favoriteVideoList Get the list of favorite video
     * @param savedClickListener Click the item of list
     */
    public SavedVideoAdapter(Activity context, ArrayList<VideoDTO> favoriteVideoList,
                             SavedClickListener savedClickListener) {
        super();
        this.mContext = context;
        mFavoriteVideoList = favoriteVideoList;
        mSavedClickListener = savedClickListener;
        displayWidth =Utils.getScreenDimensions(mContext);
    }

    @Override
    public int getItemCount() {
        return mFavoriteVideoList.size();
    }

    @Override
    public SavedVideoAdapter.SavedVideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.saved_video_layout_adapter, parent, false);
        return new SavedVideoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final SavedVideoAdapter.SavedVideoViewHolder viewHolder, int position) {

        VideoDTO newVideoDto = mFavoriteVideoList.get(position);
        Log.d("SavedVideoAdapter", "tab is saved");
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
                    public boolean onResourceReady(GlideDrawable resource, String model,
                                     Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        viewHolder.progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(viewHolder.videoImageView);
                 } catch (Exception error) {
            error.printStackTrace();
            Log.e("SavedVideoAdapter", "Exception Saved " + error.getMessage());
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

        if (newVideoDto.isVideoIsFavorite())
            viewHolder.savedVideoImageView.setImageResource(R.drawable.saved_video_img);
        else
            viewHolder.savedVideoImageView.setImageResource(R.drawable.video_save);

        if (VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).isFavorite(newVideoDto.getVideoId()))
            viewHolder.savedVideoImageView.setImageResource(R.drawable.saved_video_img);
        else
            viewHolder.savedVideoImageView.setImageResource(R.drawable.video_save);

        mSavedClickListener.onClick(viewHolder, position);

    }


    /**
     * Inner class we have to implemented for to
     * initialize the items of Favorite videos using view holder .
     */
    public static class SavedVideoViewHolder extends RecyclerView.ViewHolder {

        private final ImageView videoImageView;
        public final ImageView savedVideoImageView;
        private final TextView videoNameTextView, videoDescriptionTextView, videoDurationTextView;
        private final ProgressBar progressBar;
        public final LinearLayout videoRelativeLayout;
        public final LinearLayout mLayoutSavedImage;

        public SavedVideoViewHolder(View view) {
            super(view);
            videoImageView = view.findViewById(R.id.imgVideoThumbNail);
            savedVideoImageView = view.findViewById(R.id.save_video_image);
            videoNameTextView = view.findViewById(R.id.tv_video_name);
            videoDescriptionTextView = view.findViewById(R.id.tv_video_description);
            videoDurationTextView = view.findViewById(R.id.tv_video_duration);
            progressBar = view.findViewById(R.id.progressbar);
            progressBar.setVisibility(View.VISIBLE);
            mLayoutSavedImage = view.findViewById(R.id.layout_saved_image);
            videoRelativeLayout = view.findViewById(R.id.save_video_main_layout);

        }
    }


}
