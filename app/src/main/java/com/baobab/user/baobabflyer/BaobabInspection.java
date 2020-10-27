package com.baobab.user.baobabflyer;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class BaobabInspection extends AppCompatActivity {

    String result = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baobab_inspection);

        result = getIntent().getStringExtra("status");
        ImageView inspection = findViewById(R.id.inspection);
        TextView text1 = findViewById(R.id.text1);
        TextView text2 = findViewById(R.id.text2);
        if(result.equals("점검")){
            inspection.setBackground(getDrawable(R.drawable.ic_server));
            text1.setText("죄송합니다.\n서버 점검 중 입니다.");
            text2.setText("잠시 후 다시 시도해주십시오.");
        }else {
            inspection.setBackground(getDrawable(R.drawable.ic_wifi_disconnected));
            text1.setText("네트워크가 불안정 합니다.");
            text2.setText("Wi-Fi연결 및 데이터 상태 확인 후\n다시 시도해주세요.");
        }
    }

    @Override
    public void onBackPressed() {
        if(result.equals("점검")){
            this.moveTaskToBack( true );
            this.finish();
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        }else {
            finish();
        }
    }
}
