package com.baobab.user.baobabflyer.server.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baobab.user.baobabflyer.BaobabBigQRcode;
import com.baobab.user.baobabflyer.BaobabOptionCert;
import com.baobab.user.baobabflyer.PayAndScanResult;
import com.baobab.user.baobabflyer.R;
import com.baobab.user.baobabflyer.server.adapter.PaidMenusAdapter;
import com.baobab.user.baobabflyer.server.adapter.RecommendListAdapter;
import com.baobab.user.baobabflyer.server.singleTons.RetroSingleTon;
import com.baobab.user.baobabflyer.server.vo.CPInfoVO;
import com.baobab.user.baobabflyer.server.vo.PayMenusVO;
import com.baobab.user.baobabflyer.server.vo.UserTicketVO;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserTicketDetailDialog extends DialogFragment {

    RetroSingleTon retroSingleTon;

    private static UserTicketVO ticketVO;
    private static Context context;
    private static DecimalFormat format;
    private static SimpleDateFormat dateFormat;

    View view;

    public static UserTicketDetailDialog newInstance(UserTicketVO ticket, Context thisContext) {
        Bundle bundle = new Bundle();
        bundle.putString("", "");

        ticketVO = ticket;
        context = thisContext;
        format = new DecimalFormat("###,###");
        dateFormat = new SimpleDateFormat("yyyy년 MM월 dd일 까지");

        UserTicketDetailDialog fragment = new UserTicketDetailDialog();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.FullscreenDialog);
        view = getActivity().getLayoutInflater().inflate(R.layout.ticket_detail_dialog, null);

        builder.setView(view);
        Dialog dialog = builder.create();
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialog.setCanceledOnTouchOutside(false);

        Call<List<PayMenusVO>> call = retroSingleTon.getInstance().getRetroInterface(context.getApplicationContext()).getPaidMenus(ticketVO.getOrderNum());
        call.enqueue(new Callback<List<PayMenusVO>>() {
            @Override
            public void onResponse(Call<List<PayMenusVO>> call, Response<List<PayMenusVO>> response) {
                if(response.isSuccessful()){
                    if(response.body() != null){
                        List<PayMenusVO> list = response.body();

                        int price = 0;
                        int disPrice = 0;

                        for(int i=0;i<list.size();i++){
                            price += list.get(i).getPrice();
                            disPrice += list.get(i).getDisPrice();
                        }

                        ((TextView)view.findViewById(R.id.disCount)).setText(makeDisCount(price, disPrice));
                        ((TextView)view.findViewById(R.id.paidCount)).setText(format.format(disPrice) + "원");

                        if(dateCompare(ticketVO.getPeriodDate())){
                            ((TextView)view.findViewById(R.id.period)).setText(dateFormat.format(ticketVO.getPeriodDate()));

                            CreateQR qr = new CreateQR();
                            final Bitmap qrCode = qr.createQR(ticketVO.getTicketSerial());
                            ((ImageView)view.findViewById(R.id.qr)).setImageBitmap(qrCode);
                            view.findViewById(R.id.qr).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(context, BaobabBigQRcode.class);
                                    intent.putExtra("qr", qrCode);
                                    startActivity(intent);
                                }
                            });

                            view.findViewById(R.id.scanBtn).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(context, Scanner.class);
                                    intent.putExtra("serial", ticketVO.getTicketSerial());
                                    startActivity(intent);
                                }
                            });
                        }else {
                            ((TextView)view.findViewById(R.id.period)).setText("기간이 만료되었습니다. 결제취소 해주시기 바랍니다.");
                            view.findViewById(R.id.qr).setVisibility(View.GONE);
                            view.findViewById(R.id.scanBtn).setVisibility(View.GONE);
                            view.findViewById(R.id.passwordLayout).setVisibility(View.GONE);
                        }

                        RecyclerView recyclerView = view.findViewById(R.id.paidMenuRecyclerView);
                        recyclerView.setHasFixedSize(true);
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
                        recyclerView.setLayoutManager(layoutManager);

                        PaidMenusAdapter adapter = new PaidMenusAdapter(list);

                        recyclerView.removeAllViewsInLayout();
                        recyclerView.setAdapter(adapter);

                        view.findViewById(R.id.payCancel).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AlertDialog alert = new AlertDialog.Builder(context)
                                        .setTitle("결제취소")
                                        .setMessage("결제를 취소하시겠습니까?")
                                        .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        })
                                        .setPositiveButton("네", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(final DialogInterface dialog, int which) {
                                                Call<String> call = retroSingleTon.getInstance().getRetroInterface(context.getApplicationContext()).payCancelCheck(ticketVO.getOrderNum());
                                                call.enqueue(new Callback<String>() {
                                                    @Override
                                                    public void onResponse(Call<String> call, Response<String> response) {
                                                        if(response.isSuccessful()){
                                                            if(response.body() != null){
                                                                String result = response.body();
                                                                if(result.equals("미사용")){Intent intent = new Intent(context, BaobabOptionCert.class);
                                                                    intent.putExtra("oid", ticketVO.getOrderNum());
                                                                    intent.putExtra("kind", "payCancel");
                                                                    startActivity(intent);
                                                                    dialog.dismiss();
                                                                }else {
                                                                    Toast.makeText(context, "이미 사용했거나 취소된 티켓입니다.", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }else {
                                                                Log.d("결제취소확인", "response 내용없음");
                                                                Toast.makeText(context, "다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }else {
                                                            Log.d("결제취소확인", "서버로그 확인 필요");
                                                            Toast.makeText(context, "다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }

                                                    @Override
                                                    public void onFailure(Call<String> call, Throwable t) {
                                                        Log.d("결제취소확인", t.getLocalizedMessage());
                                                        Toast.makeText(context, "다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        })
                                        .create();
                                alert.show();
                            }
                        });
                    }else {
                        Log.d("티켓 상세내역", "response 내용없음");
                        Toast.makeText(context, "티켓 조회중 오류 발생", Toast.LENGTH_SHORT).show();
                        dismissDialog();
                    }
                }else {
                    Log.d("티켓 상세내역", "서버로그 확인 필요");
                    Toast.makeText(context, "티켓 조회중 오류 발생", Toast.LENGTH_SHORT).show();
                    dismissDialog();
                }
            }

            @Override
            public void onFailure(Call<List<PayMenusVO>> call, Throwable t) {
                Log.d("티켓 상세내역", t.getLocalizedMessage());
                Toast.makeText(context, "티켓 조회중 오류 발생", Toast.LENGTH_SHORT).show();
                dismissDialog();
            }
        });

        view.findViewById(R.id.clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissDialog();
            }
        });

        settingRecommend();

        view.findViewById(R.id.passwordScanConfirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pw = ((EditText)view.findViewById(R.id.passwordScan)).getText().toString();
                passwordScan(pw, ticketVO.getCpSeq(), ticketVO.getTicketSerial());
            }
        });

        return dialog;
    }

    private void passwordScan(String pw, final int cpSeq, final String serial){
        Sha256Util sha = new Sha256Util();
        Call<Integer> call = retroSingleTon.getInstance().getRetroInterface(context.getApplicationContext()).cpPwCheck(sha.sha256(pw), cpSeq);
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if(response.isSuccessful()){
                    if(response.body() != null){
                        int result = response.body();
                        if(result == 0){
                            productQrScan(serial, "pw", "", cpSeq);
                        }else {
                            Log.d("pw스캔", "비밀번호 불일치");
                            Toast.makeText(context, "비밀번호가 일치하지 않습니다. 다시 입력해주세요.", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Log.d("pw스캔", "response 내용없음");
                        Toast.makeText(context, "비밀번호가 틀렸거나 사용할수 없습니다. 다른 스캔 방법을 이용해주세요.", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Log.d("pw스캔", "서버 로그 확인 필요");
                    Toast.makeText(context, "비밀번호가 틀렸거나 사용할수 없습니다. 다른 스캔 방법을 이용해주세요.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Log.d("pw스캔", t.getLocalizedMessage());
                Toast.makeText(context, "비밀번호가 틀렸거나 사용할수 없습니다. 다른 스캔 방법을 이용해주세요.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void productQrScan(final String serial, final String scanner, String cpEmail, int cpSeq){
        final Intent intent = new Intent(context, PayAndScanResult.class);
        Call<Integer> call = retroSingleTon.getInstance().getRetroInterface(context.getApplicationContext()).usedTicket(serial, scanner, cpEmail, cpSeq);
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if(response.isSuccessful()){
                    if(response.body() != null){
                        int result = response.body();
                        if(result == 0){
                            Toast.makeText(context.getApplicationContext(), "티켓사용이 정상적으로 처리되었습니다.", Toast.LENGTH_SHORT).show();
                            sendPush("티켓 사용완료", "티켓사용이 완료되었습니다.", "티켓", serial + "," + getLocalPhoneNumber());
                            intent.putExtra("title", "스캔이 완료되었습니다.");
                            intent.putExtra("status", "티켓사용이 완료되었습니다.");
                            intent.putExtra("scanner", scanner);
                            intent.putExtra("div", "scan(성공)");
                            startActivity(intent);
                            ((Activity)context).finish();
                        }else if(result == 10){
                            Toast.makeText(context.getApplicationContext(), "이미 사용된 티켓입니다.", Toast.LENGTH_SHORT).show();
                            sendPush("티켓 사용실패", "이미 사용되었거나 취소된 티켓을 스캔하셨습니다.", "티켓", serial + "," + getLocalPhoneNumber());
                            intent.putExtra("title", "스캔에 실패하였습니다.");
                            intent.putExtra("status", "이미 사용되었거나 취소된 티켓입니다.");
                            intent.putExtra("scanner", scanner);
                            intent.putExtra("div", "scan(실패)");
                            startActivity(intent);
                            ((Activity)context).finish();
                        }else if(result == 9){
                            Toast.makeText(context.getApplicationContext(), "일치하는 티켓 혹은 업체가 아닙니다.", Toast.LENGTH_SHORT).show();
                            sendPush("티켓 사용실패", "해당 업체 티켓이 아닙니다. 티켓을 확인하세요.", "티켓", serial + "," + getLocalPhoneNumber());
                            intent.putExtra("title", "스캔에 실패하였습니다.");
                            intent.putExtra("status", "일치하는 티켓 혹은 업체가 아닙니다.");
                            intent.putExtra("scanner", scanner);
                            intent.putExtra("div", "scan(실패)");
                            startActivity(intent);
                            ((Activity)context).finish();
                        }else {
                            Toast.makeText(context.getApplicationContext(), "스캔을 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                            sendPush("티켓 사용 중 오류", "티켓 사용중 문제가 발생했습니다. 다시 시도해주세요.", "티켓", serial + "," + getLocalPhoneNumber());
                            intent.putExtra("title", "스캔에 실패했습니다.");
                            intent.putExtra("status", "티켓 사용중 문제가 발생했습니다. 다시 시도해주세요.");
                            intent.putExtra("scanner", scanner);
                            intent.putExtra("div", "scan(오류)");
                            startActivity(intent);
                            ((Activity)context).finish();
                        }
                    }else {
                        Log.d("티켓사용", "response 내용없음");
                        Toast.makeText(context.getApplicationContext(), "스캔을 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                        sendPush("티켓 사용 중 오류", "티켓 사용중 문제가 발생했습니다. 다시 시도해주세요.", "티켓", serial + "," + getLocalPhoneNumber());
                        intent.putExtra("title", "스캔에 실패했습니다.");
                        intent.putExtra("status", "티켓 사용중 문제가 발생했습니다. 다시 시도해주세요.");
                        intent.putExtra("scanner", scanner);
                        intent.putExtra("div", "scan(오류)");
                        startActivity(intent);
                        ((Activity)context).finish();
                    }
                }else {
                    Log.d("티켓사용", "서버로그 확인 필요");
                    Toast.makeText(context.getApplicationContext(), "스캔을 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                    sendPush("티켓 사용 중 오류", "티켓 사용중 문제가 발생했습니다. 다시 시도해주세요.", "티켓", serial + "," + getLocalPhoneNumber());
                    intent.putExtra("title", "스캔에 실패했습니다.");
                    intent.putExtra("status", "티켓 사용중 문제가 발생했습니다. 다시 시도해주세요.");
                    intent.putExtra("scanner", scanner);
                    intent.putExtra("div", "scan(오류)");
                    startActivity(intent);
                    ((Activity)context).finish();
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Log.d("티켓사용", t.getLocalizedMessage());
                Toast.makeText(context.getApplicationContext(), "스캔을 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                sendPush("티켓 사용 중 오류", "티켓 사용중 문제가 발생했습니다. 다시 시도해주세요.", "티켓", serial + "," + getLocalPhoneNumber());
                intent.putExtra("title", "스캔에 실패했습니다.");
                intent.putExtra("status", "티켓 사용중 문제가 발생했습니다. 다시 시도해주세요.");
                intent.putExtra("div", "scan(오류)");
                startActivity(intent);
                ((Activity)context).finish();
            }
        });
    }

    public String makeDisCount(int price, int disPrice){
        return "-" + (format.format(price - disPrice)) + "원";
    }

    public void settingRecommend(){
        Call<List<CPInfoVO>> call = retroSingleTon.getInstance().getRetroInterface(context.getApplicationContext()).recommendCP(ticketVO.getCpSeq());
        call.enqueue(new Callback<List<CPInfoVO>>() {
            @Override
            public void onResponse(Call<List<CPInfoVO>> call, Response<List<CPInfoVO>> response) {
                if(response.isSuccessful()){
                    if(response.body() != null){
                        List<CPInfoVO> list = response.body();

                        List<CPInfoVO> newList = new ArrayList<>();
                        CPInfoVO vo = new CPInfoVO();
                        for(int i=0;i<list.size();i++){
                            if(list.get(i).getSeq_num() == ticketVO.getCpSeq()){
                                vo = list.get(i);
                            }else {
                                newList.add(list.get(i));
                            }
                        }

                        SharedPreferences spf = context.getSharedPreferences("user", 0);
                        double userLatitude = Double.parseDouble(spf.getString("latitude", "0"));
                        double userLongitude = Double.parseDouble(spf.getString("longitude", "0"));
                        String userLocation = spf.getString("location", "");

                        RecyclerView recyclerView = view.findViewById(R.id.recommendRecyclerView);
                        recyclerView.setHasFixedSize(true);
                        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                        recyclerView.setLayoutManager(layoutManager);

                        RecommendListAdapter adapter = new RecommendListAdapter(newList, context, userLatitude, userLongitude, userLocation, vo);

                        recyclerView.removeAllViewsInLayout();
                        recyclerView.setAdapter(adapter);
                    }else {
                        Log.d("2차 추천", "response 내용없음");
                    }
                }else {
                    Log.d("2차 추천", "서버로그 확인 필요");
                }
            }

            @Override
            public void onFailure(Call<List<CPInfoVO>> call, Throwable t) {
                Log.d("2차 추천", t.getLocalizedMessage());
            }
        });
    }

    private void dismissDialog() {
        this.dismiss();
    }

    private boolean dateCompare(Date period){
        Date today = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");

        int periodInt = Integer.parseInt(simpleDateFormat.format(period));
        int todayInt = Integer.parseInt(simpleDateFormat.format(today));

        if(todayInt > periodInt){
            return false;
        }else {
            return true;
        }
    }

    private void sendPush(String title, String message, String div, String data){
        Call<Integer> call = retroSingleTon.getInstance().getRetroInterface(context.getApplicationContext()).sendMessage(title, message, div, data);
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if(response.isSuccessful()){
                    if(response.body() != null){
                        int result = response.body();
                        if(result > 0){
                            Log.d("스캔 푸시", "전달 성공");
                        }else {
                            Log.d("스캔 푸시", "전달 실패 : token 불일치");
                        }
                    }else {
                        Log.d("스캔 푸시", "response 내용없음");
                    }
                }else {
                    Log.d("스캔 푸시", "서버로그 확인 필요");
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Log.d("스캔 푸시", t.getLocalizedMessage());
            }
        });
    }

    @SuppressLint("MissingPermission")
    public String getLocalPhoneNumber() {
        TelephonyManager telephony = (TelephonyManager) context.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        String phoneNumber = "";
        try {
            if (telephony.getLine1Number() != null) {
                phoneNumber = telephony.getLine1Number();
            } else {
                if (telephony.getSimSerialNumber() != null) {
                    phoneNumber = telephony.getSimSerialNumber();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return phoneNumber;
    }
}
