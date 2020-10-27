package com.baobab.user.baobabflyer.server.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baobab.user.baobabflyer.R;

import org.apmem.tools.layouts.FlowLayout;

public class MenuThemes {
    String themes;
    FlowLayout motherLayout;
    Context context;

    public MenuThemes(String themes, FlowLayout motherLayout, Context context) {
        this.themes = themes;
        this.motherLayout = motherLayout;
        this.context = context;

        String[] themeArr = makeThemes(themes);
        for(int i=0;i<themeArr.length;i++){
            motherLayout.addView(makeText(themeArr[i]));
        }
    }

    public TextView makeText(String theme){
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int size12 = Math.round(12 * dm.density);
        int size5 = Math.round(5 * dm.density);
        int size4 = Math.round(4 * dm.density);

        TextView themeTv = new TextView(context);
        FlowLayout.LayoutParams tvParam = new FlowLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tvParam.rightMargin = size4;
        tvParam.bottomMargin = size4;
        themeTv.setLayoutParams(tvParam);
        if(theme.contains("안심")){
            themeTv.setBackground(context.getDrawable(R.drawable.theme_background_safety));
            themeTv.setTextColor(Color.parseColor("#ffffff"));
        }else {
            themeTv.setBackground(context.getDrawable(R.drawable.theme_background));
            themeTv.setTextColor(Color.parseColor("#333333"));
        }
        themeTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        themeTv.setLetterSpacing(-0.1f);
        themeTv.setText("#" + theme);
        themeTv.setPadding(size12, size5, size12, size5);

        return themeTv;
    }

    public String[] makeThemes(String themes){
        return themes.substring(1, themes.length() - 1).split(", ");
    }
}
