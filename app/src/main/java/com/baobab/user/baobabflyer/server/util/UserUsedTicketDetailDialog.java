package com.baobab.user.baobabflyer.server.util;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.baobab.user.baobabflyer.R;
import com.baobab.user.baobabflyer.ReviewInsert;
import com.baobab.user.baobabflyer.server.adapter.PaidMenusAdapter;
import com.baobab.user.baobabflyer.server.vo.PayMenusVO;
import com.baobab.user.baobabflyer.server.vo.PaymentVO;
import com.baobab.user.baobabflyer.server.vo.UserTicketHistoryVO;
import java.text.DecimalFormat;
import java.util.List;

public class UserUsedTicketDetailDialog extends DialogFragment {

    public static View view;
    static UserTicketHistoryVO vo;
    static List<PayMenusVO> list;
    static PaymentVO paymentVO;
    public static Context context;
    private static DecimalFormat format;

    public static UserUsedTicketDetailDialog newInstance(UserTicketHistoryVO ticket, List<PayMenusVO> menusList, PaymentVO payVO, Context thisContext) {
        Bundle bundle = new Bundle();
        bundle.putString("", "");

        vo = ticket;
        list = menusList;
        context = thisContext;
        format = new DecimalFormat("###,###");
        paymentVO = payVO;

        UserUsedTicketDetailDialog fragment = new UserUsedTicketDetailDialog();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.FullscreenDialog);
        view = getActivity().getLayoutInflater().inflate(R.layout.ticket_detail_used_dialog, null);

        builder.setView(view);
        Dialog dialog = builder.create();
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialog.setCanceledOnTouchOutside(false);

        view.findViewById(R.id.clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissDialog();
            }
        });

        RecyclerView recyclerView = view.findViewById(R.id.paidMenuRecyclerView);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);

        PaidMenusAdapter adapter = new PaidMenusAdapter(list);

        recyclerView.removeAllViewsInLayout();
        recyclerView.setAdapter(adapter);

        int totalPrice = 0;
        int price = 0;

        for(int i=0;i<list.size();i++){
            totalPrice += list.get(i).getPrice();
            price += list.get(i).getDisPrice();
        }

        int disPrice = totalPrice - price;

        ((TextView)view.findViewById(R.id.total)).setText(format.format(totalPrice) + "원");
        ((TextView)view.findViewById(R.id.paidCount)).setText(format.format(price) + "원");
        String disPriceStr;
        if(disPrice > 0){
            disPriceStr = "-" + format.format(disPrice) + "원";
        }else {
            disPriceStr = format.format(disPrice) + "원";
        }
        ((TextView)view.findViewById(R.id.disCount)).setText(disPriceStr);

        if(paymentVO.getRevFlag() == 0){
            view.findViewById(R.id.review_insert).setVisibility(View.VISIBLE);
            view.findViewById(R.id.review_insert).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ReviewInsert.class);
                    intent.putExtra("cpName", vo.getCpName());
                    intent.putExtra("cpSeq", vo.getCpSeq());
                    intent.putExtra("orderNum", paymentVO.getOrderNum());
                    startActivity(intent);
                }
            });
        }else {
            view.findViewById(R.id.review_insert).setVisibility(View.GONE);
        }

        return dialog;
    }

    private void dismissDialog() {
        this.dismiss();
    }
}
