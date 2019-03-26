package com.liarr.communityservice.View.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.text.Html;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.liarr.communityservice.R;
import com.liarr.communityservice.Util.HttpRequestUrlUtil;
import com.liarr.communityservice.Util.InputMatcherUtil;
import com.liarr.communityservice.Util.LogUtil;
import com.liarr.communityservice.Util.ParseJsonUtil;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignInActivity extends AppCompatActivity {

    TextView signUpLink;
    EditText telEdit;
    EditText passwordEdit;
    AppCompatButton signInBtn;

    String tel;
    String password;
    int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // 查取登录信息自动登录
        SharedPreferences preferences = getSharedPreferences("defaultUser", MODE_PRIVATE);
        String prefUserTel = preferences.getString("tel", "");
        String prefUserPassword = preferences.getString("password", "");
        if (prefUserTel == null || prefUserTel.equals("")) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            editor.apply();
        } else {
            submitSignInForm(prefUserTel, prefUserPassword);        // 用保存的登录信息进行登录
        }

        signUpLink = (TextView) findViewById(R.id.sign_up_link);
        signUpLink.setText(Html.fromHtml("No account yet? <font color='#FFFFFF'><big>Create one</big></font>."));
        signUpLink.setOnClickListener(v -> {
            Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
            startActivity(intent);
            finish();
        });

        telEdit = (EditText) findViewById(R.id.tel_edit);
        passwordEdit = (EditText) findViewById(R.id.password_edit);
        signInBtn = (AppCompatButton) findViewById(R.id.sign_in_btn);
        signInBtn.setOnClickListener(v -> {
            tel = telEdit.getText().toString();
            password = passwordEdit.getText().toString();
            if (!InputMatcherUtil.isTel(tel) || !InputMatcherUtil.isPassword(password)) {
                LogUtil.i("==SignInTel==", tel);
                LogUtil.i("==SignInPassword==", password);
                showInputErrorAlert();
            } else {
                submitSignInForm(tel, password);
            }
        });
    }

    /**
     * 向服务器提交用户填写的信息
     * @param tel 手机号码
     * @param password 密码
     */
    private void submitSignInForm(final String tel, final String password) {
        new Thread(() -> {
            try {
                OkHttpClient client = new OkHttpClient();
                RequestBody requestBody = new FormBody.Builder()
                        .add("user.tel", tel)
                        .add("user.password", password)
                        .build();
                Request request = new Request.Builder()
                        .url(HttpRequestUrlUtil.signInUrl)
                        .post(requestBody)
                        .build();
                Response response = client.newCall(request).execute();
                String responseContent = response.body().string();
                LogUtil.e("==SignInJSON==", responseContent);

                if (ParseJsonUtil.parseSignUpOrSignInStateJson(responseContent)) {      // Msg 为 success，登录成功

                    // 解析得到 uid 并存储
                    userId = ParseJsonUtil.parseUserIdJson(responseContent);
                    LogUtil.e("===UserID===", userId + "");

                    // 用 SharedPreferences 保存登录状态
                    SharedPreferences.Editor editor = getSharedPreferences("defaultUser", MODE_PRIVATE).edit();
                    editor.putString("tel", tel);
                    editor.putString("password", password);
                    editor.putInt("userId", userId);
                    editor.apply();

                    Intent intent = new Intent(this, MainActivity.class);
                    intent.putExtra("userId", userId);
                    startActivity(intent);
                    finish();
                } else {
                    // TODO: 登录失败
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * 输入 Tel 或 Password 错误弹出提示框
     */
    private void showInputErrorAlert() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(R.string.app_name)
                .setMessage("Your Tel or Password must be wrong. Please check again.")
                .setCancelable(false)
                .setPositiveButton("OK", null)
                .show();
    }
}
