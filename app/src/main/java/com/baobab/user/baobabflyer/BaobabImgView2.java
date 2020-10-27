package com.baobab.user.baobabflyer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import java.io.FileNotFoundException;
import java.io.IOException;

public class BaobabImgView2 extends AppCompatActivity {

    Bitmap bm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_baobab_img_view2 );

        ImageView imageView = (ImageView) findViewById( R.id.imgView );
        Intent imgUrlInent = getIntent();
        String uri = imgUrlInent.getStringExtra( "bankUri" );

        if (uri != null && !uri.isEmpty()) {
            Uri mUri = Uri.parse( uri );

            try {
                bm = MediaStore.Images.Media.getBitmap( getContentResolver(), mUri );
                /*imageView.setImageBitmap( bm );*/
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.d( "오류1", e.getLocalizedMessage() );
            } catch (IOException e) {
                e.printStackTrace();
                Log.d( "오류2", e.getLocalizedMessage() );
            }
            imageView.setImageBitmap( bm );
        }
    }
}

