package com.baobab.user.baobabflyer;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.baobab.user.baobabflyer.activityLoader.MakeEventLoader;
import com.baobab.user.baobabflyer.server.singleTons.RetroSingleTon;

public class BaobabMakeCpEvent extends AppCompatActivity {

    RetroSingleTon retroSingleTon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baobab_make_cp_event);

        new MakeEventLoader(this, getIntent().getIntExtra("cpSeq", 0));

        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
