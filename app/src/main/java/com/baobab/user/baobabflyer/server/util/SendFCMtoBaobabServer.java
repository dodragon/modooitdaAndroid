package com.baobab.user.baobabflyer.server.util;

import android.content.Context;
import android.util.Log;

import com.baobab.user.baobabflyer.server.singleTons.RetroSingleTon;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SendFCMtoBaobabServer {
    RetroSingleTon retroSingleTon;
    Context context;

    public SendFCMtoBaobabServer(Context context) {
        this.context = context;
    }

    public void sendFCM(String title, String message, String div, String json){
        Call<Integer> call = retroSingleTon.getInstance().getRetroInterface(context.getApplicationContext()).sendMessage(title, message, div, json);
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if(response.isSuccessful()){
                    if(response.body() != null){
                        int result = response.body();
                        if(result > 0){
                            Log.d("푸시보내기", "성공");
                        }else {
                            Log.d("푸시보내기", "사용자를 찾을 수 없음");
                        }
                    }else {
                        Log.d("푸시보내기", "response 내용없음");
                    }
                }else {
                    Log.d("푸시보내기", "서버로그 확인필요");
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Log.d("푸시보내기", t.getLocalizedMessage());
            }
        });
    }
}
