package com.baobab.user.baobabflyer.server.util;

import android.content.Context;
import android.util.Log;

import com.baobab.user.baobabflyer.server.singleTons.RetroSingleTon;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Point {

    RetroSingleTon retroSingleTon;

    public void pointUpdate(String email, final Context context){
        Call<Integer> call = retroSingleTon.getInstance().getRetroInterface(context).pointUp(email);
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if(response.isSuccessful()){
                    if(response.body() != null){
                        int result = response.body();
                        if(result > 0){
                            //Toast.makeText(context, "포인트가 적립되었습니다(+10원)", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Log.d("포인트 : ", "response 응답없음");
                    }
                }else {
                    Log.d("포인트 : ", "서버에러 : 서버 로그 확인 필요");
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Log.d("포인트 : ", "통신에러" + t.getLocalizedMessage());
            }
        });
    }
}
