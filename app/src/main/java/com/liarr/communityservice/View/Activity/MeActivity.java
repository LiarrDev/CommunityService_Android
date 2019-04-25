package com.liarr.communityservice.View.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.liarr.communityservice.R;
import com.liarr.communityservice.Util.AlertDialogUtil;
import com.liarr.communityservice.Util.InputMatcherUtil;
import com.liarr.communityservice.Util.LogUtil;
import com.liarr.communityservice.Util.UpdateUserInfoUtil;

public class MeActivity extends AppCompatActivity {

    Toolbar toolbar;

    AppCompatEditText userNameEdit;
    AppCompatImageButton editNameBtn;
    AppCompatImageButton editNameDoneBtn;
    AppCompatEditText userTelEdit;
    AppCompatImageButton editTelBtn;
    AppCompatImageButton editTelDoneBtn;
    AppCompatEditText userPasswordEdit;
    AppCompatImageButton editPasswordBtn;
    AppCompatTextView doneEventNumText;
    AppCompatTextView pointText;
    AppCompatTextView coinText;

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
        setContentView(R.layout.activity_me);

        toolbar = findViewById(R.id.me_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        initUserInfo();
    }

    private void initUserInfo() {

        getUserInfoFromSharedPreference();

        userNameEdit = findViewById(R.id.user_name_edit);
        userNameEdit.setText(prefUserName);

        editNameBtn = findViewById(R.id.edit_name_btn);
        editNameDoneBtn = findViewById(R.id.edit_name_done_btn);
        editNameBtn.setOnClickListener(v -> {
            userNameEdit.setEnabled(true);
            editNameBtn.setVisibility(View.GONE);               // 把修改的按钮设置为不可见
            editNameDoneBtn.setVisibility(View.VISIBLE);        // 把提交的按钮设置为可见
        });
        editNameDoneBtn.setOnClickListener(v -> {
            getUserInfoFromSharedPreference();              // 这里再次获取是为了防止二次修改时全局变量未更新
            LogUtil.e("==EditName==", userNameEdit.getText().toString());
            submitItemChanges("name", userNameEdit.getText().toString());
        });

        userTelEdit = findViewById(R.id.user_tel_edit);
        userTelEdit.setText(prefTel);

        editTelBtn = findViewById(R.id.edit_tel_btn);
        editTelDoneBtn = findViewById(R.id.edit_tel_done_btn);
        editTelBtn.setOnClickListener(v -> {
            userTelEdit.setEnabled(true);
            editTelBtn.setVisibility(View.GONE);
            editTelDoneBtn.setVisibility(View.VISIBLE);
        });
        editTelDoneBtn.setOnClickListener(v -> {
            getUserInfoFromSharedPreference();              // 这里再次获取是为了防止二次修改时全局变量未更新
            submitItemChanges("tel", userTelEdit.getText().toString());
        });

        userPasswordEdit = findViewById(R.id.user_password_edit);
        userPasswordEdit.setText(prefPassword);

        editPasswordBtn = findViewById(R.id.edit_password_btn);
        editPasswordBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MeActivity.this, EditPasswordActivity.class);
            startActivity(intent);
        });

        doneEventNumText = findViewById(R.id.done_event_num_text);
        doneEventNumText.setText(String.valueOf(prefDoneEventNum));

        pointText = findViewById(R.id.point_text);
        pointText.setText(String.valueOf(prefPoint));

        coinText = findViewById(R.id.coin_text);
        coinText.setText(String.valueOf(prefCoin));
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

    /**
     * 提交修改
     *
     * @param item     修改的项
     * @param newValue 修改后的值
     */
    private void submitItemChanges(String item, String newValue) {
        if (item.equals("name")) {
            if (newValue.equals(prefUserName)) {        // 未产生修改
                AlertDialogUtil.showInputNotChangeDialog(this);
            } else if (!InputMatcherUtil.isName(newValue)) {        // 不是正确的 Name
                AlertDialogUtil.showUserInfoItemInputErrorDialog(this, "Name");
            } else {
                new Thread(() -> {
                    if (UpdateUserInfoUtil.submitChangesToServer(this, prefUserId, newValue, prefTel, prefPassword, prefDoneEventNum, prefPoint, prefCoin)) {

                        runOnUiThread(() -> {
                            Toast.makeText(MeActivity.this, "修改成功", Toast.LENGTH_LONG).show();
                            userNameEdit.setEnabled(false);
                            editNameBtn.setVisibility(View.VISIBLE);                // 把修改的按钮设置为可见
                            editNameDoneBtn.setVisibility(View.GONE);               // 把提交的按钮设置为可见
                        });
                    } else {
                        runOnUiThread(() -> Toast.makeText(MeActivity.this, "修改失败", Toast.LENGTH_LONG).show());
                    }
                }).start();
            }
        } else if (item.equals("tel")) {
            if (newValue.equals(prefTel)) {
                AlertDialogUtil.showInputNotChangeDialog(this);
            } else if (!InputMatcherUtil.isTel(newValue)) {
                AlertDialogUtil.showUserInfoItemInputErrorDialog(this, "Tel");
            } else {
                new Thread(() -> {
                    if (UpdateUserInfoUtil.submitChangesToServer(this, prefUserId, prefUserName, newValue, prefPassword, prefDoneEventNum, prefPoint, prefCoin)) {
                        runOnUiThread(() -> {
                            Toast.makeText(MeActivity.this, "修改成功", Toast.LENGTH_LONG).show();
                            userTelEdit.setEnabled(false);
                            editTelBtn.setVisibility(View.VISIBLE);                 // 把修改的按钮设置为可见
                            editTelDoneBtn.setVisibility(View.GONE);                // 把提交的按钮设置为可见
                        });
                    }
                }).start();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
