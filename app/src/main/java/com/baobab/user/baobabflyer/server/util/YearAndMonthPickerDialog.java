package com.baobab.user.baobabflyer.server.util;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

import com.baobab.user.baobabflyer.R;

public class YearAndMonthPickerDialog extends DialogFragment{

    private static final int MAX_YEAR = 2099;
    private static final int MIN_YEAR = 2019;

    private DatePickerDialog.OnDateSetListener listener;
    private int year;
    private int month;
    Context context;

    Button confirmBtn;
    Button cancelBtn;

    public void setListener(DatePickerDialog.OnDateSetListener listener, int year, int month, Context context) {
        this.listener = listener;
        this.year = year;
        this.month = month;
        this.context = context;
    }

    @Override
    public Dialog onCreateDialog(Bundle saveInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialog = inflater.inflate(R.layout.year_month_picker, null);

        confirmBtn = dialog.findViewById(R.id.btn_confirm);
        cancelBtn = dialog.findViewById(R.id.btn_cancel);

        final NumberPicker monthPicker = dialog.findViewById(R.id.picker_month);
        final NumberPicker yearPicker = dialog.findViewById(R.id.picker_year);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YearAndMonthPickerDialog.this.getDialog().cancel();
            }
        });

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onDateSet(null, yearPicker.getValue(), monthPicker.getValue(), 0);
                YearAndMonthPickerDialog.this.getDialog().cancel();
            }
        });

        monthPicker.setMaxValue(12);
        monthPicker.setMinValue(1);
        monthPicker.setValue(month);

        yearPicker.setMaxValue(MAX_YEAR);
        yearPicker.setMinValue(MIN_YEAR);
        yearPicker.setValue(year);

        builder.setView(dialog);

        return builder.create();
    }

}
