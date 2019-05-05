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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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

    String mProvince;
    String mCity;
    String mCounty;

    String city;
    String county;

    int year;
    int month;
    int day;

    int eid;

    int userId;
    String userName;
    String action = null;       // 当前状态为添加还是修改

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_event);

        userId = getIntent().getIntExtra("userId", -1);
        userName = getIntent().getStringExtra("userName");
        action = getIntent().getAction();
        LogUtil.e("==RegisterEventAction==", action);

        toolbar = findViewById(R.id.register_event_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        eventChannelEdit = findViewById(R.id.event_channel_edit);
        eventNameEdit = findViewById(R.id.event_name_edit);
        eventContentEdit = findViewById(R.id.event_content_edit);
        eventTimeEdit = findViewById(R.id.event_time_edit);
        eventLocationEdit = findViewById(R.id.event_location_edit);
        eventStreetEdit = findViewById(R.id.event_street_edit);
        eventAddressEdit = findViewById(R.id.event_address_edit);
        submitEventBtn = findViewById(R.id.submit_event_btn);

        Calendar calendar = Calendar.getInstance();
        long minDay = calendar.getTimeInMillis();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.set(year, month, day);

        // TODO: 把所有文件的 ACTION 从 extra 改成 setAction
        if (action.equals("update_event")) {
            eid = getIntent().getIntExtra("eid", -1);
            eventChannel = getIntent().getStringExtra("category");
            eventName = getIntent().getStringExtra("eventName");
            eventContent = getIntent().getStringExtra("eventContent");
            eventTime = getIntent().getStringExtra("eventTime");
            city = getIntent().getStringExtra("city");
            county = getIntent().getStringExtra("county");
            eventStreet = getIntent().getStringExtra("street");
            eventAddress = getIntent().getStringExtra("address");

            eventChannelEdit.setText(eventChannel);
            eventNameEdit.setText(eventName);
            eventContentEdit.setText(eventContent);
            eventTimeEdit.setText(eventTime);
            eventLocationEdit.setText(city + " " + county);
            eventStreetEdit.setText(eventStreet);
            eventAddressEdit.setText(eventAddress);

            // 把获取过来的 City 和 County 存到 SharedPreference
            SharedPreferences.Editor editor = getSharedPreferences("add_event_location", MODE_PRIVATE).edit();
            editor.putString("city", city);
            editor.putString("county", county);
            editor.apply();

        } else if (action.equals("new_event")) {
            eventTimeEdit.setText(formatter.format(calendar.getTime()));
        }


        eventChannelEdit.setOnClickListener(v -> AlertDialogUtil.showChoosingChannelByRegisterEvent(RegisterEventActivity.this, eventChannelEdit));

        eventTimeEdit.setOnClickListener(v -> {
            if (eventTimeEdit.getText().toString().compareTo(formatter.format(calendar.getTime())) < 0) {       // 填写的时间小于当前时间
                eventTimeEdit.setText(formatter.format(calendar.getTime()));                // 设置为当前时间
            } else {
                try {
                    Date date = formatter.parse(eventTimeEdit.getText().toString());        // 把填写的时间转为 Date 格式
                    calendar.setTime(date);                                                 // 把 Date 格式的时间转为 Calendar 格式
                    year = calendar.get(Calendar.YEAR);
                    month = calendar.get(Calendar.MONTH);
                    day = calendar.get(Calendar.DAY_OF_MONTH);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            AlertDialogUtil.showDatePickerByRegisterEvent(RegisterEventActivity.this, eventTimeEdit, minDay, year, month, day);
        });

        eventLocationEdit.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterEventActivity.this, LocationActivity.class);
            intent.setAction(SETTING_ADD_EVENT_LOCATION);
            startActivity(intent);
        });

        submitEventBtn.setOnClickListener(v -> submitRegisterEvent());

    }

    @Override
    protected void onStart() {
        super.onStart();

        // 从 SharedPreference 中取出省市县
        SharedPreferences preferences = getSharedPreferences("add_event_location", MODE_PRIVATE);
        mProvince = preferences.getString("province", "");
        mCity = preferences.getString("city", "");
        mCounty = preferences.getString("county", "");
        if (!TextUtils.isEmpty(mCity.trim()) && !TextUtils.isEmpty(mCounty.trim())) {
            eventLocation = mProvince + " " + mCity + " " + mCounty;
            eventLocationEdit.setText(eventLocation);
        }
        // 清除 SharedPreference 中保存的省市县
        preferences.edit().clear().apply();
    }

    /**
     * 提交填写的 Event
     */
    private void submitRegisterEvent() {
        boolean fillFlag = true;        // 是否填写完成
        boolean updateFlag = true;      // 是否修改

        String mEventChannel = eventChannelEdit.getText().toString();
        String mEventName = eventNameEdit.getText().toString();
        String mEventContent = eventContentEdit.getText().toString();
        String mEventTime = eventTimeEdit.getText().toString();
        String mEventLocation = eventLocationEdit.getText().toString();
        String mEventStreet = eventStreetEdit.getText().toString();
        String mEventAddress = eventAddressEdit.getText().toString();

        if (mEventChannel.equals("点击选择")) {
            fillFlag = false;
            LogUtil.e("==SubmitEventFlag==", "Channel");
        } else if (TextUtils.isEmpty(mEventName.trim())) {
            fillFlag = false;
            LogUtil.e("==SubmitEventFlag==", "Name");
        } else if (TextUtils.isEmpty(mEventContent.trim())) {
            fillFlag = false;
            LogUtil.e("==SubmitEventFlag==", "Content");
        } else if (mEventLocation.equals("点击选择")) {
            fillFlag = false;
            LogUtil.e("==SubmitEventFlag==", "Location");
        } else if (TextUtils.isEmpty(mEventStreet.trim())) {
            fillFlag = false;
            LogUtil.e("==SubmitEventFlag==", "Street");
        } else if (TextUtils.isEmpty(mEventAddress.trim())) {
            fillFlag = false;
            LogUtil.e("==SubmitEventFlag==", "Address");
        } else if (action.equals("update_event")) {
            if (mEventChannel.equals(eventChannel) && mEventName.equals(eventName) && mEventContent.equals(eventContent) && mEventTime.equals(eventTime) && mCity.equals(city) && mCounty.equals(county) && mEventStreet.equals(eventStreet) && mEventAddress.equals(eventAddress)) {
                updateFlag = false;
            }
        }
        LogUtil.e("==SubmitEventFlag==", fillFlag + "");
        LogUtil.e("==UpdateEventFlag==", updateFlag + "");

        if (fillFlag) {
            if (updateFlag) {
                new Thread(() -> {
                    try {
                        OkHttpClient client = new OkHttpClient();
                        RequestBody requestBody;
                        Request request = null;
                        if (action.equals("new_event")) {
                            requestBody = new FormBody.Builder()
                                    .add("event.category", mEventChannel)
                                    .add("event.eventName", mEventName)
                                    .add("event.eventContent", mEventContent)
                                    .add("event.eventTime", mEventTime)
                                    .add("event.city", mCity)
                                    .add("event.district", mCounty)
                                    .add("event.street", mEventStreet)
                                    .add("event.address", mEventAddress)
                                    .add("event.clientId", String.valueOf(userId))
                                    .add("event.clientName", userName)
                                    .build();
                            request = new Request.Builder()
                                    .url(HttpRequestUrlUtil.registerEventUrl)
                                    .post(requestBody)
                                    .build();
                        } else if (action.equals("update_event")) {
                            requestBody = new FormBody.Builder()
                                    .add("event.eid", String.valueOf(eid))
                                    .add("event.category", mEventChannel)
                                    .add("event.eventName", mEventName)
                                    .add("event.eventContent", mEventContent)
                                    .add("event.eventTime", mEventTime)
                                    .add("event.city", mCity)
                                    .add("event.district", mCounty)
                                    .add("event.street", mEventStreet)
                                    .add("event.address", mEventAddress)
                                    .add("event.clientId", String.valueOf(userId))
                                    .add("event.clientName", userName)
                                    .build();
                            request = new Request.Builder()
                                    .url(HttpRequestUrlUtil.updateEventUrl)
                                    .post(requestBody)
                                    .build();
                        }
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
                AlertDialogUtil.showInputNotChangeDialog(this);
            }
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
