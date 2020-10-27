package com.baobab.user.baobabflyer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import androidx.annotation.RequiresApi;
import androidx.viewpager.widget.PagerAdapter;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class AutoScrollAdapter extends PagerAdapter {

    Context context;
    ArrayList<String> data;
    int[] imgFlags;

    public AutoScrollAdapter(Context context, ArrayList<String> data, int[] imgFlags) {
        this.context = context;
        this.data = data;
        this.imgFlags = imgFlags;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View v = inflater.inflate(R.layout.auto_viewpager, null);
        ImageView image_container = v.findViewById(R.id.image_container);

        Display display = ((Activity)context).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        float width = size.x;

        Glide.with(context).load(data.get(position)).override((int)width, (int)dpToPixels(315, context)).centerCrop().into(image_container);

        container.addView( v );
        image_container.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( context, ImagedisplayLargeFragment.class );
                intent.putExtra( "url", data );
                intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                context.startActivity( intent );
            }
        } );
        return v;
    }

    public float dpToPixels(float dp, Context context){
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, dm);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView( (View) object );
    }

    @Override
    public int getCount() {
        boolean result = true;
        for (int i = 0; i < data.size(); i++) {
            if (data.get( i ) != null) {
                result = false;
                break;
            }
        }
        if (result) {
            return 0;
        } else {
            return data.size();
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}