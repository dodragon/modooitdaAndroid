package com.baobab.user.baobabflyer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.baobab.user.baobabflyer.server.util.LoadDialog;
import com.baobab.user.baobabflyer.server.singleTons.RetroSingleTon;
import com.baobab.user.baobabflyer.server.vo.CPUserVO;
import com.baobab.user.baobabflyer.server.vo.UserLocationVO;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BaobabPush extends AppCompatActivity {
    RetroSingleTon retroSingleTon;

    String cpName;
    int cpSeq;

    List<UserLocationVO> fanList;
    List<UserLocationVO> normalList;

    String selectTitle = "";
    CPUserVO cpUserVO;

    int fanEaRs = 0;

    public static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baobab_push);

        context = this;
        cpName = getIntent().getStringExtra("cpName");
        cpSeq = getIntent().getIntExtra("cpSeq", 0);

        findViewById(R.id.pushPay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BaobabPush.this, BaobabPushPay.class);
                intent.putExtra("cpName", cpName);
                intent.putExtra("cpSeq", cpSeq);
                startActivity(intent);
            }
        });

        String email = getSharedPreferences("user", 0).getString("email", "");

        if (!email.equals("위대한올마이티")) {
            Call<CPUserVO> callInfo = retroSingleTon.getInstance().getRetroInterface(getApplicationContext()).cpUserAllInfo(email);
            callInfo.enqueue(new Callback<CPUserVO>() {
                @Override
                public void onResponse(Call<CPUserVO> call, Response<CPUserVO> response) {
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            cpUserVO = response.body();
                            ((TextView) findViewById(R.id.voucher)).setText(cpUserVO.getPush_ea() + "개");
                        } else {
                            Log.d("서버에러(푸시 사장님정보)", "response 내용없음");
                            Intent intent = new Intent(getApplication(), BaobabInspection.class);
                            intent.putExtra("status", "오류");
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        Log.d("서버에러(푸시 사장님정보)", "서버측 오류발생");
                        Intent intent = new Intent(getApplication(), BaobabInspection.class);
                        intent.putExtra("status", "오류");
                        startActivity(intent);
                        finish();
                    }
                }

                @Override
                public void onFailure(Call<CPUserVO> call, Throwable t) {
                    Log.d("통신에러(푸시 사장님정보)", t.getLocalizedMessage());
                    Intent intent = new Intent(getApplication(), BaobabInspection.class);
                    intent.putExtra("status", "오류");
                    startActivity(intent);
                    finish();
                }
            });
        }

        Call<List<UserLocationVO>> callFan = retroSingleTon.getInstance().getRetroInterface(getApplicationContext()).pushFan(cpSeq);
        callFan.enqueue(new Callback<List<UserLocationVO>>() {
            @Override
            public void onResponse(Call<List<UserLocationVO>> call, Response<List<UserLocationVO>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Log.d("통신 완료(푸시 팬리스트)", "완료");
                        fanList = response.body();
                        TextView fanText = findViewById(R.id.fanText);
                        fanText.setText(String.valueOf(fanList.size()));
                        LinearLayout mother = findViewById(R.id.fanMother);
                        makeCustomer(mother, fanList);
                    } else {
                        Log.d("서버에러(푸시 팬리스트)", "response 내용없음");
                        Intent intent = new Intent(getApplication(), BaobabInspection.class);
                        intent.putExtra("status", "오류");
                        startActivity(intent);
                        finish();
                    }
                } else {
                    Log.d("서버에러(푸시 팬리스트)", "서버측 오류발생");
                    Intent intent = new Intent(getApplication(), BaobabInspection.class);
                    intent.putExtra("status", "오류");
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<List<UserLocationVO>> call, Throwable t) {
                Log.d("통신에러(푸시 팬리스트)", t.getLocalizedMessage());
                Intent intent = new Intent(getApplication(), BaobabInspection.class);
                intent.putExtra("status", "오류");
                startActivity(intent);
                finish();
            }
        });

        Spinner spinner = findViewById(R.id.pushSpinner);
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(this, R.layout.spinner_cescum_layout, getResources().getTextArray(R.array.km));
        spinner.setOnItemSelectedListener(kmListener);
        spinner.setAdapter(adapter);

        Spinner titleSpinner = findViewById(R.id.titleSpinner);
        ArrayAdapter<CharSequence> titleAdapter = new ArrayAdapter<>(this, R.layout.spinner_cescum_layout, getResources().getTextArray(R.array.pushTitle));
        titleSpinner.setAdapter(titleAdapter);
        titleSpinner.setOnItemSelectedListener(titleLister);

        ToggleButton toggleButton = findViewById(R.id.pushToggle);
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                LinearLayout customerLayout = findViewById(R.id.customerLayout);
                if (isChecked) {
                    customerLayout.setVisibility(View.VISIBLE);
                } else {
                    customerLayout.setVisibility(View.GONE);
                }
            }
        });

        findViewById(R.id.send).setOnClickListener(send);

        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    AdapterView.OnItemSelectedListener kmListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            final LoadDialog loading = new LoadDialog(BaobabPush.this);
            loading.showDialog();
            int radius = Integer.parseInt(parent.getSelectedItem().toString());
            Call<List<UserLocationVO>> callFan = retroSingleTon.getInstance().getRetroInterface(getApplicationContext()).pushNormal(cpSeq, radius);
            callFan.enqueue(new Callback<List<UserLocationVO>>() {
                @Override
                public void onResponse(Call<List<UserLocationVO>> call, Response<List<UserLocationVO>> response) {
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            Log.d("통신 완료(푸시 일반리스트)", "완료");
                            normalList = dupDelete(response.body());
                            locationLog(getSharedPreferences("user", 0), normalList);
                            TextView normalText = findViewById(R.id.normalText);
                            normalText.setText(String.valueOf(normalList.size()));
                            LinearLayout mother = findViewById(R.id.normalMother);
                            makeCustomer(mother, normalList);
                            loading.dialogCancel();
                        } else {
                            Log.d("서버에러(푸시 일반리스트)", "response 내용없음");
                            Intent intent = new Intent(getApplication(), BaobabInspection.class);
                            intent.putExtra("status", "오류");
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        Log.d("서버에러(푸시 일반리스트)", "서버측 오류발생");
                        Intent intent = new Intent(getApplication(), BaobabInspection.class);
                        intent.putExtra("status", "오류");
                        startActivity(intent);
                        finish();
                    }
                }

                @Override
                public void onFailure(Call<List<UserLocationVO>> call, Throwable t) {
                    Log.d("통신에러(푸시 일반리스트)", t.getLocalizedMessage());
                    Intent intent = new Intent(getApplication(), BaobabInspection.class);
                    intent.putExtra("status", "오류");
                    startActivity(intent);
                    finish();
                }
            });
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    AdapterView.OnItemSelectedListener titleLister = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (position != 0) {
                selectTitle = parent.getSelectedItem().toString();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    public void makeCustomer(LinearLayout mother, List<UserLocationVO> list) {
        mother.removeAllViews();
        for (int i = 0; i < list.size(); i++) {
            if(list.get(i) != null){
                TextView textView = new TextView(BaobabPush.this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                textView.setLayoutParams(layoutParams);
                textView.setTextColor(Color.BLACK);
                textView.setTypeface(Typeface.DEFAULT_BOLD);
                textView.setText(list.get(i).getUser());
                mother.addView(textView);
            }
        }
    }

    public List<UserLocationVO> dupDelete(List<UserLocationVO> list) {
        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j < fanList.size(); j++) {
                if (list.get(i).getUser().equals(fanList.get(j).getUser())) {
                    list.remove(i);
                }
            }
        }
        return list;
    }


    View.OnClickListener send = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            Spinner kmSpinner = findViewById(R.id.pushSpinner);
            EditText title = findViewById(R.id.realTitle);
            EditText content = findViewById(R.id.pushContent);

            final String email = getSharedPreferences("user", 0).getString("email", "");

            EditText fanSendEt = findViewById(R.id.fanSendText);
            EditText normalSendEt = findViewById(R.id.normalSendText);

            int fanEa = 0;
            if(fanSendEt.getText().toString().length() != 0){
                fanEa = Integer.parseInt(fanSendEt.getText().toString());
            }
            int normalEa = 0;
            if(normalSendEt.getText().toString().length() != 0){
                normalEa = Integer.parseInt(normalSendEt.getText().toString());
            }

            if (email.equals("위대한올마이티")) {
                sendPush(v, fanEa, normalEa, normalSendEt, title, content, kmSpinner, email);
            } else {
                if (cpUserVO.getPush_ea() >= 0 && cpUserVO.getPush_ea() >= fanEa + normalEa) {
                    sendPush(v, fanEa, normalEa, normalSendEt, title, content, kmSpinner, email);
                } else {
                    Toast.makeText(getApplicationContext(), "푸시 이용권 구매 후 이용 바랍니다.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    private void sendPush(final View v, int fanEa, final int normalEa, EditText normalSendEt, EditText title, EditText content, Spinner kmSpinner, final String email){
        v.setEnabled(false);

        if (fanEa + normalEa == 0) {
            Toast.makeText(BaobabPush.this, "수량을 정확히 입력하세요.", Toast.LENGTH_SHORT).show();
            normalSendEt.requestFocus();
            v.setEnabled(true);
        } else if (title.getText().toString().length() == 0) {
            Toast.makeText(BaobabPush.this, "제목을 입력하세요.", Toast.LENGTH_SHORT).show();
            title.requestFocus();
            v.setEnabled(true);
        } else if (content.getText().toString().length() == 0) {
            Toast.makeText(BaobabPush.this, "내용을 입력하세요.", Toast.LENGTH_SHORT).show();
            content.requestFocus();
            v.setEnabled(true);
        } else {
            String realTitle = "[광고" + selectTitle + "] " + title.getText().toString();
            String pushContent = "[" + cpName + "] " + content.getText().toString();
            int radius = Integer.parseInt(kmSpinner.getSelectedItem().toString());

            Call<Integer> call = retroSingleTon.getInstance().getRetroInterface(getApplicationContext()).sendPush(cpName, radius, fanEa, normalEa, realTitle, pushContent, cpSeq, email);
            call.enqueue(new Callback<Integer>() {
                @Override
                public void onResponse(Call<Integer> call, Response<Integer> response) {
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            int result = response.body();
                            if(result != 0){
                                Log.d("푸시전송 완료", "결과 : " + result);
                                Toast.makeText(BaobabPush.this, "총 " + result + "개의 푸시가 정상적으로 발송되었습니다.", Toast.LENGTH_SHORT).show();

                                Call<ResponseBody> pushMinus = retroSingleTon.getInstance().getRetroInterface(getApplicationContext()).pushEaPlma(email, normalEa);
                                pushMinus.enqueue(new Callback<ResponseBody>() {
                                    @Override
                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                        onBackPressed();
                                        finish();
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                                        Log.d("푸시감소 실패", "통신에러 : " + t.getLocalizedMessage());
                                        v.setEnabled(true);
                                        Intent intent = new Intent(getApplication(), BaobabInspection.class);
                                        intent.putExtra("status", "오류");
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                            }else{
                                Toast.makeText(BaobabPush.this, "푸시가 정상적으로 전달되지 않았습니다. 다시 시도해 주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.d("푸시전송 실패", "결과값 전송 안됨(response 내용없음)");
                            v.setEnabled(true);
                            Intent intent = new Intent(getApplication(), BaobabInspection.class);
                            intent.putExtra("status", "오류");
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        Log.d("푸시전송 실패", "서버에러 (로그확인필요)");
                        v.setEnabled(true);
                        Intent intent = new Intent(getApplication(), BaobabInspection.class);
                        intent.putExtra("status", "오류");
                        startActivity(intent);
                        finish();
                    }
                }

                @Override
                public void onFailure(Call<Integer> call, Throwable t) {
                    Log.d("푸시전송 실패", "통신에러 : " + t.getLocalizedMessage());
                    v.setEnabled(true);
                    Intent intent = new Intent(getApplication(), BaobabInspection.class);
                    intent.putExtra("status", "오류");
                    startActivity(intent);
                    finish();
                }
            });
        }
    }

    public void locationLog(SharedPreferences spf, List<UserLocationVO> normalList) {
        String user = spf.getString("email", "");
        if (user.equals("")) {
            user = getLocalPhoneNumber();
        }

        for (int i = 0; i < normalList.size(); i++) {
            Call<Integer> logCall = retroSingleTon.getInstance().getRetroInterface(getApplicationContext()).loclog(normalList.get(i).getUser(), "Android", "주변이용자 검색(푸시 보내기)", user);
            logCall.enqueue(new Callback<Integer>() {
                @Override
                public void onResponse(Call<Integer> call, Response<Integer> response) {
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            Log.d("통신완료", "위치정보 로그 통신 완료");
                            int result = response.body();
                            if (result > 0) {
                                Log.d("위치정보 로그", "저장 완료");
                            } else {
                                Log.d("위치정보 로그", "저장 중 오류 발생 로그 확인 필");
                            }
                        } else {
                            Log.d("통신실패", "response 내용없음");
                            Log.d("위치정보 로그", "저장 중 오류 발생 로그 확인 필");
                        }
                    } else {
                        Log.d("통신실패", "서버로그 확인 필");
                        Log.d("위치정보 로그", "저장 중 오류 발생 로그 확인 필");
                    }
                }

                @Override
                public void onFailure(Call<Integer> call, Throwable t) {
                    Log.d("통신실패", t.getLocalizedMessage());
                    Log.d("위치정보 로그", "저장 중 오류 발생 로그 확인 필");
                }
            });
        }
    }

    @SuppressLint("MissingPermission")
    public String getLocalPhoneNumber() {
        TelephonyManager telephony = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
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
