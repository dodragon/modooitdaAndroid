package com.baobab.user.baobabflyer.server.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baobab.user.baobabflyer.BaobabCpEvent;
import com.baobab.user.baobabflyer.R;
import com.baobab.user.baobabflyer.server.singleTons.RetroSingleTon;
import com.baobab.user.baobabflyer.server.vo.EventCpMenuVO;

import java.text.DecimalFormat;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventMenuAdapter extends RecyclerView.Adapter<EventMenuAdapter.ItemViewHolder> {

    RetroSingleTon retroSingleTon;

    List<EventCpMenuVO> list;
    DecimalFormat format;
    Context context;
    int cpSeq;
    int mainSeq;

    public EventMenuAdapter(List<EventCpMenuVO> list, Context context, int cpSeq, int mainSeq) {
        this.list = list;
        this.context = context;
        this.cpSeq = cpSeq;
        this.mainSeq = mainSeq;

        format = new DecimalFormat("###,###");
    }

    @NonNull
    @Override
    public EventMenuAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.event_menu_list, viewGroup, false);
        return new EventMenuAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventMenuAdapter.ItemViewHolder itemViewHolder, int i) {
        final EventCpMenuVO vo = list.get(i);

        itemViewHolder.menuName.setText(vo.getMenuName());
        itemViewHolder.percentAge.setText(String.valueOf(vo.getPercentAge()));
        itemViewHolder.disPrice.setText(format.format(vo.getDisPrice()));
        itemViewHolder.price.setText(format.format(vo.getPrice()));
        itemViewHolder.info.setText(vo.getMenuInfo());

        if(mainSeq == vo.getSeqNum()){
            itemViewHolder.mainStar.setVisibility(View.VISIBLE);
        }

        itemViewHolder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alert = new AlertDialog.Builder(context)
                        .setIcon(R.drawable.logo_and)
                        .setTitle("이벤트")
                        .setMessage(vo.getMenuName() + "을(를)\n메인 메뉴로 지정하시겠습니까?")
                        .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("네", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Call<Integer> call = retroSingleTon.getInstance().getRetroInterface(context.getApplicationContext()).setMainEvent(cpSeq, vo.getSeqNum());
                                call.enqueue(new Callback<Integer>() {
                                    @Override
                                    public void onResponse(Call<Integer> call, Response<Integer> response) {
                                        if(response.isSuccessful()){
                                            if(response.body() != null){
                                                int result = response.body();
                                                if(result > 0){
                                                    Toast.makeText(context, "설정이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                                                    ((BaobabCpEvent)context).onResume();
                                                }else {
                                                    Log.d("대표메뉴설정", "result 실패");
                                                    Toast.makeText(context, "다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                                                }
                                            }else {
                                                Log.d("대표메뉴설정", "response 내용없음");
                                                Toast.makeText(context, "다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                                            }
                                        }else {
                                            Log.d("대표메뉴설정", "서버로그 확인필요");
                                            Toast.makeText(context, "다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Integer> call, Throwable t) {
                                        Log.d("대표메뉴설정", t.getLocalizedMessage());
                                        Toast.makeText(context, "다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        })
                        .create();
                alert.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ItemViewHolder  extends RecyclerView.ViewHolder{
        TextView menuName;
        TextView percentAge;
        TextView price;
        TextView disPrice;
        TextView info;
        LinearLayout layout;
        ImageView mainStar;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            menuName = itemView.findViewById(R.id.menuName);
            percentAge = itemView.findViewById(R.id.percentAge);
            price = itemView.findViewById(R.id.price);
            disPrice = itemView.findViewById(R.id.disPrice);
            info = itemView.findViewById(R.id.info);
            layout = itemView.findViewById(R.id.layout);
            mainStar = itemView.findViewById(R.id.mainStar);
        }
    }
}
