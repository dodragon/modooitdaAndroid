package com.baobab.user.baobabflyer;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.baobab.user.baobabflyer.server.vo.NoticeVO;

import java.text.SimpleDateFormat;

public class BaobabNoticeDetail extends AppCompatActivity {

    String backContext = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onNewIntent(getIntent());
        makeDetail((NoticeVO) getIntent().getSerializableExtra("notiVO"));
    }

    public void makeDetail(NoticeVO vo) {
        TextView title = findViewById(R.id.noticeTitle);
        title.setText(vo.getTitle());

        TextView content = findViewById(R.id.noticeContent);
        content.setText(vo.getContent());

        TextView date = findViewById(R.id.noticeDate);
        SimpleDateFormat format = new SimpleDateFormat("yyyy년 MM월 dd일 HH:mm");
        date.setText(format.format(vo.getNoti_date()));

        findViewById(R.id.backNotice).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle extras = intent.getExtras();
        if (extras != null) {
            setContentView(R.layout.activity_baobab_notice_detail);
            if (extras.getString("context") != null) {
                backContext = extras.getString("context");
            }
            makeDetail((NoticeVO) extras.getSerializable("notiVO"));
        } else {
            Log.d("엑스트라 : ", "널 !!!!!!!!!");
        }
    }

    @Override
    public void onBackPressed(){
        Intent intent = null;
        if(backContext.equals("push")){
            intent = new Intent(BaobabNoticeDetail.this, BaobabAnterMain.class);
            startActivity(intent);
            finish();
        }else {
            finish();
        }
    }
}