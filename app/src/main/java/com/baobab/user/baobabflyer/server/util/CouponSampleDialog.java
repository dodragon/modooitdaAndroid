package com.baobab.user.baobabflyer.server.util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baobab.user.baobabflyer.R;

public class CouponSampleDialog extends Dialog {

    String[] samples;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount = 0.8f;
        getWindow().setAttributes(layoutParams);

        setContentView(R.layout.coupon_sample_dialog);
        LinearLayout mother = findViewById(R.id.textMother);

        for(int i=0;i<samples.length;i++){
            mother.addView(injectSample(String.valueOf((i+1) + ". " + samples[i])));
        }

        findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CouponSampleDialog.this.dismiss();
            }
        });
    }

    public TextView injectSample(String sample){
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int size3 = Math.round(3 * dm.density);

        TextView textView = new TextView(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, size3, 0, size3);
        textView.setLayoutParams(layoutParams);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        textView.setTextColor(Color.BLACK);
        textView.setText(sample);

        return textView;
    }

    public CouponSampleDialog(@NonNull Context context, String[] samples) {
        super(context);
        this.samples = samples;
        this.context = context;
    }
}
