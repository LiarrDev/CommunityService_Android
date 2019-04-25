package com.liarr.communityservice.Util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import com.liarr.communityservice.R;
import com.liarr.communityservice.View.Activity.SignInActivity;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

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
     * 填写个人信息项格式不符合时弹出的 Dialog
     *
     * @param context Dialog 所在的 Context
     * @param item    对应的填写项
     */
    public static void showUserInfoItemInputErrorDialog(Context context, String item) {
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

    /**
     * 登记 Event 时选择对应的 Channel
     *
     * @param context  Dialog 所在的 Activity
     * @param editText 对应的 AppCompatEditText 控件
     */
    public static void showChoosingChannelByRegisterEvent(Context context, AppCompatEditText editText) {
        final String[] channelItems = {"医护", "跑腿", "清洁", "教育", "餐饮", "维修"};
        AlertDialog.Builder dialog = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
        dialog.setItems(channelItems, (dialog1, which) -> editText.setText(channelItems[which]));
        dialog.show();
    }

    /**
     * 登记 Event 时弹出的日期选择器 Date Picker
     *
     * @param context  Dialog 所在的 Activity
     * @param editText 对应的 AppCompatEditText 控件
     * @param minDay   Date Picker 显示的最小日期同时也是当天日期
     * @param nowYear  当前时间的年
     * @param nowMonth 当前时间的月
     * @param nowDay   当前时间的日
     */
    public static void showDatePickerByRegisterEvent(Context context, AppCompatEditText editText, long minDay, int nowYear, int nowMonth, int nowDay) {
        View datePickerDialog = LayoutInflater.from(context).inflate(R.layout.date_picker_dialog, null);
        DatePicker datePicker = datePickerDialog.findViewById(R.id.date_picker);
        datePicker.setMinDate(minDay);
        datePicker.init(nowYear, nowMonth, nowDay, (view, year, monthOfYear, dayOfMonth) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, monthOfYear, dayOfMonth);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            editText.setText(format.format(calendar.getTime()));
        });
        AlertDialog dialog = new AlertDialog.Builder(context).create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.setView(datePickerDialog);
        dialog.show();
        AppCompatButton dateSubmit = datePickerDialog.findViewById(R.id.date_submit);
        dateSubmit.setOnClickListener(v -> dialog.dismiss());
    }

    /**
     * 提交 Event 时如有未填写项则弹出此 Dialog
     *
     * @param context Dialog 所在的 Activity
     */
    public static void showSubmitEventError(Context context) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
        dialog.setTitle(R.string.app_name)
                .setMessage("填写未完成")
                .setCancelable(false)
                .setPositiveButton("OK", null)
                .show();
    }

    /**
     * 点击接单按钮时弹出的确认 Dialog
     *
     * @param context Dialog 所在 Activity
     * @param eid     Event ID
     */
    public static void showMakeSureToAcceptEventDialog(Context context, int eid) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
        builder.setTitle(R.string.app_name)
                .setMessage("您确定要接受此事件并为客户提供相应的服务？")
                .setCancelable(false)
                .setPositiveButton("OK", (dialogInterface, which) -> {
                    // 从 SharedPreference 中取出 UserId 和 UserName 并作为 AcceptId 和 AcceptName
                    SharedPreferences preferences = context.getSharedPreferences("defaultUser", MODE_PRIVATE);
                    int prefUserId = preferences.getInt("userId", -1);
                    String prefUserName = preferences.getString("userName", "");

                    new Thread(() -> {
                        try {
                            OkHttpClient client = new OkHttpClient();
                            RequestBody requestBody = new FormBody.Builder()
                                    .add("eid", String.valueOf(eid))
                                    .add("acceptId", String.valueOf(prefUserId))
                                    .add("acceptName", prefUserName)
                                    .build();
                            Request request = new Request.Builder()
                                    .url(HttpRequestUrlUtil.acceptEventUrl)
                                    .post(requestBody)
                                    .build();
                            Response response = client.newCall(request).execute();
                            String responseContent = response.body().string();
                            LogUtil.e("==AcceptEventRequest==", responseContent);
                            if (ParseJsonUtil.parseAcceptEventResponseJson(responseContent)) {
                                Activity activity = (Activity) context;
                                activity.runOnUiThread(() -> {
                                    Toast.makeText(context, "接单成功", Toast.LENGTH_LONG).show();
                                    activity.finish();
                                });
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }).start();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * 修改信息时因填写的内容与原文相同或未产生修改时弹出的 Dialog
     *
     * @param context Dialog 所在的 Activity
     */
    public static void showInputNotChangeDialog(Context context) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
        dialog.setTitle(R.string.app_name)
                .setMessage("未进行任何修改")
                .setCancelable(false)
                .setPositiveButton("OK", null)
                .show();
    }

    /**
     * 修改密码时原密码输入错误
     *
     * @param context Dialog 所在 Activity
     */
    public static void showOldPasswordInputErrorDialog(Context context) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
        dialog.setTitle(R.string.app_name)
                .setMessage("原密码输入不正确")
                .setCancelable(false)
                .setPositiveButton("OK", null)
                .show();
    }

    /**
     * 修改密码时新密码两次输入不匹配
     *
     * @param context Dialog 所在 Activity
     */
    public static void showNewPasswordNotConfirmDialog(Context context) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
        dialog.setTitle(R.string.app_name)
                .setMessage("新密码两次输入不匹配")
                .setCancelable(false)
                .setPositiveButton("OK", null)
                .show();
    }
}
