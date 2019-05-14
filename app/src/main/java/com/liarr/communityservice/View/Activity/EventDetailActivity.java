package com.liarr.communityservice.View.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.card.MaterialCardView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.liarr.communityservice.Database.Event;
import com.liarr.communityservice.R;
import com.liarr.communityservice.Util.AlertDialogUtil;
import com.liarr.communityservice.Util.EventStatusUtil;
import com.liarr.communityservice.Util.LogUtil;
import com.liarr.communityservice.Util.NetworkTestUtil;
import com.liarr.communityservice.Util.QueryEventUtil;

public class EventDetailActivity extends AppCompatActivity {

    Toolbar toolbar;
    AppCompatImageView channelImg;
    AppCompatTextView eventChannelText;
    AppCompatTextView eventTimeText;
    AppCompatTextView eventClientNameText;
    LinearLayout eventAccepterView;
    AppCompatTextView eventAccepterNameText;
    AppCompatTextView eventLocationText;
    AppCompatTextView eventAddressText;
    AppCompatTextView eventNameText;
    AppCompatTextView eventContentText;
    AppCompatTextView eventCoinText;
    AppCompatButton resetEventStatusBtn;

    MaterialCardView eventCommentView;
    AppCompatRatingBar ratingBar;
    AppCompatTextView eventCommentText;

    int eid;
    int userId;
    String userName;
    String queryEventStatus;

    String category;
    String eventName;
    String eventContent;
    String eventTime;
    String city;
    String county;
    String street;
    String address;
    int coin;
    int clientId;
    String clientName = null;
    int accepterId;
    String accepterName = null;
    int point;
    String comment;

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

        SharedPreferences preferences = getSharedPreferences("defaultUser", MODE_PRIVATE);
        userId = preferences.getInt("userId", -1);
        userName = preferences.getString("userName", "");
        queryEventStatus = preferences.getString("queryEventStatus", "");

        eid = getIntent().getIntExtra("eid", -1);

