package com.qryl.qrylyh.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

/**
 * Created by hp on 2017/9/5.
 */

public class DialogUtil {

    /**
     * @param context
     * @param titleResId
     * @param messageResId
     * @param cancelable
     * @param yesOnClick
     * @param noOnClick
     */
    public static void showDialog(Context context, String titleResId, int messageResId, boolean cancelable,
                                  DialogInterface.OnClickListener yesOnClick,
                                  DialogInterface.OnClickListener noOnClick) {
        AlertDialog dialog = new AlertDialog.Builder(context).setTitle(titleResId)
                .setMessage(messageResId).setCancelable(cancelable)
                .setPositiveButton("yes", yesOnClick).setNegativeButton("no", noOnClick).create();
        dialog.show();
    }

    /**
     * 自定义View
     *
     * @param context
     * @param v
     * @param cancelable
     */
    public static AlertDialog showCustomDialog(Context context, View v, boolean cancelable, DialogInterface.OnClickListener onClickListener) {
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(v)
                .setCancelable(cancelable)
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", onClickListener).create();
        if (dialog != null) {
            dialog.show();
        }
        return dialog;
    }

    /**
     * 单选对话框
     *
     * @param context
     * @param titleResId
     * @param arrayId
     * @param choiceOnClickListener
     */
    public static void showMultiItemsDialog(Context context, String titleResId, int arrayId,
                                            DialogInterface.OnClickListener choiceOnClickListener) {
        AlertDialog dialog = new AlertDialog.Builder(context).setTitle(titleResId)
                .setSingleChoiceItems(arrayId, -1, choiceOnClickListener).setCancelable(true).create();
        dialog.show();
    }
}

