package com.ncsavault.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import applicationId.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.ncsavault.dto.CategoriesTabDao;
import com.ncsavault.utils.Utils;
import java.util.ArrayList;

/**
 *  This adapter is used in CategoriesFragment to show list of tabs
 */

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoriesAdapterViewHolder> {

    private final OnClickInterface mOnClickInterface;
    private ArrayList<CategoriesTabDao> mCategoriesTabList = new ArrayList<>();
    private int displayWidth = 0;
    private final Activity mContext;

    /**
     * This is custom interface to used that click the items of recycler view.
     */
    public interface OnClickInterface {
        void onClick(CategoriesAdapterViewHolder v, long tabPosition, String categoryName);
    }


    /**
     * Constructor
     * @param onClickInterface Click the item of list
     * @param CategoriesTabList Get the tab data list
     */
    public CategoriesAdapter(OnClickInterface onClickInterface, ArrayList<CategoriesTabDao>
            CategoriesTabList, Activity context) {
        super();
        mContext=context;
        mOnClickInterface = onClickInterface;
        mCategoriesTabList = CategoriesTabList;
        displayWidth = Utils.getScreenDimensions(mContext);

    }

    @Override
    public int getItemCount() {
        Log.i("CategoriesAdapter","categories list size : "+ mCategoriesTabList.size());
        return mCategoriesTabList.size();
    }

    @Override
    public CategoriesAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.catagories_tab_layout, parent, false);
        return new CategoriesAdapterViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final CategoriesAdapterViewHolder viewHolder, int position) {

        final String categoriesTabImageUrl = mCategoriesTabList.get(position).getCategoriesUrl();
        String categoriesTabName = mCategoriesTabList.get(position).getCategoriesName();
        long categoriesId = mCategoriesTabList.get(position).getCategoriesId();
        try {
                viewHolder.progressBar.setVisibility(View.VISIBLE);
                Glide.with(mContext)
                        .load(categoriesTabImageUrl)
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
                        .into(viewHolder.playlistImageView);


        } catch (Exception e) {
            e.printStackTrace();
            Log.e("CategoriesAdapter", "Exception Categories " + e.getMessage());
        }

        int aspectHeight = (displayWidth * 8) / 16;
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                aspectHeight);
        viewHolder.playlistImageView.setLayoutParams(lp);
        viewHolder.playlistTabNameTextView.setText(categoriesTabName);
        mOnClickInterface.onClick(viewHolder, categoriesId,categoriesTabName);
    }


    /**
     * Inner class we have to implemented for
     * initialize the items of adapter using view holder here.
     */
    public static class CategoriesAdapterViewHolder extends RecyclerView.ViewHolder {

        public final ImageView playlistImageView;
        private final TextView playlistTabNameTextView;
        private final ProgressBar progressBar;


        /**
         * Constructor
         * @param view use the reference of view and initialize item.
         */
        public CategoriesAdapterViewHolder(View view) {
            super(view);
            playlistImageView = view.findViewById(R.id.tv_playlist_image);
            playlistTabNameTextView = view.findViewById(R.id.tv_playlist_name);
            progressBar = view.findViewById(R.id.progressbar);


        }
    }


}

