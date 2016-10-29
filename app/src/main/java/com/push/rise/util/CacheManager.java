package com.push.rise.util;

import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.util.LruCache;
import android.util.Base64;
import android.widget.ImageView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.RequestFuture;
import com.push.rise.R;
import com.push.rise.fragment.DetailPost;
import com.push.rise.interfaces.CacheManager.ImageCacheDelegate;
import com.push.rise.model.ImageQueueSingleton;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.internal.DiskLruCache;
import com.squareup.okhttp.internal.DiskLruCache.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by christopher on 7/22/16.
 */


public class CacheManager implements ComponentCallbacks2 {
    private LruCache<String, Bitmap> imageMemoryCache;
    private static Context mContext;
    private static CacheManager mInstance;

    private DiskLruCache mDiskLruCache;
    private final Object mDiskCacheLock = new Object();
    private boolean mDiskCacheStarting = true;
    private static final int DISK_CACHE_SIZE = 1024 * 1024 * 10; // 10MB
    private static final String DISK_CACHE_SUBDIR = "thumbnails";


    private CacheManager(Context context) {
        mContext = context;

        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        imageMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };

        // Initialize disk cache on background thread
        File cacheDir = getDiskCacheDir(context, DISK_CACHE_SUBDIR);
        new InitDiskCacheTask().execute(cacheDir);

    }

    public static synchronized CacheManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new CacheManager(context);
        }
        return mInstance;
    }

    private void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        key = AeSimpleSHA1.SHA1(key);

        if (getBitmapFromMemCache(key) == null) {
            imageMemoryCache.put(key, bitmap);
        }

        // Also add to disk cache
        synchronized (mDiskCacheLock) {
            try {
                if (mDiskLruCache != null && mDiskLruCache.get(key) == null) {
                    Editor editor = mDiskLruCache.edit(key);
                    editor.set(0, bitmapToString(bitmap));
                    editor.commit();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private Bitmap getBitmapFromMemCache(String key) {
        key = AeSimpleSHA1.SHA1(key);
        return imageMemoryCache.get(key);
    }

    private Bitmap getBitmapFromDiskCache(String key) {
        key = AeSimpleSHA1.SHA1(key);
        synchronized (mDiskCacheLock) {
            // Wait while disk cache is started from background thread
            while (mDiskCacheStarting) {
                try {
                    mDiskCacheLock.wait();
                } catch (InterruptedException e) {}
            }
            if (mDiskLruCache != null) {
                try {
                    Snapshot snapshot = mDiskLruCache.get(key);
                    if(snapshot == null){
                        return null;
                    }
                    return stringToBitmap(mDiskLruCache.get(key).getString(0));
                } catch (IOException e)
                {}
            }
        }
        return null;
    }

    public void loadBitmap(final String imageUrl, final ImageView imageView, final ImageCacheDelegate delegate) {
        loadBitmap(imageUrl, imageView, delegate, false);
    }

    public Bitmap loadBitmap(final String imageUrl, final ImageView imageView,
                           final ImageCacheDelegate delegate, Boolean serial) {
        // Check disk cache in background thread
        CheckImageCacheTaskParams params = new CheckImageCacheTaskParams(imageUrl, imageView, delegate);

        CheckImageCacheTask task = new CheckImageCacheTask();
        if(!serial) {
            task.execute(params);
            return null;
        } else {
            try {
                params = task.doInBackground(params);
                Bitmap bitmap = params.bitmap;
                if(params.bitmap == null){
                    bitmap = task.getImage(imageUrl, imageView, delegate, true);
                }

                return bitmap;
            } catch (Exception e){
                return null;
            }
        }
    }

    // Make sure this is run on a background thread
    public Bitmap retreiveBitmapFromCache(final String imageUrl) {
        // Check disk cache in background thread
        CheckImageCacheTaskParams params = new CheckImageCacheTaskParams(imageUrl, null, null);

        CheckImageCacheTask task = new CheckImageCacheTask();
        try {
            params = task.doInBackground(params);
            Bitmap bitmap = params.bitmap;
            /*if(params.bitmap == null){
                bitmap = task.getImage(imageUrl, null, null, false);
            }*/

            return bitmap;
        } catch (Exception e){
            return null;
        }
    }


        // Creates a unique subdirectory of the designated app cache directory. Tries to use external
    // but if not mounted, falls back on internal storage.
    public static File getDiskCacheDir(Context context, String uniqueName) {
        // Check if media is mounted or storage is built-in, if so, try and use external cache dir
        // otherwise use internal cache dir
        final String cachePath =
                Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ||
                        !Environment.isExternalStorageRemovable() ? context.getExternalCacheDir().getPath() :
                        context.getCacheDir().getPath();

        return new File(cachePath + File.separator + uniqueName);
    }

    public final static String bitmapToString(Bitmap in){

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        in.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        return Base64.encodeToString(bytes.toByteArray(),Base64.DEFAULT);
    }
    public final static Bitmap stringToBitmap(String in){
        byte[] bytes = Base64.decode(in, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }


    class InitDiskCacheTask extends AsyncTask<File, Void, Void> {
        @Override
        protected Void doInBackground(File... params) {
            synchronized (mDiskCacheLock) {
                File cacheDir = params[0];
                try {
                    mDiskLruCache = DiskLruCache.open(cacheDir, 1, 1, DISK_CACHE_SIZE);
                } catch (IOException e) {}
                mDiskCacheStarting = false; // Finished initialization
                mDiskCacheLock.notifyAll(); // Wake any waiting threads
            }
            return null;
        }
    }

    public void onConfigurationChanged(Configuration configuration){

    }

    public void onLowMemory() {
        try {
            mDiskLruCache.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onTrimMemory (int level) {
        try {
            mDiskLruCache.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static class CheckImageCacheTaskParams {
        String imageUrl;
        ImageView imageView;
        ImageCacheDelegate delegate;
        Bitmap bitmap;

        CheckImageCacheTaskParams(String imageUrl, ImageView imageView, ImageCacheDelegate delegate) {
            this.imageUrl = imageUrl;
            this.imageView = imageView;
            this.delegate = delegate;
        }
    }

    class CheckImageCacheTask extends AsyncTask<CheckImageCacheTaskParams, Void, CheckImageCacheTaskParams> {
        @Override
        protected CheckImageCacheTaskParams doInBackground(CheckImageCacheTaskParams... params) {
            CheckImageCacheTaskParams checkImageCacheTaskParams = params[0];
            final String imageUrl = checkImageCacheTaskParams.imageUrl;

            Bitmap bitmap = getBitmapFromMemCache(imageUrl);
            if (bitmap != null) {
                checkImageCacheTaskParams.bitmap = bitmap;
            }

            bitmap = getBitmapFromDiskCache(imageUrl);

            if(bitmap != null){
                checkImageCacheTaskParams.bitmap = bitmap;
            }

            return checkImageCacheTaskParams;
        }

        @Override
        protected void onPostExecute(CheckImageCacheTaskParams checkImageCacheTaskParams){
            final String imageUrl = checkImageCacheTaskParams.imageUrl;
            final ImageView imageView = checkImageCacheTaskParams.imageView;
            final ImageCacheDelegate delegate = checkImageCacheTaskParams.delegate;
            final Bitmap bitmap = checkImageCacheTaskParams.bitmap;

            if(bitmap != null){
                delegate.didLoadImage(bitmap, imageView, checkImageCacheTaskParams.imageUrl);
                return;
            }

            getImage(imageUrl, imageView, delegate, false);
        }

        public Bitmap getImage(final String imageUrl, final ImageView imageView, final ImageCacheDelegate delegate, Boolean serial) {
            ImageRequest request = new ImageRequest(imageUrl,
                    new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap bitmap) {

                            addBitmapToMemoryCache(imageUrl, bitmap);

                            if(delegate.getClass().isAssignableFrom(DetailPost.class)) {
                                Fragment fragment = (Fragment)delegate;
                                if(!fragment.isAdded()){
                                    return;
                                }
                            }

                            delegate.didLoadImage(bitmap, imageView, imageUrl);
                        }

                    }, mContext.getResources().getDisplayMetrics().widthPixels, 0, ImageView.ScaleType.CENTER_INSIDE, null,
                    new Response.ErrorListener() {
                        public void onErrorResponse(VolleyError error) {
                            delegate.errorLoadingImage(imageView, imageUrl);
                        }
                    });

            request.setShouldCache(false);
            if(serial) {
                RequestFuture<Bitmap> future = RequestFuture.newFuture();
                future.setRequest(request);

                try {
                    Bitmap bitmap = future.get(20, TimeUnit.SECONDS);
                    bitmap = Bitmap.createScaledBitmap(bitmap, 500, 500, false);

                    return bitmap;
                } catch (TimeoutException e) {
                    // exception handling
                }catch (InterruptedException e) {
                    // exception handling
                } catch (ExecutionException e) {
                    // exception handling
                }
            } else {
                ImageQueueSingleton.getInstance(mContext).addToRequestQueue(request);
            }


            return null;
        }
    }



}
