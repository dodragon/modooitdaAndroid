package com.baobab.user.baobabflyer;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.baobab.user.baobabflyer.server.util.ViewAnimationUtil;
import com.baobab.user.baobabflyer.server.vo.CPInfoVO;

public class BaobabMenuRevise extends AppCompatActivity {

    CPInfoVO vo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_baobab_menu_revice );

        Intent getVo = getIntent();
        vo = (CPInfoVO) getVo.getSerializableExtra("vo");
        final String divCode = getSharedPreferences("user", 0).getString("divCode", "");

        layoutOpenAndClose((LinearLayout) findViewById(R.id.menu_btn), (LinearLayout) findViewById(R.id.menu_layout));
        layoutOpenAndClose((LinearLayout) findViewById(R.id.stat_btn), (LinearLayout) findViewById(R.id.stat_layout));

        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        findViewById(R.id.menuUpdate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(divCode.equals("c-01-01")){
                    Intent intent = new Intent(BaobabMenuRevise.this, BaobabCpJoin.class);
                    intent.putExtra("cpName", vo.getCP_name());
                    intent.putExtra("cpSeq", vo.getSeq_num());
                    intent.putExtra("activity", getIntent().getStringExtra("activity"));
                    startActivity(intent);
                    finish();
                }else {
                    Toast.makeText(BaobabMenuRevise.this, "권한이 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.mainImgUpdate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(divCode.equals("c-01-01") || divCode.equals("c-02-02")){
                    Intent intent = new Intent(BaobabMenuRevise.this, BaobabMainImgUpload.class);
                    intent.putExtra("cpSeq", vo.getSeq_num());
                    intent.putExtra("cpName", vo.getCP_name());
                    startActivity(intent);
                }else {
                    Toast.makeText(BaobabMenuRevise.this, "권한이 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.menuAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(divCode.equals("c-01-01") || divCode.equals("c-02-02")){
                    Intent intent = new Intent(BaobabMenuRevise.this, BaobabCpMenuInfo.class);
                    intent.putExtra("getCpNameFromJoin", vo.getCP_name());
                    intent.putExtra("cpSeq", vo.getSeq_num());
                    startActivity(intent);
                }else {
                    Toast.makeText(BaobabMenuRevise.this, "권한이 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.menuModify).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(divCode.equals("c-01-01") || divCode.equals("c-02-02")){
                    Intent intent = new Intent(BaobabMenuRevise.this, BaobabMenuModified.class);
                    intent.putExtra("cpName", vo.getCP_name());
                    intent.putExtra("cpSeq", vo.getSeq_num());
                    intent.putExtra("select", "전체보기");
                    startActivity(intent);
                }else {
                    Toast.makeText(BaobabMenuRevise.this, "권한이 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.sendPush).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(BaobabMenuRevise.this, "준비중인 서비스 입니다.", Toast.LENGTH_SHORT).show();
                /*if(divCode.equals("c-01-01")){
                    Intent intent = new Intent(BaobabMenuRevise.this, BaobabPush.class);
                    intent.putExtra("cpName", vo.getCP_name());
                    intent.putExtra("cpSeq", vo.getSeq_num());
                    intent.putExtra("vo", vo);
                    startActivity(intent);
                }else {
                    Toast.makeText(BaobabMenuRevise.this, "권한이 없습니다.", Toast.LENGTH_SHORT).show();
                }*/
            }
        });

        findViewById(R.id.cp_total).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BaobabMenuRevise.this, BaobabCpStatistics.class);
                intent.putExtra("cpSeq", vo.getSeq_num());
                startActivity(intent);
            }
        });

        findViewById(R.id.event_total).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BaobabMenuRevise.this, BaobabEventStatistics.class);
                intent.putExtra("cpSeq", vo.getSeq_num());
                startActivity(intent);
            }
        });

        findViewById(R.id.pay_history).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BaobabMenuRevise.this, BaobabCpPayHistory.class);
                intent.putExtra("cpSeq", vo.getSeq_num());
                startActivity(intent);
            }
        });
    }

    private void layoutOpenAndClose(LinearLayout btn, final LinearLayout layout){
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewAnimationUtil viewAnimationUtil = new ViewAnimationUtil();
                if(layout.getVisibility() == View.VISIBLE){
                    ((LinearLayout)v).getChildAt(3).setVisibility(View.VISIBLE);
                    viewAnimationUtil.collapse(layout);
                }else {
                    ((LinearLayout)v).getChildAt(3).setVisibility(View.GONE);
                    viewAnimationUtil.expand(layout);
                }
            }
        });
    }
}