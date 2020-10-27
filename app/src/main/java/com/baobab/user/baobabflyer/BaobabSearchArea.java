package com.baobab.user.baobabflyer;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class BaobabSearchArea extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_baobab_search_area );
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0,0);
        //액티비티 애니메이션 x
        finish();
    }
}
