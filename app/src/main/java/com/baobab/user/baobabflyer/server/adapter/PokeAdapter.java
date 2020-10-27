package com.baobab.user.baobabflyer.server.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baobab.user.baobabflyer.BaobabPoke;
import com.baobab.user.baobabflyer.BaobabMenu;
import com.baobab.user.baobabflyer.R;
import com.baobab.user.baobabflyer.server.singleTons.RetroSingleTon;
import com.baobab.user.baobabflyer.server.util.ImageUtil;
import com.baobab.user.baobabflyer.server.vo.ShowPokeVO;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PokeAdapter extends RecyclerView.Adapter<PokeAdapter.ItemViewHolder>{

    RetroSingleTon retroSingleTon;

    List<ShowPokeVO> list;
    CheckBox allCheck;
    Context context;
    SharedPreferences spf;

    List<CheckBox> checkBoxes;

    public PokeAdapter(final List<ShowPokeVO> list, CheckBox allCheck, final Context context, SharedPreferences spf, ImageView deleteBtn) {
        this.list = list;
        this.allCheck = allCheck;
        this.context = context;
        this.spf = spf;

        checkBoxes = new ArrayList<>();
        allCheck.setOnCheckedChangeListener(allCheckListener);

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = context.getSharedPreferences("user", 0).getString("email", "");

                for(int i=0;i<checkBoxes.size();i++){
                    final int position = i;
                    if(checkBoxes.get(i).isChecked()){
                        Call<ResponseBody> call = retroSingleTon.getInstance().getRetroInterface(context.getApplicationContext()).delPoke(email, list.get(i).getCpInfoVO().getCP_name(), "our",
                                list.get(i).getPokeVO().getCp_seq());
                        call.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                Toast.makeText(context, "선택한 업체가 찜하기에서 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(context, BaobabPoke.class);
                                context.startActivity(intent);
                                ((Activity)context).finish();
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                Log.d("찜하기 삭제", t.getLocalizedMessage());
                                if(position == (checkBoxes.size() - 1)){
                                    Toast.makeText(context, "삭제가 정상적으로 이루어지지 않았습니다.", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(context, BaobabPoke.class);
                                    context.startActivity(intent);
                                    ((Activity)context).finish();
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.poke_list, viewGroup, false);
        return new PokeAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemViewHolder itemViewHolder, int i) {
        final ShowPokeVO vo = list.get(i);

        itemViewHolder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, BaobabMenu.class);
                intent.putExtra("context", "poke");
                intent.putExtra("userLoc", spf.getString("addr", ""));
                intent.putExtra("info", vo.getCpInfoVO());
                intent.putExtra("longitude", Double.parseDouble(spf.getString("longitude", "0")));
                intent.putExtra("latitude", Double.parseDouble(spf.getString("longitude", "0")));
                context.startActivity(intent);
            }
        });

        itemViewHolder.imageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemViewHolder.checkbox.performClick();
            }
        });

        SimpleTarget<Bitmap> simpleTarget = new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                ImageUtil imageUtil = new ImageUtil();
                itemViewHolder.image.setImageBitmap(imageUtil.getRoundedCornerBitmap(resource, 16));
            }
        };

        if(vo.getImgVoList() == null || vo.getImgVoList().size() == 0){
            Glide.with(context.getApplicationContext()).load(R.drawable.pagesample).asBitmap().override(110, 76).centerCrop().into(simpleTarget);
        }else {
            Glide.with(context.getApplicationContext()).load(vo.getImgVoList().get(0).getImg_url()).asBitmap().override(110, 76).centerCrop().into(simpleTarget);
        }

        itemViewHolder.cpName.setText(vo.getCpInfoVO().getCP_name());
        itemViewHolder.cpAddress.setText(vo.getCpInfoVO().getCP_address() + " " + vo.getCpInfoVO().getCP_addr_details());
        itemViewHolder.distance.setText("나와의 거리 " + setDistance(vo.getCpInfoVO().getDistance()));

        checkBoxes.add(itemViewHolder.checkbox);
        itemViewHolder.checkbox.setOnCheckedChangeListener(checkListener);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private String setDistance(double distance){
        if(distance >= 1){
            return Math.round(distance * 10) / 10 + "km";
        }else {
            distance = distance * 1000;
            return Math.round(distance) + "m";
        }
    }

    CompoundButton.OnCheckedChangeListener allCheckListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked){
                buttonView.setBackground(context.getDrawable(R.drawable.ic_check_on));
                for (CheckBox checkBox : checkBoxes) {
                    checkBox.setChecked(true);
                }
            }else {
                buttonView.setBackground(context.getDrawable(R.drawable.ic_check_off));
                for (CheckBox checkBox : checkBoxes) {
                    checkBox.setChecked(false);
                }
            }
        }
    };

    CompoundButton.OnCheckedChangeListener checkListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked){
                buttonView.setBackground(context.getDrawable(R.drawable.ic_check_on));
            }else {
                buttonView.setBackground(context.getDrawable(R.drawable.ic_check_off));
            }
        }
    };

    public class ItemViewHolder  extends RecyclerView.ViewHolder{

        LinearLayout layout;
        RelativeLayout imageLayout;
        ImageView image;
        CheckBox checkbox;
        TextView cpName;
        TextView cpAddress;
        TextView distance;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            layout = itemView.findViewById(R.id.layout);
            imageLayout = itemView.findViewById(R.id.imageLayout);
            image = itemView.findViewById(R.id.image);
            checkbox = itemView.findViewById(R.id.checkbox);
            cpName = itemView.findViewById(R.id.cpName);
            cpAddress = itemView.findViewById(R.id.cpAddress);
            distance = itemView.findViewById(R.id.distance);
        }
    }
}
