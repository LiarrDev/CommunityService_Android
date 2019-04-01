package com.liarr.communityservice.Util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import com.liarr.communityservice.R;
import com.liarr.communityservice.View.Activity.SignInActivity;

public class AlertDialogUtil {

    private static AlertDialog progressDialog;

    /**
     * 弹出加载中的 ProgressDialog
     *
     * @param context Dialog 所在的 Context
     */
    public static void showProgressDialog(Context context) {
        View progressBar = LayoutInflater.from(context).inflate(R.layout.progress_dialog, null);
        progressDialog = new AlertDialog.Builder(context).create();
        progressDialog.setView(progressBar);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    /**
     * 隐藏加载中的 ProgressDialog
     */
    public static void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    /**
     * 注册时填写项格式不符合时弹出的 Dialog
     *
     * @param context Dialog 所在的 Context
     * @param item    对应的填写项
     */
    public static void showSignUpItemInputErrorDialog(Context context, String item) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
        dialog.setTitle(R.string.app_name)
                .setMessage("It seems not like a right " + item + ". Please check again.")
                .setCancelable(false)
                .setPositiveButton("OK", null)
                .show();
    }

    /**
     * 根据服务器响应注册请求返回的 Code 弹出不同的 Dialog
     *
     * @param context      Dialog 所在的 Activity
     * @param responseCode 服务器响应的 Code
     */
    public static void showSignUpResponseDialog(Context context, String responseCode) {
        dismissProgressDialog();
        AlertDialog.Builder dialog = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
        dialog.setTitle(R.string.app_name).setCancelable(false);
        if (responseCode.equals("0")) {
            LogUtil.e("==ResponseCode==", "0");
            dialog.setMessage("Sign Up Succeed. Please Sign In.").setPositiveButton("OK", (dialogInterface, which) -> {
                Intent intent = new Intent(context, SignInActivity.class);
                context.startActivity(intent);
                Activity activity = (Activity) context;
                activity.finish();
            });
        } else if (responseCode.equals("1")) {
            LogUtil.e("==ResponseCode==", "1");
            dialog.setMessage("Sign Up Failed. Please Try Again.").setPositiveButton("OK", null);
        }
        dialog.show();
    }

    /**
     * 登录填写 Tel 或 Password 错误时弹出的 Dialog
     *
     * @param context Dialog 所在的 Activity
     */
    public static void showSignInItemInputErrorDialog(Context context) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
        dialog.setTitle(R.string.app_name)
                .setMessage("Your Tel or Password must be wrong. Please check again.")
                .setCancelable(false)
                .setPositiveButton("OK", null)
                .show();
    }
}
