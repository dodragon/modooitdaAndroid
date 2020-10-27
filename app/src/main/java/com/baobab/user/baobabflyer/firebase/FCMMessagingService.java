package com.baobab.user.baobabflyer.firebase;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import android.util.Log;

import com.baobab.user.baobabflyer.BaobabAnterMain;
import com.baobab.user.baobabflyer.BaobabCpPayHistoryDetail;
import com.baobab.user.baobabflyer.BaobabInspection;
import com.baobab.user.baobabflyer.BaobabMenu;
import com.baobab.user.baobabflyer.BaobabNoticeDetail;
import com.baobab.user.baobabflyer.BaobabUserTicketList;
import com.baobab.user.baobabflyer.R;
import com.baobab.user.baobabflyer.server.util.AES256Util;
import com.baobab.user.baobabflyer.server.vo.CPInfoVO;
import com.baobab.user.baobabflyer.server.vo.CPmainImgVO;
import com.baobab.user.baobabflyer.server.vo.NoticeVO;
import com.baobab.user.baobabflyer.server.singleTons.RetroSingleTon;
import com.baobab.user.baobabflyer.server.vo.UserLocationVO;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.apache.commons.codec.DecoderException;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FCMMessagingService extends FirebaseMessagingService {
    private static final String TAG = FCMMessagingService.class.getSimpleName();

    RetroSingleTon retroSingleTon;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.i(TAG, "onMessageReceived");

        scheduleJob();

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, TAG);
        wakeLock.acquire(3000);

        SharedPreferences spf = getSharedPreferences("token", 0);
        int beforeId = spf.getInt("pushId", 0);

        String title = remoteMessage.getData().get("title");
        String message = remoteMessage.getData().get("body");
        int id = notificationId();
        while (beforeId == id) {
            id++;
        }
        notificationIntent(title, message, id);
    }

    private void scheduleJob() {
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
        Job myJob = dispatcher.newJobBuilder()
                .setService(JobService.class)
                .setTag("my-job-tag")
                .build();
        dispatcher.schedule(myJob);
    }

    private void sendNotification(String title, String message, Intent intent, int id) {
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        SharedPreferences spf = getSharedPreferences("token", 0);
        SharedPreferences.Editor editor = spf.edit();
        editor.putInt("pushId", id);
        editor.apply();

        Uri alarmSound = Uri.parse("android.resource://" + this.getPackageName() + "/" + R.raw.baobab_alarm);


        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel("baobab", "바오밥", NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("바오밥푸시");
            notificationChannel.setShowBadge(false);
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            notificationChannel.setSound(alarmSound, audioAttributes);
            notificationChannel.setVibrationPattern(new long[]{0, 300, 100, 300});
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.rgb(255, 112, 18));
            notificationManager.createNotificationChannel(notificationChannel);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "baobab")
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logo_and))
                    .setSmallIcon(R.drawable.logo_and)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setSound(alarmSound, AudioManager.STREAM_NOTIFICATION)
                    .setStyle(new NotificationCompat.BigTextStyle())
                    .setContentIntent(pendingIntent);

            notificationManager.notify(id, notificationBuilder.build());
        } else {
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "baobab")
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logo_and))
                    .setSmallIcon(R.drawable.logo_and)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setSound(alarmSound, AudioManager.STREAM_NOTIFICATION)
                    .setVibrate(new long[]{0, 300, 100, 300})
                    .setLights(Color.rgb(255, 112, 18), 1000, 500)
                    .setStyle(new NotificationCompat.BigTextStyle())
                    .setContentIntent(pendingIntent);

            notificationManager.notify(id, notificationBuilder.build());
        }
    }

    private int notificationId() {
        Random random = new Random();
        return random.nextInt();
    }

    private void notificationIntent(final String title, final String message, final int id) {
        if(title.startsWith("[광고")){
            final Intent intent = new Intent(this, BaobabMenu.class);
            SharedPreferences spf = getSharedPreferences("token", 0);
            final Bundle bundle = new Bundle();

            Call<UserLocationVO> call = retroSingleTon.getInstance().getRetroInterface(getApplicationContext()).findUserLocation(spf.getString("pushToken", ""));
            call.enqueue(new Callback<UserLocationVO>() {
                @Override
                public void onResponse(Call<UserLocationVO> call, Response<UserLocationVO> response) {
                    if(response.isSuccessful()){
                        if(response.body() != null){
                            Log.d("통신완료", "완료");
                            final UserLocationVO userVO = response.body();

                            Call<CPInfoVO> call2 = retroSingleTon.getInstance().getRetroInterface(getApplicationContext()).findCP(getCpName(message), title, message);
                            call2.enqueue(new Callback<CPInfoVO>() {
                                @Override
                                public void onResponse(Call<CPInfoVO> call, Response<CPInfoVO> response) {
                                    if(response.isSuccessful()){
                                        if(response.body() != null){
                                            Log.d("통신완료", "완료");
                                            final CPInfoVO cpVO = response.body();
                                            final ArrayList<String> imgUrl = new ArrayList<>();
                                            Call<List<CPmainImgVO>> call3 = retroSingleTon.getInstance().getRetroInterface(getApplicationContext()).getMainImg(cpVO.getSeq_num());
                                            call3.enqueue(new Callback<List<CPmainImgVO>>() {
                                                @Override
                                                public void onResponse(Call<List<CPmainImgVO>> call, Response<List<CPmainImgVO>> response) {
                                                    if(response.isSuccessful()){
                                                        if(response.body() != null){
                                                            Log.d("통신완료", "완료");

                                                            List<CPmainImgVO> imgList = response.body();
                                                            for(int i=0;i<imgList.size();i++){
                                                                imgUrl.add(imgList.get(i).getImg_url());
                                                            }
                                                            AES256Util aes = new AES256Util();
                                                            try {
                                                                bundle.putDouble("longitude", Double.parseDouble(aes.decrypt(userVO.getLongitude())));
                                                                bundle.putDouble("latitude", Double.parseDouble(aes.decrypt(userVO.getLatitude())));
                                                                bundle.putString("userLoc", aes.decrypt(userVO.getAddr()));
                                                                bundle.putString("context", "push");
                                                                bundle.putSerializable("info", cpVO);
                                                                bundle.putStringArrayList("imgUrl", imgUrl);
                                                                intent.putExtras(bundle);
                                                                sendNotification(title, message, intent, id);
                                                            } catch (NoSuchPaddingException e) {
                                                                e.printStackTrace();
                                                            } catch (NoSuchAlgorithmException e) {
                                                                e.printStackTrace();
                                                            } catch (InvalidKeyException e) {
                                                                e.printStackTrace();
                                                            } catch (DecoderException e) {
                                                                e.printStackTrace();
                                                            } catch (BadPaddingException e) {
                                                                e.printStackTrace();
                                                            } catch (IllegalBlockSizeException e) {
                                                                e.printStackTrace();
                                                            } catch (UnsupportedEncodingException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }else {
                                                            Log.d("서버에러(푸시 getCPimg)", "response 내용없음");
                                                            Intent intent = new Intent(getApplication(), BaobabInspection.class);
                                                            intent.putExtra("status", "오류");
                                                            startActivity(intent);
                                                        }
                                                    }else {
                                                        Log.d("서버에러(푸시 getCPimg)", "서버로그 확인 필요");
                                                        Intent intent = new Intent(getApplication(), BaobabInspection.class);
                                                        intent.putExtra("status", "오류");
                                                        startActivity(intent);
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<List<CPmainImgVO>> call, Throwable t) {
                                                    Log.d("통신실패(푸시 getCPimg) : ", t.getLocalizedMessage());
                                                    Intent intent = new Intent(getApplication(), BaobabInspection.class);
                                                    intent.putExtra("status", "오류");
                                                    startActivity(intent);
                                                }
                                            });
                                        }else {
                                            Log.d("서버에러(푸시 getCP)", "response 내용없음");
                                            Intent intent = new Intent(getApplication(), BaobabInspection.class);
                                            intent.putExtra("status", "오류");
                                            startActivity(intent);
                                        }
                                    }else {
                                        Log.d("서버에러(푸시 getCP)", "서버로그 확인 필요");
                                        Intent intent = new Intent(getApplication(), BaobabInspection.class);
                                        intent.putExtra("status", "오류");
                                        startActivity(intent);
                                    }
                                }

                                @Override
                                public void onFailure(Call<CPInfoVO> call, Throwable t) {
                                    Log.d("통신실패(푸시 getCP) : ", t.getLocalizedMessage());
                                    Intent intent = new Intent(getApplication(), BaobabInspection.class);
                                    intent.putExtra("status", "오류");
                                    startActivity(intent);
                                }
                            });
                        }else {
                            Log.d("서버에러(푸시 getUser)", "response 내용없음");
                            Intent intent = new Intent(getApplication(), BaobabInspection.class);
                            intent.putExtra("status", "오류");
                            startActivity(intent);
                        }
                    }else {
                        Log.d("서버에러(푸시 getUser)", "서버로그 확인 필요");
                        Intent intent = new Intent(getApplication(), BaobabInspection.class);
                        intent.putExtra("status", "오류");
                        startActivity(intent);
                    }
                }

                @Override
                public void onFailure(Call<UserLocationVO> call, Throwable t) {
                    Log.d("통신실패(푸시 getUser) : ", t.getLocalizedMessage());
                    Intent intent = new Intent(getApplication(), BaobabInspection.class);
                    intent.putExtra("status", "오류");
                    startActivity(intent);
                }
            });
        }else if(title.startsWith("[공지사항")){
            final Intent intent = new Intent(this, BaobabNoticeDetail.class);
            final Bundle bundle = new Bundle();

            Call<NoticeVO> call = retroSingleTon.getInstance().getRetroInterface(getApplicationContext()).noticePush(title);
            call.enqueue(new Callback<NoticeVO>() {
                @Override
                public void onResponse(Call<NoticeVO> call, Response<NoticeVO> response) {
                    if(response.isSuccessful()){
                        if(response != null){
                            Log.d("공지푸시 : ", "완료");
                            NoticeVO vo = response.body();

                            bundle.putSerializable("notiVO", vo);
                            bundle.putString("context", "push");
                            intent.putExtras(bundle);
                            sendNotification(title, message, intent, id);
                        }else {
                            Log.d("공지푸시 :", "response 내용없음");
                            Intent intent = new Intent(getApplication(), BaobabInspection.class);
                            intent.putExtra("status", "오류");
                            startActivity(intent);
                        }
                    }else {
                        Log.d("공지푸시 : ", "서버에러");
                        Intent intent = new Intent(getApplication(), BaobabInspection.class);
                        intent.putExtra("status", "오류");
                        startActivity(intent);
                    }
                }

                @Override
                public void onFailure(Call<NoticeVO> call, Throwable t) {
                    Log.d("공지푸시 : ", "통신에러" + t.getLocalizedMessage());
                    Intent intent = new Intent(getApplication(), BaobabInspection.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("status", "오류");
                    startActivity(intent);
                }
            });
        }else if(title.equals("결제완료")){
            //고객
            Intent intent = new Intent(this, BaobabUserTicketList.class);
            sendNotification(title, message, intent, id);
        }else if(title.equals("결제승인") | title.equals("결제취소승인")){
            //사장님
            Bundle bundle = new Bundle();
            Intent intent = new Intent(this, BaobabCpPayHistoryDetail.class);

            bundle.putString("orderNum", message.split(" : ")[1]);
            intent.putExtras(bundle);
            sendNotification(title, message, intent, id);
        }else if(title.equals("티켓 사용완료")){
            Intent intent;

            SharedPreferences spf = getSharedPreferences("user", 0);
            String email = spf.getString("email", "");

            if(email.equals("")){
                intent = new Intent(this, BaobabAnterMain.class);
                sendNotification(title, message, intent, id);
            }else {
                intent = new Intent(this, BaobabUserTicketList.class);
                Bundle bundle = new Bundle();
                bundle.putInt("div", 858);
                sendNotification(title, message, intent, id);
            }
        }else if(title.equals("티켓 사용실패") | title.equals("티켓 사용 중 오류")){
            Intent intent;

            SharedPreferences spf = getSharedPreferences("user", 0);
            String email = spf.getString("email", "");

            if(email.equals("")){
                intent = new Intent(this, BaobabAnterMain.class);
            }else {
                intent = new Intent(this, BaobabUserTicketList.class);
            }
            sendNotification(title, message, intent, id);
        }else if(title.equals("결제취소")){
            Intent intent = new Intent(this, BaobabUserTicketList.class);
            sendNotification(title, message, intent, id);
        }else {
            Intent intent = new Intent(this, BaobabAnterMain.class);
            sendNotification(title, message, intent, id);
        }
    }

    //푸쉬 종류에 따라서 위 메서드 정의해야함

    public String getCpName(String msg){
        return msg.substring(msg.indexOf("[") + 1, msg.indexOf("]"));
    }
}