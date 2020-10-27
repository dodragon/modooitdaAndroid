package com.baobab.user.baobabflyer;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.baobab.user.baobabflyer.server.singleTons.RetroSingleTon;
import com.baobab.user.baobabflyer.server.vo.UserAllVO;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BaobabFindEmail extends AppCompatActivity {

    RetroSingleTon retroSingleTon;
    String phoneNumber = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baobab_find_email);

        phoneNumber = getIntent().getStringExtra("phone");

        Call<List<UserAllVO>> call = retroSingleTon.getInstance().getRetroInterface(getApplicationContext()).findEmail(phoneNumber);
        call.enqueue(new Callback<List<UserAllVO>>() {
            @Override
            public void onResponse(Call<List<UserAllVO>> call, Response<List<UserAllVO>> response) {
                if(response.isSuccessful()){
                    if(response.body() != null){
                        List<UserAllVO> result = response.body();

                        if(result.size() == 0){
                            ((TextView)findViewById(R.id.email)).setText("아직 없습니다. 회원가입 해주시기 바랍니다.");
                        }else {
                            StringBuilder emailResult = new StringBuilder();
                            for(int i=0;i<result.size();i++){
                                emailResult.append(result.get(i).getEmail());
                                if(result.get(i).getUser_password().length() <= 0){
                                    emailResult.append(" [카카오/구글]");
                                }
                                emailResult.append("\n");
                            }
                            ((TextView)findViewById(R.id.email)).setText(emailResult.toString() + " 입니다.");
                        }
                    }else {
                        Log.d("서버에러", "response 내용없음");
                        Intent intent = new Intent(getApplication(), BaobabInspection.class);
                        intent.putExtra("status", "오류");
                        startActivity(intent);
                        finish();
                    }
                }else {
                    Log.d("서버에러", "서버로그 확인바람");
                    Intent intent = new Intent(getApplication(), BaobabInspection.class);
                    intent.putExtra("status", "오류");
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<List<UserAllVO>> call, Throwable t) {
                Log.d("통신에러", t.getLocalizedMessage());
                Intent intent = new Intent(getApplication(), BaobabInspection.class);
                intent.putExtra("status", "오류");
                startActivity(intent);
                finish();
            }
        });

        findViewById(R.id.checkFinish).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
