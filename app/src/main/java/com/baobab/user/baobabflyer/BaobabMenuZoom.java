package com.baobab.user.baobabflyer;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class BaobabMenuZoom extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_baobab_menu_zoom );

        Intent getIntent = getIntent();
        String menuImg = getIntent.getStringExtra( "menuImg" );
        String menuName = getIntent.getStringExtra( "menuName" );
        String menuPrice = getIntent.getStringExtra( "menuPrice" );

        TextView menuNameTV = findViewById( R.id.menuName );
        menuNameTV.setText( menuName );

        ImageView menuImgIV = findViewById( R.id.menuImg );
        Glide.with( BaobabMenuZoom.this ).load( menuImg ).asBitmap().into( menuImgIV );

        TextView menuPriceTV = findViewById( R.id.menuPrice );
        menuPriceTV.setText( menuPrice );
    }
}
