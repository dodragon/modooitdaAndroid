package com.baobab.user.baobabflyer;

import android.app.DatePickerDialog;
import android.graphics.Color;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.baobab.user.baobabflyer.server.adapter.StatisticsListAdapter;
import com.baobab.user.baobabflyer.server.singleTons.RetroSingleTon;
import com.baobab.user.baobabflyer.server.util.YearAndMonthPickerDialog;
import com.baobab.user.baobabflyer.server.vo.StatChartDataVO;
import com.baobab.user.baobabflyer.server.vo.StatisticsVO;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BaobabCpStatistics extends AppCompatActivity {

    RetroSingleTon retroSingleTon;

    SimpleDateFormat simpleDateFormat;
    String statDiv = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baobab_cp_statistics);

        ((TextView)findViewById(R.id.date)).addTextChangedListener(watcher);
        findViewById(R.id.days).setOnClickListener(mainBtnListener);
        findViewById(R.id.months).setOnClickListener(mainBtnListener);

        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        findViewById(R.id.days).performClick();
    }

    View.OnClickListener mainBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ((Button) v).setTextColor(Color.parseColor("#5c7cfa"));
            String formatStr = "";
            switch (v.getId()) {
                case R.id.days:
                    statDiv = "일";
                    formatStr = "yyyy-MM-dd";
                    simpleDateFormat = new SimpleDateFormat(formatStr);
                    ((TextView)findViewById(R.id.date)).setText(simpleDateFormat.format(new Date()));
                    ((Button) findViewById(R.id.months)).setTextColor(Color.BLACK);
                    findViewById(R.id.unUse_line).setVisibility(View.VISIBLE);
                    findViewById(R.id.used_line).setVisibility(View.GONE);
                    findViewById(R.id.dateLayout).setOnClickListener(datePickerListener);
                    break;
                case R.id.months:
                    statDiv = "월";
                    formatStr = "yyyy-MM";
                    simpleDateFormat = new SimpleDateFormat(formatStr);
                    ((TextView)findViewById(R.id.date)).setText(simpleDateFormat.format(new Date()));
                    ((Button) findViewById(R.id.days)).setTextColor(Color.BLACK);
                    findViewById(R.id.unUse_line).setVisibility(View.GONE);
                    findViewById(R.id.used_line).setVisibility(View.VISIBLE);
                    findViewById(R.id.dateLayout).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String selectDate = ((TextView)findViewById(R.id.date)).getText().toString();
                            String[] yearAndMonthArr = selectDate.split("-");
                            YearAndMonthPickerDialog yearAndMonthPickerDialog = new YearAndMonthPickerDialog();
                            yearAndMonthPickerDialog.setListener(yearMonthListener, Integer.parseInt(yearAndMonthArr[0]), Integer.parseInt(yearAndMonthArr[1]), BaobabCpStatistics.this);
                            yearAndMonthPickerDialog.show(getSupportFragmentManager(), "year and month picker");
                        }
                    });
                    break;
            }

        }
    };

    TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String selectDate = ((TextView)findViewById(R.id.date)).getText().toString();
            getDataAndSetting(selectDate, statDiv);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private void getDataAndSetting(String selectDate, final String statDiv){
        int cpSeq = getIntent().getIntExtra("cpSeq", 0);
        Call<StatisticsVO> call = retroSingleTon.getInstance().getRetroInterface(getApplicationContext()).cpStat(cpSeq, selectDate, statDiv);
        call.enqueue(new Callback<StatisticsVO>() {
            @Override
            public void onResponse(Call<StatisticsVO> call, Response<StatisticsVO> response) {
                if(response.isSuccessful()){
                    if(response.body() != null){
                        StatisticsVO vo = response.body();

                        DecimalFormat format = new DecimalFormat("###,###");
                        ((TextView)findViewById(R.id.allSales)).setText("총 매출 : " + format.format(vo.getAllSales()) + "원");
                        ((TextView)findViewById(R.id.useSales)).setText("총 사용 : " + format.format(vo.getAllSales()) + "원");
                        ((TextView)findViewById(R.id.canSales)).setText("총 취소 : " + format.format(vo.getAllSales()) + "원");

                        String[] contentArr = new String[]{
                                "조회수",
                                "찜하기",
                                "결제승인",
                                "티켓스캔",
                                "결제취소",
                                "보낸푸시",
                                "푸시클릭"
                        };

                        List<List<StatChartDataVO>> chartList = new ArrayList<>();
                        if(statDiv.equals("월")){
                            chartList.add(vo.getLineChartHits());
                            chartList.add(vo.getLineChartPoke());
                            chartList.add(vo.getLineChartPaySuccess());
                            chartList.add(vo.getLineChartScan());
                            chartList.add(vo.getLineChartPayCancel());
                            chartList.add(vo.getLineChartPush());
                            chartList.add(vo.getLineChartPushClick());
                        }

                        int[] afterData = new int[]{
                                vo.getAHit(),
                                vo.getAPoke(),
                                vo.getAPay(),
                                vo.getAScan(),
                                vo.getACancel(),
                                vo.getAPush(),
                                vo.getAPushClick()
                        };

                        int[] beforeData = new int[]{
                                vo.getBHit(),
                                vo.getBPoke(),
                                vo.getBPay(),
                                vo.getBScan(),
                                vo.getBCancel(),
                                vo.getBPush(),
                                vo.getBPushClick()
                        };

                        RecyclerView recyclerView = findViewById(R.id.recyclerView);
                        recyclerView.setHasFixedSize(true);
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(BaobabCpStatistics.this);
                        recyclerView.setLayoutManager(layoutManager);

                        StatisticsListAdapter adapter = new StatisticsListAdapter(contentArr, afterData, beforeData, statDiv, chartList);

                        recyclerView.removeAllViewsInLayout();
                        recyclerView.setAdapter(adapter);
                    }else {
                        Log.d("업체 통계", "response 내용없음");
                        Toast.makeText(BaobabCpStatistics.this, "내용을 받아오지 못했습니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Log.d("업체 통계", "서버로그 확인 필요");
                    Toast.makeText(BaobabCpStatistics.this, "내용을 받아오지 못했습니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<StatisticsVO> call, Throwable t) {
                Log.d("업체 통계", t.getLocalizedMessage());
                Toast.makeText(BaobabCpStatistics.this, "내용을 받아오지 못했습니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    DatePickerDialog.OnDateSetListener yearMonthListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            ((TextView)findViewById(R.id.date)).setText(year + "-" + makeMonthAndDay(month));
        }
    };

    View.OnClickListener datePickerListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String[] dateArr = ((TextView)findViewById(R.id.date)).getText().toString().split("-");
            datePicker(dateArr, R.id.date).show();
        }
    };

    public DatePickerDialog datePicker(String[] todayStrArr, final int id){
        DatePickerDialog dialog = new DatePickerDialog(BaobabCpStatistics.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        ((TextView)findViewById(id)).setText(year + "-" + makeMonthAndDay(month + 1) + "-" + makeMonthAndDay(dayOfMonth));
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
}
