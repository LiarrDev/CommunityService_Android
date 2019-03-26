package com.liarr.communityservice.View.Activity;

import android.content.DialogInterface;
import android.content.Intent;
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

public class SignUpActivity extends AppCompatActivity {

    TextView signInLink;
    EditText nameEdit;
    EditText telEdit;
    EditText passwordEdit;
    AppCompatButton signUpBtn;

    String name;
    String tel;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        signInLink = (TextView) findViewById(R.id.sign_in_link);
        signInLink.setText(Html.fromHtml("No account yet? <font color='#FFFFFF'><big>Create one</big></font>."));
        signInLink.setOnClickListener(v -> {
            Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
            startActivity(intent);
            finish();
        });

        nameEdit = (EditText) findViewById(R.id.name_edit);
        telEdit = (EditText) findViewById(R.id.tel_edit);
        passwordEdit = (EditText) findViewById(R.id.password_edit);
        signUpBtn = (AppCompatButton) findViewById(R.id.sign_up_btn);
        signUpBtn.setOnClickListener(v -> {
            name = nameEdit.getText().toString();
            tel = telEdit.getText().toString();
            password = passwordEdit.getText().toString();

            if (!InputMatcherUtil.isName(name)) {
                showInputErrorAlert("Name");
            } else if (!InputMatcherUtil.isTel(tel)) {
                showInputErrorAlert("Tel");
            } else if (!InputMatcherUtil.isPassword(password)) {
                showInputErrorAlert("Password");
            } else {
                submitSignUpForm(name, tel, password);
            }
        });
    }

    private void submitSignUpForm(String name, String tel, String password) {
        new Thread(() -> {
            try {
                OkHttpClient client = new OkHttpClient();
                RequestBody requestBody = new FormBody.Builder()
                        .add("user.userName", name)
                        .add("user.password", password)
                        .add("user.tel", tel)
                        .build();
                Request request = new Request.Builder()
                        .url(HttpRequestUrlUtil.signUpUrl)
                        .post(requestBody)
                        .build();
                Response response = client.newCall(request).execute();
                String responseContent = response.body().string();
                LogUtil.e("==SignUpJSON==", responseContent);

                if (ParseJsonUtil.parseSignUpOrSignInStateJson(responseContent)) {      // Msg 为 success，注册成功
                    runOnUiThread(this::showSignInSucceedAlert);
                } else {
                    // TODO: 登陆失败弹出对应对话框，可以复用成功的对话框
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void showSignInSucceedAlert() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(R.string.app_name)
                .setMessage("Sign Up Succeed. Please Sign In.")
                .setCancelable(false)
                .setPositiveButton("OK", (signInDialog, which) -> {
                    Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                    startActivity(intent);
                    finish();
                })
                .show();
    }

    private void showInputErrorAlert(String item) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(R.string.app_name)
                .setMessage("It seems not like a right " + item + ". Please check again.")
                .setCancelable(false)
                .setPositiveButton("OK", null)
                .show();
    }
}
