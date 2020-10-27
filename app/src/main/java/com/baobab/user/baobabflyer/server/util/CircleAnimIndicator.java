package com.baobab.user.baobabflyer.server.util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class CircleAnimIndicator extends LinearLayout {

    private Context context;
    private int itemMargin;
    private int animDuration;
    private int defaultCircle;
    private int selectCircle;

    private ImageView[] imgDot;

    public void setAnimDuration(int animDuration){
        this.animDuration = animDuration;
    }

    public void setItemMargin(int itemMargin){
        this.itemMargin = itemMargin;
    }

    public CircleAnimIndicator(Context context) {
        super(context);
        this.context = context;
    }

    public CircleAnimIndicator(Context context, AttributeSet attr){
        super(context, attr);
        this.context = context;
    }

    public void createDotPanel(int count, int defaultCircle, int selectCircle){
        this.defaultCircle = defaultCircle;
        this.selectCircle = selectCircle;

        imgDot = new ImageView[count];

        for(int i=0;i<count;i++){
            imgDot[i] = new ImageView(context);
            LinearLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(itemMargin, itemMargin, itemMargin, itemMargin);
            params.gravity = Gravity.CENTER;

            imgDot[i].setLayoutParams(params);
            imgDot[i].setImageResource(defaultCircle);
            imgDot[i].setTag(imgDot[i].getId(), false);
            this.addView(imgDot[i]);
        }

        selectDot(0);
    }

    public void selectDot(int position){
        for(int i=0;i<imgDot.length;i++){
            if(i == position){
                imgDot[i].setImageResource(selectCircle);
                selectScaleAnim(imgDot[i], 1.5f, 1f);

                if(i == 0){
                    imgDot[imgDot.length - 1].setImageResource(defaultCircle);
                    defaultScaleAnim(imgDot[imgDot.length - 1], 1.5f, 1f);
                }
            }else {
                imgDot[i].setImageResource(defaultCircle);
                defaultScaleAnim(imgDot[i], 1.5f, 1f);
            }
        }
    }

    public void selectScaleAnim(View view, float startScale, float endScale){
        Animation anim = new ScaleAnimation(
                startScale, endScale,
                startScale, endScale,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setFillAfter(true);
        anim.setDuration(animDuration);
        view.startAnimation(anim);
        view.setTag(view.getId(), true);
    }

    public void defaultScaleAnim(View view, float startScale, float endScale){
        Animation anim = new ScaleAnimation(
                startScale, endScale,
                startScale, endScale,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setFillAfter(true);
        anim.setDuration(animDuration);
        view.startAnimation(anim);
        view.setTag(view.getId(), false);
    }
}
