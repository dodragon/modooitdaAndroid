package com.baobab.user.baobabflyer.server.util;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.baobab.user.baobabflyer.R;
import com.baobab.user.baobabflyer.server.singleTons.RetroSingleTon;
import com.baobab.user.baobabflyer.server.vo.EventCpMenuVO;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MenuInfoDialog extends DialogFragment {

    RetroSingleTon retroSingleTon;

    public static Context context;
    public static String optionSerial;
    public static EventCpMenuVO vo;

    public static MenuInfoDialog newInstance(Context acContext, String option, EventCpMenuVO menuVo) {
        Bundle bundle = new Bundle();
        bundle.putString("", "");

        context = acContext;
        optionSerial = option;
        vo = menuVo;

        MenuInfoDialog fragment = new MenuInfoDialog();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.FullscreenDialog);
        final View view = getActivity().getLayoutInflater().inflate(R.layout.selected_info_dialog, null);

        builder.setView(view);
        Dialog dialog = builder.create();
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialog.setCanceledOnTouchOutside(false);

        view.findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissDialog();
            }
        });

        Call<String> call = retroSingleTon.getInstance().getRetroInterface(context.getApplicationContext()).optionNameSelect(optionSerial);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.isSuccessful()){
                    if(response.body() != null){
                        String result = response.body();
                        ((TextView)view.findViewById(R.id.optionName)).setText(result);
                    }else {
                        Log.d("optionNameSelect ::: ", "response 내용없음");
                        ((TextView)view.findViewById(R.id.optionName)).setText("");
                    }
                }else {
                    Log.d("optionNameSelect ::: ", "서버로그 확인 필요");
                    ((TextView)view.findViewById(R.id.optionName)).setText("");
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("optionNameSelect ::: ", t.getLocalizedMessage());
                ((TextView)view.findViewById(R.id.optionName)).setText("");
            }
        });

        ((TextView)view.findViewById(R.id.menuName)).setText(vo.getMenuName());
        ((TextView)view.findViewById(R.id.info)).setText(vo.getMenuInfo());
        return dialog;
    }

    private void dismissDialog() {
        this.dismiss();
    }
}
