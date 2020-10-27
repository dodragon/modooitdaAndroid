package com.baobab.user.baobabflyer;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.baobab.user.baobabflyer.activityLoader.UpdateEventLoader;
import com.baobab.user.baobabflyer.server.vo.EventCpVO;

public class BaobabUpdateCpEvent extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baobab_update_cp_event);

        Intent get = getIntent();
        EventCpVO vo = (EventCpVO) get.getSerializableExtra("vo");
        int mainSeq = get.getIntExtra("mainSeq", 0);

        new UpdateEventLoader(this, vo, mainSeq);

        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
