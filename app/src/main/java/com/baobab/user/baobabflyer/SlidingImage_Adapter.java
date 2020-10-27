package com.baobab.user.baobabflyer;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Parcelable;
import androidx.annotation.RequiresApi;
import androidx.viewpager.widget.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import static androidx.core.content.ContextCompat.startActivity;

/**
 * Created by Parsania Hardik on 23/04/2016.
 */
public class SlidingImage_Adapter extends PagerAdapter {

    private LayoutInflater inflater;
    private Context context;
    private List<String> urlList;

    public SlidingImage_Adapter(Context context, List<String> urlList) {
        this.context = context;
        this.urlList = urlList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return urlList.size();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public Object instantiateItem(ViewGroup view, final int position) {
        View imageLayout = inflater.inflate( R.layout.slidesimages_layout, view, false);

        assert imageLayout != null;
        final ImageView imageView = (ImageView) imageLayout.findViewById(R.id.image);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        for(int i=0;i<urlList.size();i++){
            Log.d("슬라이드이미지 어뎁터" + String.valueOf(i), urlList.get(i));
        }

        Log.d("슬라이드이미지 어뎁터 포지션", String.valueOf(position));

        if(urlList != null){
            try {
                Log.d("슬라이드이미지 어뎁터", encodedUrl(urlList.get(position)));

                //Glide.clear(imageView);
                Glide.with(context).load(encodedUrl(urlList.get(position))).asBitmap()/*.signature(new StringSignature(UUID.randomUUID().toString())).diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)*/.into(imageView);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if(urlList.get( position ).contains( "baobabMainImg" )){
                imageView.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent( context, ImagedisplayLargeFragment.class );
                        intent.putExtra( "url", (Serializable) urlList);
                        startActivity( context, intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK), null );

                    }
                } );
            }
        }
        view.addView(imageLayout, 0);
        return imageLayout;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    public String encodedUrl(String url) throws UnsupportedEncodingException {
        return url.substring(0, url.lastIndexOf("/")) + "/" + URLEncoder.encode(url.substring(url.lastIndexOf("/")+1, url.length()-1), "UTF-8");
    }
}