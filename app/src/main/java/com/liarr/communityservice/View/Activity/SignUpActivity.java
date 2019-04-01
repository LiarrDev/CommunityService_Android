package com.liarr.communityservice.View.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.text.Html;
import android.widget.EditText;
import android.widget.TextView;

import com.liarr.communityservice.R;
import com.liarr.communityservice.Util.AlertDialogUtil;
import com.liarr.communityservice.Util.HttpRequestUrlUtil;
import com.liarr.communityservice.Util.InputMatcherUtil;
import com.liarr.communityservice.Util.LogUtil;
import com.liarr.communityservice.Util.NetworkTestUtil;
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
                AlertDialogUtil.showSignUpItemInputErrorDialog(this, "Name");
            } else if (!InputMatcherUtil.isTel(tel)) {
                AlertDialogUtil.showSignUpItemInputErrorDialog(this, "Tel");
            } else if (!InputMatcherUtil.isPassword(password)) {
                AlertDialogUtil.showSignUpItemInputErrorDialog(this, "Password");
            } else {
                submitSignUpForm(name, tel, password);
            }
        });
    }

    /**
     * 提交注册的表单
     *
     * @param name     用户名字
     * @param tel      手机号码
     * @param password 密码
     */
    private void submitSignUpForm(String name, String tel, String password) {

        AlertDialogUtil.showProgressDialog(this);

        new Thread(() -> {
            if (!NetworkTestUtil.isNetworkAvailable(this)) {        // 网络异常，弹出提示
                NetworkTestUtil.showNetworkDisableToast(this);
            }
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
                runOnUiThread(() -> AlertDialogUtil.showSignUpResponseDialog(SignUpActivity.this, ParseJsonUtil.parseSignUpOrSignInCodeJson(responseContent)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
