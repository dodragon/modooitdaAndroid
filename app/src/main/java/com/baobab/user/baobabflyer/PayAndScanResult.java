package com.baobab.user.baobabflyer;

import android.app.Activity;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.baobab.user.baobabflyer.server.util.Scanner;
import com.bumptech.glide.Glide;

public class PayAndScanResult extends AppCompatActivity {

    String div;
    String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_and_scan_result);

        Intent getData = getIntent();

        div = getData.getStringExtra("div");
        title = getData.getStringExtra("title");
        String status = getData.getStringExtra("status");

        ((TextView)findViewById(R.id.statusTitle)).setText(title);
        ((TextView)findViewById(R.id.status)).setText(status);

        ImageView iconView = findViewById(R.id.resultIcon);
        Button backBtn = findViewById(R.id.back);
        if(div.startsWith("scan")){
            backBtn.setText("스캔하기");
            if(div.contains("성공")){
                Glide.with(this).load(R.drawable.success).into(iconView);
                String scanner = getIntent().getStringExtra("scanner");
                if(scanner.equals("user")){
                    backBtn.setText("티켓함가기");
                }else {
                    backBtn.setText("돌아가기");
                }
            }else {
                Glide.with(this).load(R.drawable.error).into(iconView);
                backBtn.setText("돌아가기");
            }
        }else if(div.equals("pay(성공)")){
            if (title.equals("계좌 등록이 완료되었습니다.") || title.contains("자유")){
                backBtn.setText("돌아가기");
            }else {
                backBtn.setText("티켓함으로 가기");
                Glide.with(this).load(R.drawable.success).into(iconView);
            }
        }else if(div.equals("pay(실패)") | div.equals("push(실패)")){
            backBtn.setText("다시 결제하기");
            Glide.with(this).load(R.drawable.error).into(iconView);
        }else if(div.startsWith("결제취소")){
            if(div.contains("성공")){
                backBtn.setText("티켓함으로 가기");
                Glide.with(this).load(R.drawable.success).into(iconView);
            }else {
                backBtn.setText("다시하기");
                Glide.with(this).load(R.drawable.error).into(iconView);
            }
        }else if(div.equals("push(성공)")){
            backBtn.setText("푸시보내기");
            Glide.with(this).load(R.drawable.success).into(iconView);
        }
        backBtn.setOnClickListener(back);
        findViewById(R.id.home).setOnClickListener(home);
    }

    View.OnClickListener back = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(div.equals("pay(실패)") | div.equals("결제취소(실패)") | div.equals("결제취소(오류)") | div.equals("push(실패)")){
                finish();
            }else if(div.equals("pay(성공)") | div.equals("결제취소(성공)")){
                if(title.equals("계좌 등록이 완료되었습니다.") || title.contains("자유")){
                    finish();
                }else {
                    if(div.equals("결제취소(성공)")){
                        ((Activity) BaobabUserTicketList.CONTEXT).finish();
                    }
                    Intent home = new Intent(PayAndScanResult.this, BaobabUserTicketList.class);
                    startActivity(home);
                    finish();
                }
            }else if(div.startsWith("scan")){
                String scanner = getIntent().getStringExtra("scanner");
                if(scanner.equals("cp")){
                    Intent home = new Intent(PayAndScanResult.this, Scanner.class);
                    startActivity(home);
                    finish();
                }else {
                    ((Activity) BaobabUserTicketList.CONTEXT).finish();
                    Intent home = new Intent(PayAndScanResult.this, BaobabUserTicketList.class);
                    startActivity(home);
                    finish();
                }
            }else if(div.equals("push(성공)")){
                ((Activity)BaobabPush.context).finish();
                Intent home = new Intent(PayAndScanResult.this, BaobabPush.class);
                home.putExtra("cpName", getIntent().getStringExtra("cpName"));
                home.putExtra("cpSeq", getIntent().getIntExtra("cpSeq", 0));
                startActivity(home);
                finish();
            }
        }
    };

    View.OnClickListener home = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent home = new Intent(PayAndScanResult.this, BaobabAnterMain.class);
            startActivity(home);
            finishAffinity();
        }
    };

    @Override
    public void onBackPressed(){
        if(div.equals("pay(실패)") | div.equals("결제취소(실패)") | div.equals("결제취소(오류)")){
            finish();
        }else if(div.equals("pay(성공)") | div.equals("결제취소(성공)")){
            if(div.equals("결제취소(성공)")){
                ((Activity) BaobabUserTicketList.CONTEXT).finish();
            }
            finish();
        }else if(div.startsWith("scan")){
            String scanner = getIntent().getStringExtra("scanner");
            if(scanner.equals("user")){
                ((Activity) BaobabUserTicketList.CONTEXT).finish();
                Intent home = new Intent(PayAndScanResult.this, BaobabUserTicketList.class);
                startActivity(home);
                finish();
            }else {
                Intent home = new Intent(PayAndScanResult.this, Scanner.class);
                startActivity(home);
                finish();
            }
        }
    }
}
