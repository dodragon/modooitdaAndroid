package com.baobab.user.baobabflyer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ScrollView;
import android.widget.Toast;

public class BaobabAcceptTerms3 extends AppCompatActivity {

    CheckBox firstCheck;
    ScrollView scrollView1;
    ScrollView scrollView2;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_baobab_accept_terms3 );

        firstCheck = findViewById( R.id.firstCheck );
        scrollView1 = findViewById( R.id.scrollView1 );
        scrollView2 = findViewById( R.id.scrollView2 );

        scrollView2.setOnTouchListener( new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    scrollView1.requestDisallowInterceptTouchEvent( false );
                } else {
                    scrollView1.requestDisallowInterceptTouchEvent( true );
                }
                return false;
            }
        } );

        firstCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Button commitBtn = findViewById(R.id.commitBtn);
                if(isChecked){
                    commitBtn.setBackgroundColor(Color.parseColor("#5c7cfa"));
                }else {
                    commitBtn.setBackgroundColor(Color.rgb(216, 220, 229));
                }
            }
        });

        Button commitBtn = findViewById( R.id.commitBtn );
        commitBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!firstCheck.isChecked()) {
                    Toast.makeText( BaobabAcceptTerms3.this, "첫번째 동의란을 체크해주세요.", Toast.LENGTH_SHORT ).show();
                    firstCheck.requestFocus();
                    return;
                } else {
                    Intent commitIntent = new Intent( BaobabAcceptTerms3.this, BaobabBeingMall.class );
                    startActivity( commitIntent );
                    finish();
                }

            }
        } );

        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}

