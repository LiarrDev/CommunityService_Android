package com.liarr.communityservice.View.Activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.liarr.communityservice.Database.Event;
import com.liarr.communityservice.R;
import com.liarr.communityservice.Util.LogUtil;
import com.liarr.communityservice.Util.QueryEventUtil;
import com.liarr.communityservice.View.Adapter.EventItemAdapter;

import java.util.ArrayList;
import java.util.List;

public class EventListActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerView;

    String channel;
    String location;
    int userId;

    private EventItemAdapter adapter;

    private List<Event> eventList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);

        channel = getIntent().getStringExtra("channel");
        location = getIntent().getStringExtra("location");
        userId = getIntent().getIntExtra("userId", -1);
        LogUtil.e("==ListChannel==", channel);
        LogUtil.e("==ListLocation==", location);

        recyclerView = findViewById(R.id.event_list_recycler);

        toolbar = findViewById(R.id.event_list_toolbar);
        toolbar.setTitle(channel);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        initEventList();
    }

    /**
     * 加载 Event List
     */
    private void initEventList() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        new Thread(() -> {
            eventList = QueryEventUtil.queryUnacceptedEventWithChannel(channel, userId, location);
            runOnUiThread(() -> {
                adapter = new EventItemAdapter(eventList);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            });
        }).start();
    }

    /**
     * Menu 选择管理
     *
     * @param item Menu Item
     * @return SUPER
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
