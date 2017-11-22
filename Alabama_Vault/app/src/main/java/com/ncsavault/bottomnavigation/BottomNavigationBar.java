package com.ncsavault.bottomnavigation;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.LinearLayout;

import applicationId.R;

import java.util.List;


/**
 * Helper class to handle bottom navigation UI and click events
 */

public class BottomNavigationBar implements View.OnClickListener {

    public static final int MENU_BAR_1 = 0;
    public static final int MENU_BAR_2 = 1;
    public static final int MENU_BAR_3 = 2;
    public static final int MENU_BAR_4 = 3;

    private final Context mContext;
    private final BottomNavigationMenuClickListener mListener;

    private final AppCompatImageView mImageViewBar1;
    private final AppCompatImageView mImageViewBar2;
    private final AppCompatImageView mImageViewBar3;
    private final AppCompatImageView mImageViewBar4;
    private final AppCompatTextView mTextViewBar1;
    private final AppCompatTextView mTextViewBar2;
    private final AppCompatTextView mTextViewBar3;
    private final AppCompatTextView mTextViewBar4;

    public BottomNavigationBar(Context mContext, List<NavigationPage> pages, BottomNavigationMenuClickListener listener) {

        // initialize variables
        this.mContext = mContext;
        AppCompatActivity mActivity = (AppCompatActivity) mContext;
        this.mListener = listener;

        // getting reference to bar linear layout view groups
        LinearLayout mLLBar1 = (LinearLayout) mActivity.findViewById(R.id.linearLayoutBar1);
        LinearLayout mLLBar2 = (LinearLayout) mActivity.findViewById(R.id.linearLayoutBar2);
        LinearLayout mLLBar3 = (LinearLayout) mActivity.findViewById(R.id.linearLayoutBar3);
        LinearLayout mLLBar4 = (LinearLayout) mActivity.findViewById(R.id.linearLayoutBar4);

        // getting reference to bar icons
        this.mImageViewBar1 = (AppCompatImageView) mActivity.findViewById(R.id.imageViewBar1);
        this.mImageViewBar2 = (AppCompatImageView) mActivity.findViewById(R.id.imageViewBar2);
        this.mImageViewBar3 = (AppCompatImageView) mActivity.findViewById(R.id.imageViewBar3);
        this.mImageViewBar4 = (AppCompatImageView) mActivity.findViewById(R.id.imageViewBar4);
        // setting the icons
        this.mImageViewBar1.setImageDrawable(pages.get(0).getIcon());
        this.mImageViewBar2.setImageDrawable(pages.get(1).getIcon());
        this.mImageViewBar3.setImageDrawable(pages.get(2).getIcon());
        this.mImageViewBar4.setImageDrawable(pages.get(3).getIcon());
        // getting reference to bar titles
        this.mTextViewBar1 = (AppCompatTextView) mActivity.findViewById(R.id.textViewBar1);
        this.mTextViewBar2 = (AppCompatTextView) mActivity.findViewById(R.id.textViewBar2);
        this.mTextViewBar3 = (AppCompatTextView) mActivity.findViewById(R.id.textViewBar3);
        this.mTextViewBar4 = (AppCompatTextView) mActivity.findViewById(R.id.textViewBar4);

        // Setting the titles
        this.mTextViewBar1.setText(pages.get(0).getTitle());
        this.mTextViewBar2.setText(pages.get(1).getTitle());
        this.mTextViewBar3.setText(pages.get(2).getTitle());
        this.mTextViewBar4.setText(pages.get(3).getTitle());
        // setting click listeners
        mLLBar1.setOnClickListener(this);
        mLLBar2.setOnClickListener(this);
        mLLBar3.setOnClickListener(this);
        mLLBar4.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        // setting clicked bar as highlighted view
        setView(view);

        // triggering click listeners
        if (view.getId() == R.id.linearLayoutBar1) {
            mListener.onClickedOnBottomNavigationMenu(MENU_BAR_1);
        } else if (view.getId() == R.id.linearLayoutBar2) {
            mListener.onClickedOnBottomNavigationMenu(MENU_BAR_2);
        } else if (view.getId() == R.id.linearLayoutBar3) {
            mListener.onClickedOnBottomNavigationMenu(MENU_BAR_3);
        } else if (view.getId() == R.id.linearLayoutBar4) {
            mListener.onClickedOnBottomNavigationMenu(MENU_BAR_4);
        }

    }


    /**
     * sets the clicked view as selected, resets other views
     *
     * @param view clicked view
     */
    public void setView(View view) {

        // resetting colors of all icons
        this.mImageViewBar1.setColorFilter(ContextCompat.getColor(mContext, R.color.black));
        this.mImageViewBar2.setColorFilter(ContextCompat.getColor(mContext, R.color.black));
        this.mImageViewBar3.setColorFilter(ContextCompat.getColor(mContext, R.color.black));
        this.mImageViewBar4.setColorFilter(ContextCompat.getColor(mContext, R.color.black));
        // resetting colors of all titles
        this.mTextViewBar1.setTextColor(ContextCompat.getColor(mContext, R.color.black));
        this.mTextViewBar2.setTextColor(ContextCompat.getColor(mContext, R.color.black));
        this.mTextViewBar3.setTextColor(ContextCompat.getColor(mContext, R.color.black));
        this.mTextViewBar4.setTextColor(ContextCompat.getColor(mContext, R.color.black));
        // selectively colorizing the marked view
        if (view.getId() == R.id.linearLayoutBar1) {
            this.mImageViewBar1.setColorFilter(ContextCompat.getColor(mContext, R.color.app_theme_color));
            this.mTextViewBar1.setTextColor(ContextCompat.getColor(mContext, R.color.app_theme_color));
        } else if (view.getId() == R.id.linearLayoutBar2) {
            this.mImageViewBar2.setColorFilter(ContextCompat.getColor(mContext, R.color.app_theme_color));
            this.mTextViewBar2.setTextColor(ContextCompat.getColor(mContext, R.color.app_theme_color));
        } else if (view.getId() == R.id.linearLayoutBar3) {
            this.mImageViewBar3.setColorFilter(ContextCompat.getColor(mContext, R.color.app_theme_color));
            this.mTextViewBar3.setTextColor(ContextCompat.getColor(mContext, R.color.app_theme_color));
        } else if (view.getId() == R.id.linearLayoutBar4) {
            this.mImageViewBar4.setColorFilter(ContextCompat.getColor(mContext, R.color.app_theme_color));
            this.mTextViewBar4.setTextColor(ContextCompat.getColor(mContext, R.color.app_theme_color));
        }

    }

    /**
     * Custom interface to click event of bottom tab
     */
    public interface BottomNavigationMenuClickListener {
        void onClickedOnBottomNavigationMenu(int menuType);
    }

}
