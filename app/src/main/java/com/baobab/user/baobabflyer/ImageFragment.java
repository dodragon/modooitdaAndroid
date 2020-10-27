package com.baobab.user.baobabflyer;

import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.signature.StringSignature;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import java.util.UUID;

public class ImageFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image, container, false);

        final SubsamplingScaleImageView imageView = view.findViewById( R.id.imageView );

        if (getArguments() != null) {
            Bundle args = getArguments();
            String url = args.getString( "url" );

            Glide.clear(imageView);
            Glide.with( imageView.getContext() ).load(url).asBitmap().signature(new StringSignature(UUID.randomUUID().toString()))
                    .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    imageView.setImage( ImageSource.bitmap( resource ) );
                }
            } );
        }

        return view;
    }
}