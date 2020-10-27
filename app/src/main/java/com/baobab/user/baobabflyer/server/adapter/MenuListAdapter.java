package com.baobab.user.baobabflyer.server.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baobab.user.baobabflyer.BaobabCpMenu;
import com.baobab.user.baobabflyer.BaobabMenu;
import com.baobab.user.baobabflyer.R;
import com.baobab.user.baobabflyer.server.singleTons.RetroSingleTon;
import com.baobab.user.baobabflyer.server.util.MenuDetailDialog;
import com.baobab.user.baobabflyer.server.vo.MenuAdapterListVO;
import com.baobab.user.baobabflyer.server.vo.MenuVO;
import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MenuListAdapter extends RecyclerView.Adapter<MenuListAdapter.ItemViewHolder> {

    RetroSingleTon retroSingleTon;

    boolean isImage;
    Context activityContext;
    Context appContext;
    List<MenuAdapterListVO> list;
    List<MenuVO> voList;
    boolean hasLine;
    String cpName;
    int cpSeq;

    int size68;

    DecimalFormat format;

    public MenuListAdapter(boolean isImage, Context activityContext, Context appContext, List<MenuAdapterListVO> list, boolean hasLine, String cpName, int cpSeq, List<MenuVO> voList) {
        this.isImage = isImage;
        this.activityContext = activityContext;
        this.appContext = appContext;
        this.list = list;
        this.hasLine = hasLine;
        this.cpName = cpName;
        this.cpSeq = cpSeq;
        this.voList = voList;

        DisplayMetrics dm = activityContext.getResources().getDisplayMetrics();
        size68 = Math.round(68 * dm.density);

        format = new DecimalFormat("###,###");
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        if(isImage){
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.image_menu_recycler_view, viewGroup, false);
        }else{
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.image_non_menu_recycler_view, viewGroup, false);
        }
        return new MenuListAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder itemViewHolder, int i) {
        final MenuAdapterListVO vo = list.get(i);
        final int position = i;

        if(!isImage && position == 0 && !hasLine){
            itemViewHolder.setIsRecyclable(false);
            itemViewHolder.line.setVisibility(View.GONE);
        }

        itemViewHolder.menuName.setText(vo.getMenuName());
        itemViewHolder.price.setText(Currency.getInstance(Locale.KOREA).getSymbol() + " " + format.format(vo.getPrice()));

        if(isImage){
            glideUrlResource(appContext, vo.getMenuImg(), size68, size68).into(itemViewHolder.menuImg);
        }

        if(isImage && (getItemCount() - 1) == position){
            itemViewHolder.line.setVisibility(View.GONE);
        }

        itemViewHolder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<List<MenuVO>> call = retroSingleTon.getInstance().getRetroInterface(appContext).getAllMenu(cpSeq, vo.getMenuName());
                call.enqueue(new Callback<List<MenuVO>>() {
                    @Override
                    public void onResponse(Call<List<MenuVO>> call, Response<List<MenuVO>> response) {
                        if(response.isSuccessful()){
                            if(response.body() != null){
                                List<MenuVO> resultList = response.body();
                                MenuDetailDialog dialog = MenuDetailDialog.newInstance(new Gson().toJson(resultList));
                                dialog.setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light);
                                try{
                                    dialog.show(((BaobabMenu)(activityContext)).getSupportFragmentManager(), "");
                                }catch (ClassCastException e){
                                    dialog.show(((BaobabCpMenu)(activityContext)).getSupportFragmentManager(), "");
                                }
                            }else {
                                Log.d("메뉴상세", "response 내용없음");
                                Toast.makeText(activityContext, "상세조회하기를 할 수 없습니다.", Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            Log.d("메뉴상세", "서버로그 확인필요");
                            Toast.makeText(activityContext, "상세조회하기를 할 수 없습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<MenuVO>> call, Throwable t) {
                        Log.d("메뉴상세", t.getLocalizedMessage());
                        Toast.makeText(activityContext, "상세조회하기를 할 수 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public DrawableRequestBuilder<String> glideUrlResource(Context context, String resource, int width, int height){
        return  Glide.with(context).load(resource).override(width, height).signature(new StringSignature(UUID.randomUUID().toString())).centerCrop().thumbnail(0.1f);
    }

    public class ItemViewHolder  extends RecyclerView.ViewHolder{
        TextView menuName;
        TextView price;
        TextView option;
        ImageView menuImg;
        View line;
        LinearLayout layout;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            layout = itemView.findViewById(R.id.layout);
            menuName = itemView.findViewById(R.id.menuName);
            price = itemView.findViewById(R.id.price);
            if(isImage){
                menuImg = itemView.findViewById(R.id.menuImg);
            }
            line = itemView.findViewById(R.id.line);
        }
    }
}
