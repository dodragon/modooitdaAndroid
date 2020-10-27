package com.baobab.user.baobabflyer;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.baobab.user.baobabflyer.server.adapter.EventMenuStatListAdapter;
import com.baobab.user.baobabflyer.server.singleTons.RetroSingleTon;
import com.baobab.user.baobabflyer.server.util.LineChartValueFormatter;
import com.baobab.user.baobabflyer.server.vo.EventOptionStatVO;
import com.baobab.user.baobabflyer.server.vo.MenuSpecListVO;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BaobabEventStatisticsDetail extends AppCompatActivity {

    RetroSingleTon retroSingleTon;

    String eventSerial;
    SimpleDateFormat dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baobab_event_statistics_detail);

        eventSerial = getIntent().getStringExtra("eventSerial");
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        TextView startTv = findViewById(R.id.start);
        TextView endTv = findViewById(R.id.end);

        String start = getIntent().getStringExtra("start");
        String end = getIntent().getStringExtra("end");

        startTv.setText("시작일 : " + start);
        endTv.setText("종료일 : " + end);

        startTv.addTextChangedListener(watcher);
        endTv.addTextChangedListener(watcher);

        startTv.setOnClickListener(datePickerListener);
        endTv.setOnClickListener(datePickerListener);

        String startDate = startTv.getText().toString().replaceAll("시작일 : ", "");
        String endDate = endTv.getText().toString().replaceAll("종료일 : ", "");
        settingDisplay(startDate, endDate);
    }

    View.OnClickListener datePickerListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String[] dateArr = ((TextView)findViewById(v.getId())).getText().toString().replaceAll("시작일 : ", "").replaceAll("종료일 : ", "").split("-");
            datePicker(dateArr, v.getId()).show();
        }
    };

    public DatePickerDialog datePicker(String[] todayStrArr, final int id){
        DatePickerDialog dialog = new DatePickerDialog(BaobabEventStatisticsDetail.this,
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

    TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String startDate = ((TextView)findViewById(R.id.start)).getText().toString().replaceAll("시작일 : ", "");
            String endDate = ((TextView)findViewById(R.id.end)).getText().toString().replaceAll("종료일 : ", "");
            settingDisplay(startDate, endDate);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private void settingDisplay(String start, String end){
        String[] endArr = end.split("-");
        String tomorrow = String.valueOf(Integer.parseInt(endArr[2]) + 1);
        String realEnd = endArr[0] + "-" + endArr[1] + "-" + tomorrow;

        Call<List<EventOptionStatVO>> call = retroSingleTon.getInstance().getRetroInterface(getApplicationContext()).eventDetailStat(eventSerial, start, realEnd);
        call.enqueue(new Callback<List<EventOptionStatVO>>() {
            @Override
            public void onResponse(Call<List<EventOptionStatVO>> call, Response<List<EventOptionStatVO>> response) {
                if(response.isSuccessful()){
                    if(response.body() != null){
                        List<EventOptionStatVO> list = response.body();
                        if (list.size() == 0){
                            Log.d("Event Stat Detail", "list size = 0");
                            Toast.makeText(BaobabEventStatisticsDetail.this, "확인 가능한 통계 자료가 없습니다.", Toast.LENGTH_SHORT).show();
                        }else{
                            Log.d("전체 리스트", list.toString());
                            settingChart(list);
                            settingSpinner(list);
                        }
                    }else {
                        Log.d("Event Stat Detail", "response 내용없음");
                        Toast.makeText(BaobabEventStatisticsDetail.this, "확인 가능한 통계 자료가 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Log.d("Event Stat Detail", "서버 로그 확인 필요");
                    Toast.makeText(BaobabEventStatisticsDetail.this, "확인 가능한 통계 자료가 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<EventOptionStatVO>> call, Throwable t) {
                Log.d("Event Stat Detail", t.getLocalizedMessage());
                Toast.makeText(BaobabEventStatisticsDetail.this, "확인 가능한 통계 자료가 없습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void settingChart(List<EventOptionStatVO> list){
        PieChart eaChart = findViewById(R.id.pieChartEa);
        PieChart salesChart = findViewById(R.id.pieChartSales);

        ArrayList<String> title = new ArrayList<>();
        ArrayList<Entry> eaEntry = new ArrayList<>();
        ArrayList<Entry> salesEntry = new ArrayList<>();
        for(int i=0;i<list.size();i++){
            title.add(list.get(i).getOptionName());
            eaEntry.add(new Entry(list.get(i).getOptionSaleEa(), i));
            salesEntry.add(new Entry(list.get(i).getOptionSales(), i));
        }

        eaChart.setDescription("판매량");
        eaChart.setRotationEnabled(true);
        eaChart.setDrawHoleEnabled(false);
        eaChart.setDrawSliceText(false);
        eaChart.animateY(1500, Easing.EasingOption.EaseInOutCubic);
        salesChart.setDescription("판매액");
        salesChart.setRotationEnabled(true);
        salesChart.setDrawHoleEnabled(false);
        salesChart.setDrawSliceText(false);
        salesChart.animateY(1500, Easing.EasingOption.EaseInOutCubic);

        PieDataSet dataSetEa = new PieDataSet(eaEntry, "");
        dataSetEa.setSliceSpace(3f);
        dataSetEa.setSelectionShift(2f);

        int[] colors = ColorTemplate.JOYFUL_COLORS;
        dataSetEa.setColors(colors);

        PieData eaData = new PieData(title, dataSetEa);
        eaData.setValueFormatter(new LineChartValueFormatter());
        eaData.setValueTextSize(11f);
        eaData.setValueTextColor(Color.DKGRAY);

        PieDataSet dataSetSales = new PieDataSet(salesEntry, "");
        dataSetSales.setSliceSpace(3f);
        dataSetSales.setSelectionShift(2f);

        dataSetSales.setColors(colors);

        PieData salesData = new PieData(title, dataSetSales);
        salesData.setValueFormatter(new LineChartValueFormatter());
        salesData.setValueTextSize(11f);
        salesData.setValueTextColor(Color.DKGRAY);

        eaChart.setData(eaData);
        eaChart.highlightValues(null);
        eaChart.invalidate();
        eaChart.setDescription("");
        eaChart.setDescriptionPosition(3f, 3f);
        salesChart.setData(salesData);
        salesChart.highlightValues(null);
        salesChart.invalidate();
        salesChart.setDescription("");
        salesChart.setDescriptionPosition(3f, 3f);

        Legend eaLegend = eaChart.getLegend();
        eaLegend.setEnabled(true);
        Legend salesLegend = salesChart.getLegend();
        salesLegend.setEnabled(true);
    }

    private void settingSpinner(final List<EventOptionStatVO> list){
        View.OnClickListener listOrderByListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Button)findViewById(R.id.eaOd)).setTextColor(Color.rgb(51, 51, 51));
                ((Button)findViewById(R.id.eaOd)).setTypeface(Typeface.DEFAULT);
                ((Button)findViewById(R.id.salesOd)).setTextColor(Color.rgb(51, 51, 51));
                ((Button)findViewById(R.id.salesOd)).setTypeface(Typeface.DEFAULT);

                ((Button)findViewById(v.getId())).setTextColor(Color.parseColor("#5c7cfa"));
                ((Button)findViewById(v.getId())).setTypeface(Typeface.DEFAULT_BOLD);

                String optionName = v.getTag().toString();

                List<MenuSpecListVO> specList = new ArrayList<>();
                for(int i=0;i<list.size();i++){
                    if(list.get(i).getOptionName().equals(optionName)){
                        specList = list.get(i).getMenuSpecs();
                        break;
                    }
                }

                RecyclerView recyclerView = findViewById(R.id.recyclerView);
                recyclerView.setHasFixedSize(true);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(BaobabEventStatisticsDetail.this);
                recyclerView.setLayoutManager(layoutManager);

                EventMenuStatListAdapter adapter = new EventMenuStatListAdapter(orderList(specList, v.getId()));

                recyclerView.removeAllViewsInLayout();
                recyclerView.setAdapter(adapter);
            }
        };

        findViewById(R.id.salesOd).setOnClickListener(listOrderByListener);
        findViewById(R.id.eaOd).setOnClickListener(listOrderByListener);

        String[] optionName = new String[list.size()];
        for(int i=0;i<list.size();i++){
            optionName[i] = list.get(i).getOptionName();
        }

        final Spinner spinner = findViewById(R.id.optionNameSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, optionName);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                findViewById(R.id.eaOd).setTag(parent.getSelectedItem().toString());
                findViewById(R.id.salesOd).setTag(parent.getSelectedItem().toString());
                findViewById(R.id.salesOd).performClick();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private List<MenuSpecListVO> orderList(List<MenuSpecListVO> list, final int id){
        Collections.sort(list, new Comparator<MenuSpecListVO>() {
            @Override
            public int compare(MenuSpecListVO o1, MenuSpecListVO o2) {
                int firstValue;
                int secondValue;
                if(id == R.id.salesOd){
                    firstValue = o1.getPaidEa();
                    secondValue = o2.getPaidEa();
                    return compareNumber(firstValue, secondValue);
                }else{
                    firstValue = o1.getAllPaid();
                    secondValue = o2.getAllPaid();
                    return compareNumber(firstValue, secondValue);
                }
            }
        });

        return list;
    }

    private int compareNumber(int a, int b){
        if(a > b){
            return -1;
        }else if(a < b){
            return 1;
        }else{
            return 0;
        }
    }
}
