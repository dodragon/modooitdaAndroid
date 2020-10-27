package com.baobab.user.baobabflyer.firebase;

import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class FCMInstanceIdService extends FirebaseInstanceIdService  {
    private static final String TAG = FCMInstanceIdService.class.getSimpleName();

    // 토큰 재생성
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String token = FirebaseInstanceId.getInstance().getToken();
        SharedPreferences spf = getSharedPreferences("token", 0);
        SharedPreferences.Editor editor = spf.edit();
        editor.putString("pushToken", token);
        editor.apply();
        Log.d(TAG, "token = " + token);
    }
}