package com.baobab.user.baobabflyer.server.util;

import android.app.Dialog;
import android.content.Intent;
import androidx.fragment.app.DialogFragment;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import android.view.View;
import android.view.WindowManager;

import com.baobab.user.baobabflyer.BaobabDeleteCpEvent;
import com.baobab.user.baobabflyer.BaobabMakeCpEvent;
import com.baobab.user.baobabflyer.BaobabTurnUpdateEvent;
import com.baobab.user.baobabflyer.BaobabUpdateListCpEvent;
import com.baobab.user.baobabflyer.R;

public class EventOptionDialog extends DialogFragment {

    int cpSeq;
    int mainSeq;

    public static EventOptionDialog newInstance(int cpSeq, int mainSeq) {
        Bundle bundle = new Bundle();
        bundle.putString("cpSeq", String.valueOf(cpSeq));
        bundle.putString("mainSeq", String.valueOf(mainSeq));

        EventOptionDialog fragment = new EventOptionDialog();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            cpSeq = Integer.parseInt(getArguments().getString("cpSeq"));
            mainSeq = Integer.parseInt(getArguments().getString("mainSeq"));
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.FullscreenDialog);
        View view = getActivity().getLayoutInflater().inflate(R.layout.activity_event_option_dialog, null);

        view.findViewById(R.id.addEvent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissDialog();
                Intent intent = new Intent(getActivity(), BaobabMakeCpEvent.class);
                intent.putExtra("cpSeq", cpSeq);
                startActivity(intent);
            }
        });

        view.findViewById(R.id.orderChange).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissDialog();
                Intent intent = new Intent(getActivity(), BaobabTurnUpdateEvent.class);
                intent.putExtra("cpSeq", cpSeq);
                startActivity(intent);
            }
        });

        view.findViewById(R.id.deleteEvent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissDialog();
                Intent intent = new Intent(getActivity(), BaobabDeleteCpEvent.class);
                intent.putExtra("cpSeq", cpSeq);
                startActivity(intent);
            }
        });

        view.findViewById(R.id.updateEvent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissDialog();
                Intent intent = new Intent(getActivity(), BaobabUpdateListCpEvent.class);
                intent.putExtra("cpSeq", cpSeq);
                intent.putExtra("mainSeq", mainSeq);
                startActivity(intent);
            }
        });

        builder.setView(view);
        Dialog dialog = builder.create();
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }

    private void dismissDialog() {
        this.dismiss();
    }
}
