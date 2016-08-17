package com.doing.library.http;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

/**
 * Created by Doing on 2016/8/9 0009.
 */
public class ImageLoaderUtils {

    public static void loadGif(Context context, Integer resourceId, Integer placeHolderId, ImageView imageView){
        Glide.with(context)
                .load(resourceId)
                .asGif()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
//                .placeholder(placeHolderId)
//                .centerCrop()
                .into(imageView);
    }

    public static void loadGif(Context context, String url, ImageView imageView){
        Glide.with(context)
                .load(url)
                .asGif()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
//                .centerCrop()
                .into(imageView);
    }

}
