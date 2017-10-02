package com.ncsavault.customviews;

import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;

/**
 * Created by gauravkumar.singh on 8/21/2017.
 */

public class RecyclerViewDisabler implements RecyclerView.OnItemTouchListener {

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        return true;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }
}
