package com.baobab.user.baobabflyer;

import android.app.DatePickerDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.baobab.user.baobabflyer.server.adapter.EventStatListAdapter;
import com.baobab.user.baobabflyer.server.singleTons.RetroSingleTon;
import com.baobab.user.baobabflyer.server.vo.EventStatListVO;
import com.baobab.user.baobabflyer.server.vo.SalesHistoryVO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BaobabEventStatistics extends AppCompatActivity {

    RetroSingleTon retroSingleTon;

    int cpSeq;
    SimpleDateFormat dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baobab_event_statistics);

        cpSeq = getIntent().getIntExtra("cpSeq", 0);
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        TextView startTv = findViewById(R.id.start);
        TextView endTv = findViewById(R.id.end);

        Date today = new Date();
        startTv.setText("시작일 : " + getYesterday());
        endTv.setText("종료일 : " + dateFormat.format(today));

        startTv.addTextChangedListener(watcher);
        endTv.addTextChangedListener(watcher);

        startTv.setOnClickListener(datePickerListener);
        endTv.setOnClickListener(datePickerListener);

        String startDate = startTv.getText().toString().replaceAll("시작일 : ", "");
        String endDate = endTv.getText().toString().replaceAll("종료일 : ", "");
        settingList(cpSeq, startDate, endDate);

        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    View.OnClickListener datePickerListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String[] dateArr = ((TextView)findViewById(v.getId())).getText().toString().replaceAll("시작일 : ", "").replaceAll("종료일 : ", "").split("-");
            datePicker(dateArr, v.getId()).show();
        }
    };

    public DatePickerDialog datePicker(String[] todayStrArr, final int id){
        DatePickerDialog dialog = new DatePickerDialog(BaobabEventStatistics.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String dateStr = year + "-" + makeMonthAndDay(month + 1) + "-" + makeMonthAndDay(dayOfMonth);
                        if(id == R.id.start){
                            ((TextView)findViewById(id)).setText("시작일 : " + dateStr);
                        }else {
                            ((TextView)findViewById(id)).setText("종료일 : " + dateStr);
                        }
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

    public String getYesterday(){
        Calendar c1 = new GregorianCalendar();
        c1.add(Calendar.DATE, -1); // 오늘날짜로부터 -1
        return dateFormat.format(c1.getTime());
    }

    TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String startDate = ((TextView)findViewById(R.id.start)).getText().toString().replaceAll("시작일 : ", "");
            String endDate = ((TextView)findViewById(R.id.end)).getText().toString().replaceAll("종료일 : ", "");
            settingList(cpSeq, startDate, endDate);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private void settingList(int cpSeq, final String start, final String end){
        String[] endArr = end.split("-");
        String tomorrow = String.valueOf(Integer.parseInt(endArr[2]) + 1);
        String realEnd = endArr[0] + "-" + endArr[1] + "-" + tomorrow;

        Call<List<SalesHistoryVO>> call = retroSingleTon.getInstance().getRetroInterface(getApplicationContext()).eventStatList(cpSeq, start, realEnd);
        call.enqueue(new Callback<List<SalesHistoryVO>>() {
            @Override
            public void onResponse(Call<List<SalesHistoryVO>> call, Response<List<SalesHistoryVO>> response) {
                if(response.isSuccessful()){
                    if(response.body() != null){
                        List<SalesHistoryVO> list = response.body();

                        if(list.size() == 0){
                            Toast.makeText(BaobabEventStatistics.this, "확인된 통계 자료가 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
                        }else{
                            RecyclerView recyclerView = findViewById(R.id.recyclerView);
                            recyclerView.setHasFixedSize(true);
                            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(BaobabEventStatistics.this);
                            recyclerView.setLayoutManager(layoutManager);

                            EventStatListAdapter adapter = new EventStatListAdapter(makeListData(list), BaobabEventStatistics.this, start, end);

                            recyclerView.removeAllViewsInLayout();
                            recyclerView.setAdapter(adapter);
                        }
                    }else {
                        Log.d("이벤트통계 리스트", "response 내용없음");
                        Toast.makeText(BaobabEventStatistics.this, "확인된 통계 자료가 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Log.d("이벤트통계 리스트", "서버 로그 확인 필요");
                    Toast.makeText(BaobabEventStatistics.this, "확인된 통계 자료가 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<SalesHistoryVO>> call, Throwable t) {
                Log.d("이벤트통계 리스트", t.getLocalizedMessage());
                Toast.makeText(BaobabEventStatistics.this, "확인된 통계 자료가 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private List<EventStatListVO> makeListData(List<SalesHistoryVO> list){
        List<EventStatListVO> newList = new ArrayList<>();
        for(int i=0;i<list.size();i++){
            if(i == 0){
                newList.add(firstAddData(list.get(i)));
            }else {
                for(int j=0;j<newList.size();j++){
                    if(newList.get(j).getEventName().equals(list.get(i).getEventName())){
                        newList.get(j).setAllPaid(newList.get(j).getAllPaid() + list.get(i).getSalesPrice());
                        if(list.get(i).getSalesStatus().equals("판매")){
                            newList.get(j).setPaidCount(newList.get(j).getPaidCount() + 1);
                        }else if(list.get(i).getSalesStatus().equals("취소")){
                            newList.get(j).setCancelCount(newList.get(j).getCancelCount() + 1);
                        }else{
                            newList.get(j).setScanCount(newList.get(j).getScanCount() + 1);
                        }
                    }else {
                        firstAddData(list.get(i));
                    }
                }
            }
        }
        return newList;
    }

    private EventStatListVO firstAddData(SalesHistoryVO vo){
        EventStatListVO newVo = new EventStatListVO();
        newVo.setEventName(vo.getEventName());
        newVo.setEventSerial(vo.getEventSerial());
        if(vo.getSalesStatus().equals("판매")){
            newVo.setPaidCount(1);
        }else if(vo.getSalesStatus().equals("취소")){
            newVo.setCancelCount(1);
        }else{
            newVo.setScanCount(1);
        }
        newVo.setAllPaid(vo.getSalesPrice());
        return newVo;
    }
}