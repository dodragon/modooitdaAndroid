package com.baobab.user.baobabflyer.server.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baobab.user.baobabflyer.BaobabUserTicketList;
import com.baobab.user.baobabflyer.R;
import com.baobab.user.baobabflyer.server.singleTons.RetroSingleTon;
import com.baobab.user.baobabflyer.server.util.UserUsedTicketDetailDialog;
import com.baobab.user.baobabflyer.server.vo.PayMenusVO;
import com.baobab.user.baobabflyer.server.vo.PaymentVO;
import com.baobab.user.baobabflyer.server.vo.UserTicketHistoryVO;

import java.text.SimpleDateFormat;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserCouponHistoryListAdapter extends RecyclerView.Adapter<UserCouponHistoryListAdapter.ItemViewHolder>{

    RetroSingleTon retroSingleTon;

    List<UserTicketHistoryVO> list;
    Context context;
    SimpleDateFormat format;

    public UserCouponHistoryListAdapter(List<UserTicketHistoryVO> list, Context context) {
        this.list = list;
        this.context = context;
        this.format = new SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분 ss초");
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.coupon_user_history_list, viewGroup, false);
        return new UserCouponHistoryListAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder itemViewHolder, int i) {
        final UserTicketHistoryVO vo = list.get(i);

        itemViewHolder.cpName.setText(vo.getCpName());
        itemViewHolder.date.setText(format.format(vo.getCurDate()));

        itemViewHolder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<List<PayMenusVO>> call = retroSingleTon.getInstance().getRetroInterface(context.getApplicationContext()).useTicketHistoryDetail(vo.getOrderNum());
                call.enqueue(new Callback<List<PayMenusVO>>() {
                    @Override
                    public void onResponse(Call<List<PayMenusVO>> call, Response<List<PayMenusVO>> response) {
                        if(response.isSuccessful()){
                            if(response.body() != null){
                                final List<PayMenusVO> resultList = response.body();

                                Call<PaymentVO>  call2 = retroSingleTon.getInstance().getRetroInterface(context.getApplicationContext()).payDetail(vo.getOrderNum());
                                call2.enqueue(new Callback<PaymentVO>() {
                                    @Override
                                    public void onResponse(Call<PaymentVO> call, Response<PaymentVO> response) {
                                        if(response.isSuccessful()){
                                            if(response.body() != null){
                                                PaymentVO paymentVO = response.body();

                                                UserUsedTicketDetailDialog dialog = UserUsedTicketDetailDialog.newInstance(vo, resultList, paymentVO, context);
                                                dialog.setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light);
                                                dialog.show(((BaobabUserTicketList)(context)).getSupportFragmentManager(), "");
                                            }else {
                                                Log.d("사용티켓 디테일 payment", "response 내용없음");
                                            }
                                        }else {
                                            Log.d("사용티켓 디테일 payment", "서버로그 확인 필요");
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<PaymentVO> call, Throwable t) {
                                        Log.d("사용티켓 디테일 payment", t.getLocalizedMessage());
                                    }
                                });
                            }else {
                                Log.d("사용티켓 디테일", "response 내용없음");
                            }
                        }else {
                            Log.d("사용티켓 디테일", "서버로그 확인 필요");
                        }
                    }

                    @Override
                    public void onFailure(Call<List<PayMenusVO>> call, Throwable t) {
                        Log.d("사용티켓 디테일", t.getLocalizedMessage());
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ItemViewHolder  extends RecyclerView.ViewHolder{
        LinearLayout layout;
        TextView cpName;
        TextView date;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            layout = itemView.findViewById(R.id.layout);
            cpName = itemView.findViewById(R.id.cpName);
            date = itemView.findViewById(R.id.date);
        }
    }
}
