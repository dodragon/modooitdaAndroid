package com.baobab.user.baobabflyer.server.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baobab.user.baobabflyer.BaobabMenuModi;
import com.baobab.user.baobabflyer.BaobabMenuModified;
import com.baobab.user.baobabflyer.R;
import com.baobab.user.baobabflyer.server.singleTons.RetroSingleTon;
import com.baobab.user.baobabflyer.server.vo.MenuVO;
import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MenuUpdateListAdapter extends RecyclerView.Adapter<MenuUpdateListAdapter.ItemViewHolder>{

    RetroSingleTon retroSingleTon;

    List<MenuVO> list;
    Context context;
    int cpSeq;

    List<String> delList;

    public MenuUpdateListAdapter(List<MenuVO> list, final Context context, Button deleteBtn, final String cpName, final int cpSeq) {
        this.list = list;
        this.context = context;
        this.cpSeq = cpSeq;

        delList = new ArrayList<>();

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i=0;i<delList.size();i++){
                    Call<ResponseBody> call = retroSingleTon.getInstance().getRetroInterface(context.getApplicationContext()).menuDel(cpName, delList.get(i).split(", ")[0], delList.get(i).split(", ")[1]);
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if(response.isSuccessful()){
                                Toast.makeText(context, "메뉴가 정상적으로 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(context, BaobabMenuModified.class);
                                intent.putExtra("cpName", cpName);
                                intent.putExtra("cpSeq", cpSeq);
                                context.startActivity(intent);
                                ((Activity)context).finish();
                            }else {
                                Log.d("메뉴 삭제하기", "서버로그 확인 필요");
                                Toast.makeText(context, "메뉴가 삭제되지 않았습니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Log.d("메뉴 삭제하기", t.getLocalizedMessage());
                            Toast.makeText(context, "메뉴가 삭제되지 않았습니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });
    }

    @NonNull
    @Override
    public MenuUpdateListAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.menu_update_list, viewGroup, false);
        return new MenuUpdateListAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemViewHolder itemViewHolder, int i) {
        final MenuVO vo = list.get(i);

        itemViewHolder.checkBox.setOnCheckedChangeListener(checkBoxListener);
        itemViewHolder.checkBox.setTag(vo.getMenu_name() + ", " + vo.getMenu_option());
        if(!vo.getMenu_img().equals("이미지없음")){
            Glide.clear(itemViewHolder.image);
            Glide.with(context).load(vo.getMenu_img()).override(150, 120).centerCrop().signature(new StringSignature(UUID.randomUUID().toString())).into(itemViewHolder.image);
        }else {
            itemViewHolder.image.setVisibility(View.GONE);
        }
        itemViewHolder.menuName.setText(vo.getMenu_name() + " (" + vo.getMenu_option() + ")");

        if(i == (list.size() - 1)){
            itemViewHolder.line.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }

        itemViewHolder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemViewHolder.checkBox.performClick();
            }
        });

        itemViewHolder.layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(context, BaobabMenuModi.class);
                intent.putExtra("vo", vo);
                intent.putExtra("cpSeq", cpSeq);
                context.startActivity(intent);
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        try{
            return list.size();
        }catch (NullPointerException e){
            return 0;
        }
    }

    CompoundButton.OnCheckedChangeListener checkBoxListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                buttonView.setBackground(context.getApplicationContext().getDrawable(R.drawable.ic_check_on));
                delList.add(buttonView.getTag().toString());
            } else {
                buttonView.setBackground(context.getApplicationContext().getDrawable(R.drawable.ic_check_off));
                delList.remove(buttonView.getTag().toString());
            }
        }
    };

    public class ItemViewHolder  extends RecyclerView.ViewHolder{
        LinearLayout layout;
        CheckBox checkBox;
        ImageView image;
        TextView menuName;
        View line;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            layout = itemView.findViewById(R.id.layout);
            checkBox = itemView.findViewById(R.id.checkbox);
            image = itemView.findViewById(R.id.image);
            menuName = itemView.findViewById(R.id.menuName);
            line = itemView.findViewById(R.id.line);
        }
    }
}
