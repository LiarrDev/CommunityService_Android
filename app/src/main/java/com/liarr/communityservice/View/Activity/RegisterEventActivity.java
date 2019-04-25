package com.liarr.communityservice.View.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Toast;

import com.liarr.communityservice.R;
import com.liarr.communityservice.Util.AlertDialogUtil;
import com.liarr.communityservice.Util.HttpRequestUrlUtil;
import com.liarr.communityservice.Util.LogUtil;
import com.liarr.communityservice.Util.ParseJsonUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.liarr.communityservice.View.Fragment.ChooseAreaFragment.SETTING_ADD_EVENT_LOCATION;

public class RegisterEventActivity extends AppCompatActivity {

    Toolbar toolbar;
    AppCompatEditText eventChannelEdit;
    AppCompatEditText eventNameEdit;
    AppCompatEditText eventContentEdit;
    AppCompatEditText eventTimeEdit;
    AppCompatEditText eventLocationEdit;
    AppCompatEditText eventStreetEdit;
    AppCompatEditText eventAddressEdit;
    AppCompatButton submitEventBtn;

    String eventChannel;
    String eventName;
    String eventContent;
    String eventTime;
    String eventLocation;
    String eventStreet;
    String eventAddress;

    String province;
    String city;
    String county;

    int userId;
    String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_event);

        userId = getIntent().getIntExtra("userId", -1);
        userName = getIntent().getStringExtra("userName");

        toolbar = findViewById(R.id.register_event_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        eventChannelEdit = findViewById(R.id.event_channel_edit);
        eventChannelEdit.setOnClickListener(v -> AlertDialogUtil.showChoosingChannelByRegisterEvent(RegisterEventActivity.this, eventChannelEdit));

        eventNameEdit = findViewById(R.id.event_name_edit);

        eventContentEdit = findViewById(R.id.event_content_edit);

        Calendar calendar = Calendar.getInstance();
        int nowYear = calendar.get(Calendar.YEAR);
        int nowMonth = calendar.get(Calendar.MONTH);
        int nowDay = calendar.get(Calendar.DAY_OF_MONTH);
        long minDay = calendar.getTimeInMillis();
        eventTimeEdit = findViewById(R.id.event_time_edit);
        calendar.set(nowYear, nowMonth, nowDay);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        eventTimeEdit.setText(format.format(calendar.getTime()));
        eventTimeEdit.setOnClickListener(v -> AlertDialogUtil.showDatePickerByRegisterEvent(RegisterEventActivity.this, eventTimeEdit, minDay, nowYear, nowMonth, nowDay));

        eventLocationEdit = findViewById(R.id.event_location_edit);
        eventLocationEdit.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterEventActivity.this, LocationActivity.class);
            intent.putExtra("action", SETTING_ADD_EVENT_LOCATION);
            startActivity(intent);
        });

        eventStreetEdit = findViewById(R.id.event_street_edit);

        eventAddressEdit = findViewById(R.id.event_address_edit);

        submitEventBtn = findViewById(R.id.submit_event_btn);
        submitEventBtn.setOnClickListener(v -> submitRegisterEvent());
    }

    @Override
    protected void onStart() {
        super.onStart();

        // 从 SharedPreference 中取出省市县
        SharedPreferences preferences = getSharedPreferences("add_event_location", MODE_PRIVATE);
        province = preferences.getString("province", "");
        city = preferences.getString("city", "");
        county = preferences.getString("county", "");
        if (!TextUtils.isEmpty(province.trim()) && !TextUtils.isEmpty(city.trim()) && !TextUtils.isEmpty(county.trim())) {
            eventLocation = province + " " + city + " " + county;
            eventLocationEdit.setText(eventLocation);
        }
        // 清除 SharedPreference 中保存的省市县
        preferences.edit().clear().apply();
    }

    /**
     * 提交填写的 Event
     */
    private void submitRegisterEvent() {
        boolean flag = true;

        eventChannel = eventChannelEdit.getText().toString();
        eventName = eventNameEdit.getText().toString();
        eventContent = eventContentEdit.getText().toString();
        eventTime = eventTimeEdit.getText().toString();
        eventLocation = eventLocationEdit.getText().toString();
        eventStreet = eventStreetEdit.getText().toString();
        eventAddress = eventAddressEdit.getText().toString();

        if (eventChannel.equals("点击选择")) {
            flag = false;
            LogUtil.e("==SubmitEventFlag==", "Channel");
        } else if (TextUtils.isEmpty(eventName.trim())) {
            flag = false;
            LogUtil.e("==SubmitEventFlag==", "Name");
        } else if (TextUtils.isEmpty(eventContent.trim())) {
            flag = false;
            LogUtil.e("==SubmitEventFlag==", "Content");
        } else if (eventLocation.equals("点击选择")) {
            flag = false;
            LogUtil.e("==SubmitEventFlag==", "Location");
        } else if (TextUtils.isEmpty(eventStreet.trim())) {
            flag = false;
            LogUtil.e("==SubmitEventFlag==", "Street");
        } else if (TextUtils.isEmpty(eventAddress.trim())) {
            flag = false;
            LogUtil.e("==SubmitEventFlag==", "Address");
        }
        LogUtil.e("==SubmitEventFlag==", flag + "");

        if (flag) {
            new Thread(() -> {
                try {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder()
                            .add("event.category", eventChannel)
                            .add("event.eventName", eventName)
                            .add("event.eventContent", eventContent)
                            .add("event.eventTime", eventTime)
                            .add("event.city", city)
                            .add("event.district", county)
                            .add("event.street", eventStreet)
                            .add("event.address", eventAddress)
                            .add("event.clientId", String.valueOf(userId))
                            .add("event.clientName", userName)
                            .build();
                    Request request = new Request.Builder()
                            .url(HttpRequestUrlUtil.registerEventUrl)
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseContent = response.body().string();
                    LogUtil.e("==SubmitEventResponseJson==", responseContent);
                    runOnUiThread(() -> {
                        if (ParseJsonUtil.parseSubmitEventResponseCodeJson(responseContent).equals("0")) {      // Code 为 0，提交成功
                            Toast.makeText(RegisterEventActivity.this, "提交成功", Toast.LENGTH_LONG).show();
                            LogUtil.e("==RegisterEvent==", "Succeed");
                            finish();
                        } else {
                            Toast.makeText(RegisterEventActivity.this, "提交失败", Toast.LENGTH_LONG).show();
                            LogUtil.e("==RegisterEvent==", "Failed");
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        } else {
            AlertDialogUtil.showSubmitEventError(this);
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
