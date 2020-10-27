package com.baobab.user.baobabflyer;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainPopup extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main_popup);

        Glide.with(this).load("https://baobabimage.cafe24.com/popup/popup.png").override(900, 900).into((ImageView) findViewById(R.id.img));

        CheckBox checkBox = findViewById(R.id.noSee);
        checkBox.setOnCheckedChangeListener(checkedChangeListener);

        findViewById(R.id.closeBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    CompoundButton.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            SharedPreferences spf = getSharedPreferences("popup", MODE_PRIVATE);
            SharedPreferences.Editor editor = spf.edit();
            if(isChecked){
                editor.putString("start", format.format(new Date()));
                editor.putBoolean("see", true);
            }else {
                editor.putBoolean("see", false);
            }
            editor.apply();
        }
    };
}
