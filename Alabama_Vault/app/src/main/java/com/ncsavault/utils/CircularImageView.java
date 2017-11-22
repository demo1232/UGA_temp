package com.ncsavault.utils;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Class used for create a custom circular image.
 */

public class CircularImageView extends ShaderImageView {

    /**
     * Constructor of the class
     * @param context the reference of the Context.
     */
    public CircularImageView(Context context) {
        super(context);
    }

    /**
     * Constructor of the class
     * @param context the reference of the Context.
     * @param attrs AttributeSet
     */
    public CircularImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Constructor of the class
     * @param context the reference of the Context.
     * @param attrs AttributeSet
     * @param defStyle defStyle
     */
    public CircularImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public ShaderHelper createImageViewHelper() {
        return new CircleShader();
    }
}
