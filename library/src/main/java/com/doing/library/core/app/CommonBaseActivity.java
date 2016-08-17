package com.doing.library.core.app;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Toast;

import com.doing.library.R;

/**
    //代替繁杂的findViewById
     protected  <T extends View> T $(int resId)

    // progress
     protected void showCommonProgressDialog(String msg)
     protected void dismissCommonProgressDialog()
     public void showProgressIndicator()
     public void hideProgressIndicator()

     //普通提醒dialog
     protected void showCommonAlertDialog(String msg)

     //Toast
     protected void showCommonLongSnackBar(View rootView, String msg)
     protected void showCommonShortSnackBar(View rootView, String msg)
     protected void showCommonLongToast(String msg)
     protected void showCommonShortToast(String msg)

     //ActionBar相关
     public void setActionBarTitle(CharSequence text)
     public void setActionBarTitle(int resId)
     public void setActionBarSubtitle(CharSequence text)
     public void setActionBarSubtitle(int resId)

 */

public class CommonBaseActivity extends AppCompatActivity
{
    private static final String BASE_TAG = CommonBaseActivity.class.getSimpleName();

    private ProgressDialog mCommonProgressDialog;

    /**
     *    findViewById
     *
     *    使用示例：
     *    ibTakePicture = $(R.id.ib_camera_take_picture);
     */
    protected  <T extends View> T $(int resId) {
        return (T) super.findViewById(resId);
    }


    //旋转圆形 progress dialog
    protected void showCommonProgressDialog(String msg) {
        if (mCommonProgressDialog != null && mCommonProgressDialog.isShowing())
            dismissCommonProgressDialog();

        mCommonProgressDialog = ProgressDialog.show(this, getResources().getString(R.string.app_name), msg);
    }

    protected void dismissCommonProgressDialog() {
        if (mCommonProgressDialog != null) {
            mCommonProgressDialog.dismiss();
            mCommonProgressDialog = null;
        }
    }

    protected void showCommonAlertDialog(String msg) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.app_name))
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


    protected void showCommonLongSnackBar(View rootView, String msg) {
        Snackbar.make(rootView, msg, Snackbar.LENGTH_LONG).show();
    }

    protected void showCommonShortSnackBar(View rootView, String msg){
        Snackbar.make(rootView, msg, Snackbar.LENGTH_SHORT).show();
    }

    protected void showCommonLongToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    protected void showCommonShortToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }


    public void showProgressIndicator() {
        setProgressBarIndeterminateVisibility(true);
    }

    public void hideProgressIndicator() {
        setProgressBarIndeterminateVisibility(false);
    }

    public void setActionBarTitle(CharSequence text) {
        getActionBar().setTitle(text);
    }

    public void setActionBarTitle(int resId) {
        getActionBar().setTitle(resId);
    }

    public void setActionBarSubtitle(CharSequence text) {
        getActionBar().setSubtitle(text);
    }

    public void setActionBarSubtitle(int resId) {
        getActionBar().setSubtitle(resId);
    }

}
