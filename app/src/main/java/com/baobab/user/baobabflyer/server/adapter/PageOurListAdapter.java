package com.baobab.user.baobabflyer.server.adapter;

import android.content.Context;
import android.content.Intent;
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
import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;

public class PageOurListAdapter extends RecyclerView.Adapter<PageOurListAdapter.ItemViewHolder>{

    private List<CPInfoVO> cpInfoList;
    private Context context;

    private double longitude;
    private double latitude;
    private String userAddress;

    private DrawableRequestBuilder<Integer> starN;
    private DrawableRequestBuilder<Integer> starP;

    public PageOurListAdapter(List<CPInfoVO> cpInfoList, Context context, double longitude, double latitude, String userAddress) {
        this.cpInfoList = cpInfoList;
        this.context = context;
        this.longitude = longitude;
        this.latitude = latitude;
        this.userAddress = userAddress;

        starN = glideDrawableResource(context, R.drawable.star_n, 18, 18);
        starP = glideDrawableResource(context, R.drawable.star_p, 18, 18);
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.page_our_list, viewGroup, false);
        return new PageOurListAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder itemViewHolder, int i) {
        final CPInfoVO item = cpInfoList.get(i);

        try {
            if(timeCalcu(item.getBusiness_start(), item.getBusiness_end()) && closeDayCalcu(item.getClose_day())){
                itemViewHolder.businessLayout.setVisibility(View.GONE);
            }else {
                itemViewHolder.businessLayout.setVisibility(View.VISIBLE);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        itemViewHolder.cpNameTv.setText(item.getCP_name());

        itemViewHolder.allLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, BaobabMenu.class);
                intent.putExtra("context", "page");
                intent.putExtra("userLoc", userAddress);
                intent.putExtra("info", item);
                intent.putExtra("longitude", longitude);
                intent.putExtra("latitude", latitude);
                context.startActivity(intent);
            }
        });

        if(item.getImg_url() != null && item.getImg_url().startsWith("http")){
            glideUrlResource(context, item.getImg_url().replaceAll("baobabMainImg", "baobabMinMainImg"), 420, 312, item.getImgFlag()).into(itemViewHolder.mainImg);
        }else {
            glideDrawableResource(context, R.drawable.pagesample, 420, 312).into(itemViewHolder.mainImg);
        }

        for(int j=0;j<5;j++){
            if(j<item.getRev_grade()){
                starP.into((ImageView) itemViewHolder.revLayout.getChildAt(j));
            }else {
                starN.into((ImageView) itemViewHolder.revLayout.getChildAt(j));
            }
        }

        String intro = item.getCP_intro();
        if(intro.length() == 0){
            intro = item.getCP_address() + " " + item.getCP_addr_details();
        }

        itemViewHolder.cpIntroTv.setText(intro);
        itemViewHolder.distanceTv.setText(distanceCalcu(item.getDistance()));
    }

    @Override
    public int getItemCount() {
        return cpInfoList.size();
    }

    public String distanceCalcu(double dis) {
        if (dis > 1) {
            dis = Double.parseDouble(String.format("%.2f", dis));
            return dis + "km";
        } else {
            return (int) (dis * 1000) + "m";
        }
    }

    public DrawableRequestBuilder<Integer> glideDrawableResource(Context context, int resource, int width, int height){
        return  Glide.with(context).load(resource).override(width, height).centerCrop();
    }

    public DrawableRequestBuilder<String> glideUrlResource(Context context, String resource, int width, int height, int imgFlag){
        if(imgFlag == 1){
            return  Glide.with(context).load(resource).signature(new StringSignature(UUID.randomUUID().toString())).override(width, height).centerCrop().thumbnail(0.1f);
        }else{
            return  Glide.with(context).load(resource).override(width, height).centerCrop().thumbnail(0.1f);
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
        ImageView mainImg;
        TextView cpNameTv;
        TextView cpIntroTv;
        TextView distanceTv;
        LinearLayout revLayout;
        ImageView babDealImg;
        LinearLayout allLayout;
        TextView rev_num;
        LinearLayout businessLayout;


        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            mainImg = itemView.findViewById(R.id.main_img);
            cpNameTv = itemView.findViewById(R.id.cpName);
            cpIntroTv = itemView.findViewById(R.id.cpIntro);
            distanceTv = itemView.findViewById(R.id.distance);
            revLayout = itemView.findViewById(R.id.rev_layout);
            allLayout = itemView.findViewById(R.id.allLayout);
            rev_num = itemView.findViewById(R.id.rev_num);
            businessLayout = itemView.findViewById(R.id.businessLayout);
        }
    }
}
