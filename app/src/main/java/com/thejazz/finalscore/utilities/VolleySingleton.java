package com.thejazz.finalscore.utilities;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Created by TheJazz on 02/08/16.
 */
public class VolleySingleton {
    private static VolleySingleton sInstance = null;
    private static RequestQueue requestQueue;
    private static ImageLoader imageLoader;

    private VolleySingleton() {
        requestQueue = Volley.newRequestQueue(MyApplication.getAppContext());
        imageLoader = new ImageLoader(requestQueue, new ImageLoader.ImageCache() {
            private LruCache<String, Bitmap> cache = new LruCache<>((int) (Runtime.getRuntime().maxMemory() / 1024) / 8);

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

    public static VolleySingleton getInstance() {
        if (sInstance == null)
            sInstance = new VolleySingleton();
        return sInstance;
    }

    public RequestQueue getRequestQueue() {
        return requestQueue;
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }

}
