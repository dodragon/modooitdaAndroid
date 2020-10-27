package com.baobab.user.baobabflyer.server.adapter;

import android.graphics.Color;
import android.graphics.Typeface;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baobab.user.baobabflyer.R;
import com.baobab.user.baobabflyer.server.util.LineChartValueFormatter;
import com.baobab.user.baobabflyer.server.vo.StatChartDataVO;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StatisticsListAdapter  extends RecyclerView.Adapter<StatisticsListAdapter.ItemViewHolder>{

    String[] titleArr;
    int[] aData;
    int[] bData;
    String div;
    List<List<StatChartDataVO>> chartLists;

    public StatisticsListAdapter(String[] titleArr, int[] aData, int[] bData, String div, List<List<StatChartDataVO>> chartLists) {
        this.titleArr = titleArr;
        this.aData = aData;
        this.bData = bData;
        this.div = div;
        this.chartLists = chartLists;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cp_statistics_list, viewGroup, false);
        return new StatisticsListAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder itemViewHolder, int i) {
        itemViewHolder.aEa.setText(aData[i] + "회");
        itemViewHolder.bEa.setText(bData[i] + "회");
        itemViewHolder.title.setText(titleArr[i]);

        if(div.equals("일")){
            itemViewHolder.timeDiv.setText("전일");
            itemViewHolder.barChart.setVisibility(View.VISIBLE);
            itemViewHolder.lineChart.setVisibility(View.GONE);
            setBarChart(aData[i], bData[i], itemViewHolder.barChart);
        }else {
            itemViewHolder.timeDiv.setText("전월");
            itemViewHolder.barChart.setVisibility(View.GONE);
            itemViewHolder.lineChart.setVisibility(View.VISIBLE);
            drawLineGraph(chartLists.get(i), itemViewHolder);
        }
    }

    @Override
    public int getItemCount() {
        return titleArr.length;
    }

    public void setBarChart(int aData, int bData, HorizontalBarChart barChart){
        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(bData, 0));
        entries.add(new BarEntry(aData, 1));

        BarDataSet dataSet = new BarDataSet(entries, "");
        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

        ArrayList<String> labels = new ArrayList<>();
        labels.add("전일");
        labels.add("금일");

        BarData data = new BarData(labels, dataSet);
        dataSet.setColors(ColorTemplate.LIBERTY_COLORS);

        barChart.setData(data);
        barChart.setDescription("");
        barChart.animateY(1500, Easing.EasingOption.EaseInOutCubic);
        barChart.invalidate();
        barChart.getLegend().setEnabled(false);
    }

    public void drawLineGraph(List<StatChartDataVO> list, ItemViewHolder itemViewHolder){
        Log.d("그래서 어찌오는디?", list.toString());
        LineChart chart = itemViewHolder.lineChart;

        ArrayList<String> labels = new ArrayList<>();
        ArrayList<Entry> entries = new ArrayList<>();
        for(int i=0;i<list.size();i++){
            entries.add(new Entry(list.get(i).getValue(), i));
            labels.add(dayToInteger(list.get(i).getDt()) + "일");
        }

        new Entry(1, 1);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();

        LineDataSet dataSet = new LineDataSet(entries, "횟수");
        dataSet.setDrawCircles(false);
        dataSet.setColor(Color.rgb(217, 208, 43));
        dataSet.setLineWidth(2f);
        dataSet.setValueTextSize(12f);
        dataSet.setValueTypeface(Typeface.DEFAULT_BOLD);
        dataSet.setValueTextColor(Color.rgb(255, 106, 41));
        dataSets.add(dataSet);

        LineData data = new LineData(labels, dataSets);
        data.setValueFormatter(new LineChartValueFormatter());

        chart.setDescription("");
        chart.setData(data);
        chart.animateX(1500, Easing.EasingOption.EaseInOutCubic);
        chart.animateY(1500, Easing.EasingOption.EaseInOutCubic);
    }

    private int dayToInteger(Date date){
        SimpleDateFormat format = new SimpleDateFormat("dd");
        return Integer.parseInt(format.format(date));
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder{
        TextView aEa;
        TextView bEa;
        TextView title;
        TextView timeDiv;
        HorizontalBarChart barChart;
        LineChart lineChart;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            aEa = itemView.findViewById(R.id.aEa);
            bEa = itemView.findViewById(R.id.bEa);
            title = itemView.findViewById(R.id.title);
            timeDiv = itemView.findViewById(R.id.timeDiv);
            barChart = itemView.findViewById(R.id.barChart);
            lineChart = itemView.findViewById(R.id.lineChart);
        }
    }
}
