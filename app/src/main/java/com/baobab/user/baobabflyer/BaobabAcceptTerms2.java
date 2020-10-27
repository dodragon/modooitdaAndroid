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


public class BaobabAcceptTerms2 extends AppCompatActivity {

    CheckBox secondCheck;
    CheckBox thirdCheck;
    CheckBox fourthCheck;
    ScrollView scrollView1;
    ScrollView scrollView2;
    ScrollView scrollView3;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_baobab_accept_terms2 );

        secondCheck = findViewById( R.id.secondCheck );
        thirdCheck = findViewById( R.id.thirdCheck );
        fourthCheck = findViewById( R.id.fourthCheck );
        scrollView1 = findViewById( R.id.scrollView1 );
        scrollView2 = findViewById( R.id.scrollView2 );
        scrollView3 = findViewById( R.id.scrollView3 );

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

        scrollView3.setOnTouchListener( new View.OnTouchListener() {
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

        final CheckBox cbAll = findViewById( R.id.checkbox_all );
        cbAll.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cbAll.isChecked()) {
                    secondCheck.setChecked( true );
                    thirdCheck.setChecked( true );
                    fourthCheck.setChecked( true );
                } else {
                    secondCheck.setChecked( false );
                    thirdCheck.setChecked( false );
                    fourthCheck.setChecked( false );
                }
            }
        } );

        CompoundButton.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                CheckBox[] box = {secondCheck, thirdCheck, fourthCheck};
                boolean allCheck = true;
                if (isChecked) {
                    for (int i = 0; i < box.length; i++) {
                        if (!box[i].isChecked()) {
                            allCheck = false;
                        }
                    }
                    if (allCheck) {
                        cbAll.setChecked( true );
                    }
                } else {
                    cbAll.setChecked( false );
                }

                Button commitBtn = findViewById(R.id.commitBtn);

                if(secondCheck.isChecked() && thirdCheck.isChecked()){
                    commitBtn.setBackgroundColor(Color.parseColor("#5c7cfa"));
                }else {
                    commitBtn.setBackgroundColor(Color.rgb(216, 220, 229));
                }
            }
        };

        secondCheck.setOnCheckedChangeListener( checkedChangeListener );
        thirdCheck.setOnCheckedChangeListener( checkedChangeListener );
        fourthCheck.setOnCheckedChangeListener( checkedChangeListener );

        Button commitBtn = findViewById( R.id.commitBtn );
        commitBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!secondCheck.isChecked()) {
                    Toast.makeText( BaobabAcceptTerms2.this, "첫번째 동의란을 체크해주세요.", Toast.LENGTH_SHORT ).show();
                    secondCheck.requestFocus();
                    return;
                } else if (!thirdCheck.isChecked()) {
                    Toast.makeText( BaobabAcceptTerms2.this, "두번째 동의란을 체크해주세요.", Toast.LENGTH_SHORT ).show();
                    thirdCheck.requestFocus();
                    return;
                } else {
                    Intent commitIntent = new Intent( BaobabAcceptTerms2.this, BaobabUserSignIn.class );
                    BaobabUserSignIn baobabUserSignIn = new BaobabUserSignIn();
                    commitIntent.putExtra( "check", baobabUserSignIn.pushCheck( fourthCheck ) );
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
