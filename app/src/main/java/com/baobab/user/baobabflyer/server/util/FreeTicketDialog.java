package com.baobab.user.baobabflyer.server.util;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.baobab.user.baobabflyer.CpPasswordCert;
import com.baobab.user.baobabflyer.R;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class FreeTicketDialog extends DialogFragment {

    private static double percent;
    private static int cpSeq;
    private static Context context;
    private static String password;
    private static String cpName;

    public static FreeTicketDialog newInstance(double percentAge, int cpSeqs, Context acContext, String pw, String cpNames) {
        Bundle bundle = new Bundle();
        bundle.putString("", "");

        percent = percentAge;
        context = acContext;
        cpSeq = cpSeqs;
        password = pw;
        cpName = cpNames;

        FreeTicketDialog fragment = new FreeTicketDialog();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.FullscreenDialog);
        final View view = getActivity().getLayoutInflater().inflate(R.layout.free_ticket_dialog, null);

        builder.setView(view);
        Dialog dialog = builder.create();
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialog.setCanceledOnTouchOutside(false);

        ((EditText) view.findViewById(R.id.price)).setHint(percent + "% 할인받기");

        view.findViewById(R.id.freeTicketBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String priceStr = ((EditText) view.findViewById(R.id.price)).getText().toString();
                if (!priceStr.equals("")) {
                    int price = Integer.parseInt(priceStr);
                    int disPrice = (int) (price - (price / 100 * percent));
                    if (price >= 1000) {
                        Intent intent = new Intent(context, CpPasswordCert.class);
                        intent.putExtra("cpSeq", cpSeq);
                        intent.putExtra("cpName", cpName);
                        intent.putExtra("disPrice", disPrice);
                        intent.putExtra("price", price);
                        intent.putExtra("pw", password);
                        startActivity(intent);
                    }else {
                        Toast.makeText(context, "결제 금액이 1000원 이상 이어야 합니다.", Toast.LENGTH_SHORT).show();    
                    }
                } else {
                    Toast.makeText(context, "결제 금액을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        view.findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissDialog();
            }
        });

        return dialog;
    }

    public void dismissDialog() {
        this.dismiss();
    }
}
