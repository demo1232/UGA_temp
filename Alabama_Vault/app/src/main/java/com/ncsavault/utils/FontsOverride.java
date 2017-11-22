package com.ncsavault.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import java.lang.reflect.Field;

/**
 * Class used for the using the font in whole the app.
 */

public final class FontsOverride {


    @SuppressWarnings("unused")
    protected static void replaceFont(String staticTypefaceFieldName,
                                      final Typeface newTypeface) {
        try {
            final Field staticField = Typeface.class
                    .getDeclaredField(staticTypefaceFieldName);
            staticField.setAccessible(true);
            staticField.set(null, newTypeface);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method using for custom font in app.
     * @param context reference of the Context
     *
     */
    public static void overrideFont(Context context) {
        try {
            final Typeface customFontTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Regular.ttf");

            final Field defaultFontTypefaceField = Typeface.class.getDeclaredField("SERIF");
            defaultFontTypefaceField.setAccessible(true);
            defaultFontTypefaceField.set(null, customFontTypeface);
        } catch (Exception e) {
            Log.e("TypeFaceUtils","Can not set custom font " + "fonts/OpenSans-Regular.ttf" + " instead of " + "SERIF");
        }
    }
}