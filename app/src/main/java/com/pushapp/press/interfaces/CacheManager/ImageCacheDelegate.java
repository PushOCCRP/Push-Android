package com.pushapp.press.interfaces.CacheManager;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * Created by christopher on 7/22/16.
 */
public interface ImageCacheDelegate
{
    void didLoadImage(Bitmap bitmap, ImageView imageView, String url);
    void errorLoadingImage(ImageView imageView, String url);
}
