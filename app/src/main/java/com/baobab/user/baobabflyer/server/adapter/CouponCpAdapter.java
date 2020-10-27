package com.baobab.user.baobabflyer.server.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baobab.user.baobabflyer.BaobabMenu;
import com.baobab.user.baobabflyer.R;
import com.baobab.user.baobabflyer.server.vo.CPInfoVO;
import com.bumptech.glide.Glide;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class CouponCpAdapter extends RecyclerView.Adapter<CouponCpAdapter.ItemViewHolder> {

    List<CPInfoVO> infoList;
    Context context;
    String userAddress;
    double latitude;
    double longitude;

    DecimalFormat decimalFormat;

    public CouponCpAdapter(List<CPInfoVO> infoList, Context context, String userAddress, double latitude, double longitude) {
        this.infoList = nullCheck(infoList);
        this.context = context;
        this.userAddress = userAddress;
        this.latitude = latitude;
        this.longitude = longitude;

        decimalFormat = new DecimalFormat("###,###");
    }

    public List<CPInfoVO> nullCheck(List<CPInfoVO> list){
        List<CPInfoVO> newList = new ArrayList<>();
        for(int i=0;i<list.size();i++){
            if(list.get(i).getEcmVO() != null){
                newList.add(list.get(i));
            }
        }
        return newList;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.main_slide, viewGroup, false);
        return new CouponCpAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder itemViewHolder, int i) {
        final CPInfoVO cpInfoVO = infoList.get(i);

        Glide.with(context).load(cpInfoVO.getImg_url()).centerCrop().into(itemViewHolder.imageView);

        itemViewHolder.mainCouponTv.setText(cpInfoVO.getCP_name());
        itemViewHolder.mainCouponTv.setSelected(true);
        itemViewHolder.cpNameTv.setText(cpInfoVO.getEcmVO().getMenuName().replaceAll("\n", " "));
        itemViewHolder.cpNameTv.setSelected(true);
        itemViewHolder.cpNameTv.setTextColor(Color.parseColor("#5c7cfa"));
        itemViewHolder.eaTv.setText("판매량:" + cpInfoVO.getEcmVO().getSalesRate());
        if(cpInfoVO.getEcmVO().getPercentAge() > 14){
            itemViewHolder.percent.setText(cpInfoVO.getEcmVO().getPercentAge() + "%");
        }else{
            itemViewHolder.percent.setVisibility(View.GONE);
        }
        itemViewHolder.disPrice.setText(decimalFormat.format(cpInfoVO.getEcmVO().getDisPrice()) + "원");
        itemViewHolder.price.setText(decimalFormat.format(cpInfoVO.getEcmVO().getPrice()) + "원");
        itemViewHolder.price.setPaintFlags(itemViewHolder.price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, 0, 20);
        itemViewHolder.container.setLayoutParams(layoutParams);

        String theme1 = cpInfoVO.getCP_Theme1();
        if(theme1.contains("포장") && theme1.contains("방문")){
            itemViewHolder.ribbonIv2.setVisibility(View.VISIBLE);
            itemViewHolder.ribbonTv2.setText("방문 & 포장");
            itemViewHolder.ribbonTv2.setVisibility(View.VISIBLE);
        }else if(theme1.contains("포장")){
            itemViewHolder.ribbonIv2.setVisibility(View.VISIBLE);
            itemViewHolder.ribbonTv2.setVisibility(View.VISIBLE);
            itemViewHolder.ribbonTv2.setText("방문");
        }else if(theme1.contains("방문")){
            itemViewHolder.ribbonIv.setVisibility(View.VISIBLE);
            itemViewHolder.ribbonTv.setVisibility(View.VISIBLE);
            itemViewHolder.ribbonTv.setText("방문");
        }

        itemViewHolder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, BaobabMenu.class);
                intent.putExtra("context", "page");
                intent.putExtra("userLoc", userAddress);
                intent.putExtra("info", cpInfoVO);
                intent.putExtra("longitude", longitude);
                intent.putExtra("latitude", latitude);
                context.startActivity(intent);
            }
        });

        try {
            if(cpInfoVO.getCpStatus().equals("open") & timeCalcu(cpInfoVO.getBusiness_start(), cpInfoVO.getBusiness_end()) & closeDayCalcu(cpInfoVO.getClose_day())){
                itemViewHolder.cpClose.setVisibility(View.GONE);
            }else if(!cpInfoVO.getCpStatus().equals("open")){
                itemViewHolder.cpClose.setVisibility(View.VISIBLE);
            }else if(closeDayCalcu(cpInfoVO.getClose_day()) & !timeCalcu(cpInfoVO.getBusiness_start(), cpInfoVO.getBusiness_end())){
                itemViewHolder.cpClose.setVisibility(View.VISIBLE);
                itemViewHolder.closeTv.setText(cpInfoVO.getBusiness_start() + " ~ " + cpInfoVO.getBusiness_end());
            }else {
                itemViewHolder.cpClose.setVisibility(View.VISIBLE);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        itemViewHolder.distance.setText(setDistance(cpInfoVO.getDistance()));
        itemViewHolder.revGrade.setText(String.valueOf(cpInfoVO.getRev_grade()));
    }

    @Override
    public int getItemCount() {
        return infoList.size();
    }

    private String setDistance(double distance){
        if(distance >= 1){
            return Math.round(distance * 10) / 10 + "km";
        }else {
            distance = distance * 1000;
            return Math.round(distance) + "m";
        }
    }

    public boolean timeCalcu(String bStart, String bEnd) throws ParseException {
        Date today = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        String start = dateFormat.format(today) + " " + bStart + ":00";
        String end = dateFormat.format(today) + " " + bEnd + ":00";

        SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date startDay = dateFormat2.parse(start);
        Date endDay = dateFormat2.parse(end);

        int startCompare = today.compareTo(startDay);
        int endCompare;
        if(startDay.compareTo(endDay) == -1){
            endCompare = today.compareTo(endDay);
        }else {
            Calendar cal = new GregorianCalendar();
            cal.add(Calendar.DATE, 1);
            Date date = cal.getTime();

            end = dateFormat.format(date) + " " + bEnd + ":00";
            endDay = dateFormat2.parse(end);
            endCompare = today.compareTo(endDay);
        }

        return startCompare == 1 && endCompare == -1;
    }

    public boolean closeDayCalcu(String colseStr){
        Calendar c = Calendar.getInstance();
        int week = c.get(Calendar.WEEK_OF_MONTH);

        String[] weekDay = {"일", "월", "화", "수", "목", "금", "토"};
        Calendar cal = Calendar.getInstance();
        int num = cal.get(Calendar.DAY_OF_WEEK)-1;
        String today = weekDay[num];

        String[] strArr = colseStr.split("~");

        return colseStr.equals("~~~~~무") | !strArr[week - 1].equals(today);
    }

    public class ItemViewHolder  extends RecyclerView.ViewHolder{
        LinearLayout container;
        LinearLayout cpClose;
        TextView closeTv;
        ImageView imageView;
        TextView mainCouponTv;
        TextView cpNameTv;
        TextView eaTv;
        ImageView ribbonIv;
        TextView ribbonTv;
        ImageView ribbonIv2;
        TextView ribbonTv2;
        TextView disPrice;
        TextView price;
        TextView percent;
        TextView distance;
        TextView revGrade;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            this.container = itemView.findViewById(R.id.container);
            this.cpClose = itemView.findViewById(R.id.cpClose);
            this.closeTv = itemView.findViewById(R.id.closeTv);
            this.imageView = itemView.findViewById(R.id.mainImg);
            this.mainCouponTv = itemView.findViewById(R.id.mainCoupon);
            this.cpNameTv = itemView.findViewById(R.id.cpName);
            this.eaTv = itemView.findViewById(R.id.couponEa);
            this.ribbonIv = itemView.findViewById(R.id.ribbon2_ic);
            this.ribbonTv = itemView.findViewById(R.id.ribbon2_tv);
            this.ribbonIv2 = itemView.findViewById(R.id.ribbon_ic);
            this.ribbonTv2 = itemView.findViewById(R.id.ribbon_tv);
            this.disPrice = itemView.findViewById(R.id.disPrice);
            this.price = itemView.findViewById(R.id.price);
            this.percent = itemView.findViewById(R.id.percent);
            this.distance = itemView.findViewById(R.id.distance);
            this.revGrade = itemView.findViewById(R.id.revGrade);
        }
    }
}
