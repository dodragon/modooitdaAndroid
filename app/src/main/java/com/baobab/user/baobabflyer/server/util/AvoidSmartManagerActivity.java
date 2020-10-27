package com.baobab.user.baobabflyer.server.util;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.baobab.user.baobabflyer.R;

public class AvoidSmartManagerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avoid_smart_manager);
        overridePendingTransition(0, 0);

        finish();
    }
}
