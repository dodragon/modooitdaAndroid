package com.baobab.user.baobabflyer.server.util;

import android.content.Context;
import android.graphics.Bitmap;
import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import android.widget.ImageView;

import com.bumptech.glide.request.target.BitmapImageViewTarget;

public class UIUtil {
    public static BitmapImageViewTarget getRoundedImageTarget(@NonNull final Context context, @NonNull final ImageView imageView,
                                                              final float radius) {
        return new BitmapImageViewTarget(imageView) {
            @Override
            protected void setResource(final Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                circularBitmapDrawable.setCornerRadius(radius);
                imageView.setImageDrawable(circularBitmapDrawable);
            }
        };
    }
}
