package com.liarr.communityservice.View.Activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.liarr.communityservice.R;
import com.liarr.communityservice.Util.AlertDialogUtil;
import com.liarr.communityservice.Util.InputMatcherUtil;
import com.liarr.communityservice.Util.UpdateUserInfoUtil;

public class EditPasswordActivity extends AppCompatActivity {

    Toolbar toolbar;
    AppCompatEditText oldPasswordEdit;
    AppCompatEditText newPasswordEdit;
    AppCompatEditText confirmNewPasswordEdit;
    AppCompatButton submitPasswordBtn;

    int prefUserId;
    String prefUserName;
    String prefTel;
    String prefPassword;
    int prefDoneEventNum;
    float prefPoint;
    int prefCoin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_password);

        toolbar = findViewById(R.id.edit_password_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        oldPasswordEdit = findViewById(R.id.old_password_edit);
        newPasswordEdit = findViewById(R.id.new_password_edit);
        confirmNewPasswordEdit = findViewById(R.id.confirm_new_password_edit);
        submitPasswordBtn = findViewById(R.id.submit_password_btn);
        submitPasswordBtn.setOnClickListener(v -> submitPassword());
    }

    private void submitPassword() {
        String oldPassword = oldPasswordEdit.getText().toString();
        String newPassword = newPasswordEdit.getText().toString();
        String confirmNewPassword = confirmNewPasswordEdit.getText().toString();
        getUserInfoFromSharedPreference();
        if (!oldPassword.equals(prefPassword)) {
            AlertDialogUtil.showMessageDialog(this, "原密码输入不正确");
        } else if (!newPassword.equals(confirmNewPassword)) {
            AlertDialogUtil.showMessageDialog(this, "新密码两次输入不匹配");
        } else if (!InputMatcherUtil.isPassword(confirmNewPassword)) {
            AlertDialogUtil.showUserInfoItemInputErrorDialog(this, "Password");     // 密码格式不正确
        } else if (confirmNewPassword.equals(oldPassword)) {
            AlertDialogUtil.showMessageDialog(this, "未进行任何修改");         // 两次输入密码相同
        } else {
            new Thread(() -> {
                if (UpdateUserInfoUtil.submitChangesToServer(EditPasswordActivity.this, prefUserId, prefUserName, prefTel, confirmNewPassword, prefDoneEventNum, prefPoint, prefCoin)) {
                    runOnUiThread(() -> {
                        Toast.makeText(EditPasswordActivity.this, "修改成功", Toast.LENGTH_LONG).show();
                        finish();
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(EditPasswordActivity.this, "修改失败", Toast.LENGTH_LONG).show());
                }
            }).start();
        }
    }

    /**
     * 从 SharedPreference 中获取用户信息
     */
    public void getUserInfoFromSharedPreference() {
        SharedPreferences preferences = getSharedPreferences("defaultUser", MODE_PRIVATE);
        prefUserId = preferences.getInt("userId", -1);
        prefUserName = preferences.getString("userName", "");
        prefPassword = preferences.getString("password", "");
        prefTel = preferences.getString("tel", "");
        prefDoneEventNum = preferences.getInt("doneEventNum", 0);
        prefPoint = preferences.getFloat("point", 0);
        prefCoin = preferences.getInt("coin", 0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}