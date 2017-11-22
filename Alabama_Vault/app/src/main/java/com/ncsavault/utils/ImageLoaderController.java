package com.ncsavault.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;

/**
 * Class used for handle image caching in app.
 */

public class ImageLoaderController {

    @SuppressLint("StaticFieldLeak")
    private static ImageLoaderController mImageLoaderInstance = null;
    private final Context mContext;
    private RequestQueue mRequestQueue;
    private final ImageLoader mImageLoader;

    /**
     * Constructor of the class
     * @param ctx reference of the Context.
     */
    private ImageLoaderController(Context ctx) {
        mContext = ctx;
        mRequestQueue = getRequestQueue();

        mImageLoader = new ImageLoader(mRequestQueue,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap>
                            cache = new LruCache<>(20);

                    @Override
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }

                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }
                });
    }

    /**
     * Method usd for make it single instance.
     * @param context the reference
     * @return the instance of the ImageLoaderController.
     */
    public static synchronized ImageLoaderController getInstance(Context context) {
        if (mImageLoaderInstance == null) {
            mImageLoaderInstance = new ImageLoaderController(context);
        }
        return mImageLoaderInstance;
    }

    /**
     * Method used for DiskBasedCache the image
     * @return the instance of RequestQueue.
     */
    private RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            Cache cache = new DiskBasedCache(mContext.getCacheDir(), 10 * 1024 * 1024);
            Network network = new BasicNetwork(new HurlStack());
            mRequestQueue = new RequestQueue(cache, network);
            // Don't forget to start the volley request queue
            mRequestQueue.start();
        }
        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }
}
