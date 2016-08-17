package com.doing.azhihu.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Toast;

import com.doing.azhihu.R;

/**
 * Created by Doing on 2016/8/17 0017.
 */

/*
    // progress
    protected void showProgressDialog(Context context, String msg)
    protected void dismissProgressDialog()

    //普通提醒dialog
    protected void showCommonAlertDialog(Context context, String msg)

    //Toast
    protected void showLongSnackBar(View rootView, String msg)
    protected void showShortSnackBar(View rootView, String msg)
    protected void showLongToast(Context context, String msg)
    protected void showShortToast(Context context, String msg)

*/
public class AndroidUtils {

    private static ProgressDialog mCommonProgressDialog;

    //旋转圆形 progress dialog
    public static void showProgressDialog(Context context, String msg) {
        if (mCommonProgressDialog != null && mCommonProgressDialog.isShowing())
            dismissProgressDialog();

        mCommonProgressDialog = ProgressDialog.show(context, context.getResources().getString(R.string.app_name), msg);
    }

    public static void dismissProgressDialog() {
        if (mCommonProgressDialog != null) {
            mCommonProgressDialog.dismiss();
            mCommonProgressDialog = null;
        }
    }

    public static void showAlertDialog(Context context, String msg) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getResources().getString(R.string.app_name))
                .setMessage(msg)
                .setCancelable(false)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create().show();
    }


    public static void showLongSnackBar(View rootView, String msg) {
        Snackbar.make(rootView, msg, Snackbar.LENGTH_LONG).show();
    }

    public static void showShortSnackBar(View rootView, String msg){
        Snackbar.make(rootView, msg, Snackbar.LENGTH_SHORT).show();
    }

    public static void showLongToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    public static void showShortToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

}
