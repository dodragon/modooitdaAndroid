package com.baobab.user.baobabflyer;

import android.app.DatePickerDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baobab.user.baobabflyer.server.adapter.CpPayHistoryListAdapter;
import com.baobab.user.baobabflyer.server.singleTons.RetroSingleTon;
import com.baobab.user.baobabflyer.server.vo.PaymentVO;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BaobabCpPayHistory extends AppCompatActivity {

    RetroSingleTon retroSingleTon;

    int cpSeq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baobab_cp_pay_history);

        cpSeq = getIntent().getIntExtra("cpSeq", 0);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String date = format.format(new Date());
        ((TextView)findViewById(R.id.dateText)).setText(date);

        findViewById(R.id.dateLayout).setOnClickListener(datePickListener);

        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        settingList(date);
    }

    View.OnClickListener datePickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String[] todayStrArr = ((TextView)((LinearLayout)v).getChildAt(1)).getText().toString().split("-");
            datePicker(todayStrArr, R.id.dateText).show();
        }
    };

    public DatePickerDialog datePicker(String[] todayStrArr, final int id){
        DatePickerDialog dialog = new DatePickerDialog(BaobabCpPayHistory.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String date = year + "-" + makeMonthAndDay(month + 1) + "-" + makeMonthAndDay(dayOfMonth);
                        ((TextView)findViewById(id)).setText(date);
                        settingList(date);
                    }
                },
                Integer.parseInt(todayStrArr[0]),
                Integer.parseInt(todayStrArr[1]) - 1,
                Integer.parseInt(todayStrArr[2]));

        return dialog;
    }

    public String makeMonthAndDay(int date){
        if(date < 10){
            return "0" + date;
        }else {
            return String.valueOf(date);
        }
    }

    private void settingList(String date){
        Call<List<PaymentVO>> call = retroSingleTon.getInstance().getRetroInterface(getApplicationContext()).getPayment(cpSeq, "%" + date + "%");
        call.enqueue(new Callback<List<PaymentVO>>() {
            @Override
            public void onResponse(Call<List<PaymentVO>> call, Response<List<PaymentVO>> response) {
                if(response.isSuccessful()){
                    if(response.body() != null){
                        List<PaymentVO> list = response.body();
                        RecyclerView recyclerView = findViewById(R.id.recyclerView);
                        recyclerView.setHasFixedSize(true);
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(BaobabCpPayHistory.this);
                        recyclerView.setLayoutManager(layoutManager);

                        CpPayHistoryListAdapter adapter = new CpPayHistoryListAdapter(list, BaobabCpPayHistory.this);

                        recyclerView.removeAllViewsInLayout();
                        recyclerView.setAdapter(adapter);
                    }else {
                        Log.d("결제승인내역 리스트", "response 내용없음");
                        Toast.makeText(BaobabCpPayHistory.this, "다시 시도해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }else {
                    Log.d("결제승인내역 리스트", "서버로그 확인 필요");
                    Toast.makeText(BaobabCpPayHistory.this, "고객센터로 문의주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<List<PaymentVO>> call, Throwable t) {
                Log.d("결제승인내역 리스트", t.getLocalizedMessage());
                Toast.makeText(BaobabCpPayHistory.this, "고객센터로 문의주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
