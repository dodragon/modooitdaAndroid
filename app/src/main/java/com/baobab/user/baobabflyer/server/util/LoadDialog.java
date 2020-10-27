package com.baobab.user.baobabflyer.server.util;

import android.app.ProgressDialog;
import android.content.Context;

public class LoadDialog {
    Context context;
    ProgressDialog dialog;

    public LoadDialog(Context context) {
        this.context = context;
        dialog = new ProgressDialog(context);
    }

    public void showDialog() {
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(false);
        dialog.setMessage("로딩중입니다..");
        dialog.show();
    }

    public void dialogCancel() {
        dialog.dismiss();
    }
}
