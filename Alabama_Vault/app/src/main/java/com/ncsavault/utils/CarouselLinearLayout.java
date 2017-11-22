package com.ncsavault.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.ncsavault.adapters.CarouselPagerAdapter;

/**
 * Class used for Horizontal view pager on home screen.
 */
public class CarouselLinearLayout extends LinearLayout {
    private float scale = CarouselPagerAdapter.BIG_SCALE;

    /**
     * Constructor of the class
     * @param context reference of the Context.
     * @param attrs AttributeSet.
     */
    public CarouselLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Constructor of the class
     * @param context reference of the Context.
     */
    public CarouselLinearLayout(Context context) {
        super(context);
    }

    public void setScaleBoth(float scale) {
        this.scale = scale;
        this.invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // The main mechanism to display scale animation, you can customize it as your needs
        int w = this.getWidth();
        int h = this.getHeight();
        canvas.scale(scale, scale, w/2, h/2);
    }
}
