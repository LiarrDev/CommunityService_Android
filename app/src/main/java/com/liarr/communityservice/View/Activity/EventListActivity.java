package com.liarr.communityservice.View.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.liarr.communityservice.R;
import com.liarr.communityservice.Util.LogUtil;

public class EventListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);

        String channel = getIntent().getStringExtra("channel");
        String location = getIntent().getStringExtra("location");
        LogUtil.e("==ListChannel==", channel);
        LogUtil.e("==ListLocation==", location);
    }
}
