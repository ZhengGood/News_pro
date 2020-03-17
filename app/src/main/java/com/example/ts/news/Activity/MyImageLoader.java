package com.example.ts.news.Activity;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.youth.banner.loader.ImageLoader;

public class MyImageLoader extends ImageLoader {


    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
//        imageView.setImageResource(((BannerItem) path).pic);
        Glide.with(context).load((String) path).into(imageView);
    }


}
