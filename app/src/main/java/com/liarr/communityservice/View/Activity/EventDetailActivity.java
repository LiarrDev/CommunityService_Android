package com.liarr.communityservice.View.Activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.liarr.communityservice.Database.Event;
import com.liarr.communityservice.R;
import com.liarr.communityservice.Util.AlertDialogUtil;
import com.liarr.communityservice.Util.HttpRequestUrlUtil;
import com.liarr.communityservice.Util.LogUtil;
import com.liarr.communityservice.Util.NetworkTestUtil;
import com.liarr.communityservice.Util.ParseJsonUtil;
import com.liarr.communityservice.Util.QueryEventUtil;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class EventDetailActivity extends AppCompatActivity {

    Toolbar toolbar;
    AppCompatImageView channelImg;
    AppCompatTextView eventChannelText;
    AppCompatTextView eventTimeText;
    AppCompatTextView eventClientNameText;
    AppCompatTextView eventLocationText;
    AppCompatTextView eventAddressText;
    AppCompatTextView eventNameText;
    AppCompatTextView eventContentText;
    AppCompatTextView eventCoinText;
    AppCompatButton acceptEventBtn;

    int eid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        toolbar = findViewById(R.id.event_detail_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        eid = getIntent().getIntExtra("eid", -1);

        channelImg = findViewById(R.id.detail_channel_img);
        eventChannelText = findViewById(R.id.detail_event_channel);
        eventTimeText = findViewById(R.id.detail_event_time);
        eventClientNameText = findViewById(R.id.detail_event_client_name);
        eventLocationText = findViewById(R.id.detail_event_location);
        eventAddressText = findViewById(R.id.detail_event_address);
        eventNameText = findViewById(R.id.detail_event_name);
        eventContentText = findViewById(R.id.detail_event_content);
        eventCoinText = findViewById(R.id.detail_event_coin);
        acceptEventBtn = findViewById(R.id.detail_accept_event_btn);

        initDetail();
    }

    private void initDetail() {
        AlertDialogUtil.showProgressDialog(this);
        if (eid != -1) {

            new Thread(() -> {
                if (!NetworkTestUtil.isNetworkAvailable(this)) {
                    NetworkTestUtil.showNetworkDisableToast(this);
                }
                Event event = QueryEventUtil.queryUnacceptedEventWithEid(eid);
                if (event != null) {
                    String category = event.getCategory();
                    String eventName = event.getEventName();
                    String eventContent = event.getEventContent();
                    String eventTime = event.getEventTime();
                    String city = event.getCity();
                    String county = event.getCounty();
                    String street = event.getStreet();
                    String address = event.getAddress();
                    String clientName = event.getClientName();
                    int coin = event.getCoin();
                    runOnUiThread(() -> {
                        switch (category) {
                            case "医护":
                                channelImg.setImageResource(R.drawable.ic_nursing);
                                break;
                            case "跑腿":
                                channelImg.setImageResource(R.drawable.ic_legwork);
                                break;
                            case "清洁":
                                channelImg.setImageResource(R.drawable.ic_cleaning);
                                break;
                            case "教育":
                                channelImg.setImageResource(R.drawable.ic_education);
                                break;
                            case "餐饮":
                                channelImg.setImageResource(R.drawable.ic_repast);
                                break;
                            case "维修":
                                channelImg.setImageResource(R.drawable.ic_repair);
                                break;
                            default:
                        }
                        eventChannelText.setText(category);
                        eventTimeText.setText(eventTime);
                        eventClientNameText.setText(clientName);
                        eventLocationText.setText(city + " " + county + " " + street);
                        eventAddressText.setText(address);
                        eventNameText.setText(eventName);
                        eventContentText.setText(eventContent);
                        eventCoinText.setText(coin + "");

                        AlertDialogUtil.dismissProgressDialog();
                    });

                }
            }).start();

            acceptEventBtn.setOnClickListener(v -> AlertDialogUtil.showMakeSureToAcceptEventDialog(EventDetailActivity.this, eid));
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
