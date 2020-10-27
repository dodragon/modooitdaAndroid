package com.baobab.user.baobabflyer;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class BaobabBigQRcode extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baobab_big_qrcode);

        Bitmap img = getIntent().getParcelableExtra("qr");
        ImageView imgv = findViewById(R.id.imgView);
        imgv.setBackground(new BitmapDrawable(img));
    }
}
