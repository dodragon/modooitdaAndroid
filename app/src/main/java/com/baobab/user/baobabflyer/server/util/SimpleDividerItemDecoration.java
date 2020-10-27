package com.baobab.user.baobabflyer.server.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.baobab.user.baobabflyer.R;

public class SimpleDividerItemDecoration extends RecyclerView.ItemDecoration {
    private Drawable divider;

    public SimpleDividerItemDecoration(Context context){
        divider = context.getResources().getDrawable(R.drawable.line_divider);
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parant, RecyclerView.State state){
        int left = parant.getPaddingLeft();
        int right = parant.getWidth() - parant.getPaddingRight();

        int childCount = parant.getChildCount();
        for(int i=0;i<childCount;i++){
            View child = parant.getChildAt(i);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams)child.getLayoutParams();

            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + divider.getIntrinsicHeight();

            divider.setBounds(left, top, right, bottom);
            divider.draw(c);
        }
    }
}
