package com.qryl.qrylyh.view;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;

/**
 * Created by hp on 2017/9/5.
 */

public class MyAlertDialog extends AlertDialog.Builder {
    private View view;

    public MyAlertDialog(Context context, View view) {
        super(context);
        this.view = view;
    }

    public MyAlertDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    public AlertDialog show() {
        setView(view);
        return super.show();
    }
}