        channelImg = findViewById(R.id.detail_channel_img);
        eventChannelText = findViewById(R.id.detail_event_channel);
        eventTimeText = findViewById(R.id.detail_event_time);
        eventClientNameText = findViewById(R.id.detail_event_client_name);
        eventAccepterView = findViewById(R.id.detail_event_accepter_view);
        eventAccepterNameText = findViewById(R.id.detail_event_accepter_name);
        eventLocationText = findViewById(R.id.detail_event_location);
        eventAddressText = findViewById(R.id.detail_event_address);
        eventNameText = findViewById(R.id.detail_event_name);
        eventContentText = findViewById(R.id.detail_event_content);
        eventCoinText = findViewById(R.id.detail_event_coin);
        resetEventStatusBtn = findViewById(R.id.reset_event_status_btn);
        eventCommentView = findViewById(R.id.detail_done_event_comment_view);
        ratingBar = findViewById(R.id.detail_event_rating_bar);
        eventCommentText = findViewById(R.id.detail_event_comment);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initDetail();
    }

    private void initDetail() {
        AlertDialogUtil.showProgressDialog(this);
        if (eid != -1) {

            new Thread(() -> {
                if (!NetworkTestUtil.isNetworkAvailable(this)) {
                    NetworkTestUtil.showNetworkDisableToast(this);
                }
                Event event = QueryEventUtil.queryEventWithEid(eid);
                if (event != null) {
                    category = event.getCategory();
                    eventName = event.getEventName();
                    eventContent = event.getEventContent();
                    eventTime = event.getEventTime();
                    city = event.getCity();
                    county = event.getCounty();
                    street = event.getStreet();
                    address = event.getAddress();
                    clientName = event.getClientName();
                    accepterName = event.getAcceptName();
                    clientId = event.getClientId();
                    accepterId = event.getAcceptId();
                    coin = event.getCoin();
                    point = event.getPoint();
                    comment = event.getComment();
                    runOnUiThread(() -> {
                        switch (category) {
                            case "医护":
                                Glide.with(this).load(R.drawable.ic_nursing).into(channelImg);
                                break;
                            case "跑腿":
                                Glide.with(this).load(R.drawable.ic_legwork).into(channelImg);
                                break;
                            case "清洁":
                                Glide.with(this).load(R.drawable.ic_cleaning).into(channelImg);
                                break;
                            case "教育":
                                Glide.with(this).load(R.drawable.ic_education).into(channelImg);
                                break;
                            case "餐饮":
                                Glide.with(this).load(R.drawable.ic_repast).into(channelImg);
                                break;
                            case "维修":
                                Glide.with(this).load(R.drawable.ic_repair).into(channelImg);
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
                        eventCommentText.setText(comment);
                        ratingBar.setRating(point / 2);
                        LogUtil.e("==Rating==", point + "");

                        if (accepterId != -1) {
                            eventAccepterView.setVisibility(View.VISIBLE);
                            eventAccepterNameText.setText(accepterName);
                        }
                        if (TextUtils.isEmpty(comment)) {
                            eventCommentView.setVisibility(View.GONE);
                        }

                        AlertDialogUtil.dismissProgressDialog();
                    });
                }
            }).start();

            if (queryEventStatus.equals(EventStatusUtil.UNACCEPTED)) {      // 在主页查询其他用户发布的待接单的 Event
                eventClientNameText.setOnClickListener(v -> AlertDialogUtil.showDealerInfoDialog(this, clientId));
                resetEventStatusBtn.setOnClickListener(v -> AlertDialogUtil.showMakeSureToAcceptEventDialog(this, eid));
            } else if (queryEventStatus.equals(EventStatusUtil.PUBLISHED_BUT_NOT_ACCEPTED)) {       // 个人 Event 页内用户发布的待接单的 Event
                resetEventStatusBtn.setText("修改");
                resetEventStatusBtn.setOnClickListener(v -> sentUpdateEventIntent());
            } else if (queryEventStatus.equals(EventStatusUtil.ONGOING)) {      // 个人 Event 页内进行中的 Event
                resetEventStatusBtn.setText("完成");
                resetEventStatusBtn.setOnClickListener(v -> {
                    LogUtil.e("==OnGoingDetail_UserId==", String.valueOf(userId));
                    LogUtil.e("==OnGoingDetail_ClientId==", String.valueOf(clientId));
                    LogUtil.e("==OnGoingDetail_AcceptId==", String.valueOf(accepterId));
                    if (userId == clientId) {                       // 当前用户作为发布者
                        AlertDialogUtil.showClientConfirmEventDoneDialog(this, eid);
                    } else if (userId == accepterId) {              // 当前用户作为接单人
                        AlertDialogUtil.showAccepterSetEventDoneDialog(this, eid);
                    }
                });
                eventClientNameText.setOnClickListener(v -> {
                    if (clientId != 0) {
                        AlertDialogUtil.showDealerInfoDialog(this, clientId);
                    }
                });
                eventAccepterNameText.setOnClickListener(v -> {
                    if (accepterId != 0) {
                        AlertDialogUtil.showDealerInfoDialog(this, accepterId);
                    }
                });
                LogUtil.e("==UserID==", String.valueOf(userId));
                LogUtil.e("==ClientID==", String.valueOf(clientId));
                LogUtil.e("==AccepterID==", String.valueOf(accepterId));
            } else if (queryEventStatus.equals(EventStatusUtil.DONE)) {         // 个人 Event 页内当前用户发布的已完成的 Event
                resetEventStatusBtn.setVisibility(View.GONE);
                eventCommentView.setVisibility(View.VISIBLE);
                eventAccepterNameText.setOnClickListener(v -> AlertDialogUtil.showDealerInfoDialog(this, accepterId));
            } else if (queryEventStatus.equals(EventStatusUtil.ANYONE_SET_DONE)) {    // 个人 Event 页内当前用户接受的已完成的 Event
                resetEventStatusBtn.setVisibility(View.GONE);
                eventCommentView.setVisibility(View.VISIBLE);
                eventClientNameText.setOnClickListener(v -> AlertDialogUtil.showDealerInfoDialog(this, clientId));
            } else if (queryEventStatus.equals(EventStatusUtil.PENDING_APPROVAL)) {      // 个人 Event 页内待审核的 Event
                resetEventStatusBtn.setText("修改");
                resetEventStatusBtn.setOnClickListener(v -> sentUpdateEventIntent());
            } else if (queryEventStatus.equals(EventStatusUtil.NOT_PASS)) {             // 个人 Event 页内审核不通过的 Event
                resetEventStatusBtn.setText("修改");
                resetEventStatusBtn.setOnClickListener(v -> sentUpdateEventIntent());
            }
        }
    }

    /**
     * 跳转到 Event 编辑页
     */
    private void sentUpdateEventIntent() {
        Intent intent = new Intent(this, RegisterEventActivity.class);
        intent.setAction("update_event");
        intent.putExtra("userId", userId);
        intent.putExtra("userName", userName);
        intent.putExtra("eid", eid);
        intent.putExtra("category", category);
        intent.putExtra("eventName", eventName);
        intent.putExtra("eventContent", eventContent);
        intent.putExtra("eventTime", eventTime);
        intent.putExtra("city", city);
        intent.putExtra("county", county);
        intent.putExtra("street", street);
        intent.putExtra("address", address);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}