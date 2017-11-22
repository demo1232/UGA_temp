package com.ncsavault.customviews;

import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;

/**
 * Class to restrict the list of recycler view.
 */

public class RecyclerViewDisable implements RecyclerView.OnItemTouchListener {

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
