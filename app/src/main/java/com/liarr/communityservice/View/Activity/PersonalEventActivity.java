package com.liarr.communityservice.View.Activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.liarr.communityservice.Database.Event;
import com.liarr.communityservice.R;
import com.liarr.communityservice.Util.EventStatusUtil;
import com.liarr.communityservice.Util.LogUtil;
import com.liarr.communityservice.Util.QueryEventUtil;
import com.liarr.communityservice.View.Adapter.EventItemAdapter;

import java.util.ArrayList;
import java.util.List;

public class PersonalEventActivity extends AppCompatActivity {

    Toolbar toolbar;
    BottomNavigationView bottomNavigationView;
    TabLayout tabLayout;
    RecyclerView recyclerView;

    private List<Event> eventList = new ArrayList<>();

    private EventItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_event);

        toolbar = findViewById(R.id.personal_event_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        tabLayout = findViewById(R.id.personal_event_tab);
        tabLayout.removeAllTabs();

        // 刚进入当前页面需要进行的预加载
        tabLayout.addTab(tabLayout.newTab().setText("待接单"));
        tabLayout.addTab(tabLayout.newTab().setText("进行中"));
        initEventList(EventStatusUtil.UNACCEPTED, EventStatusUtil.AS_CLIENT);
        initTabSelectedEventData();

        recyclerView = findViewById(R.id.personal_event_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.nav_event_published:
                    tabLayout.removeAllTabs();
                    tabLayout.addTab(tabLayout.newTab().setText("待接单"));
                    tabLayout.addTab(tabLayout.newTab().setText("进行中"));
                    return true;

                case R.id.nav_event_finished:
                    tabLayout.removeAllTabs();
                    tabLayout.addTab(tabLayout.newTab().setText("我发布的"));
                    tabLayout.addTab(tabLayout.newTab().setText("我接受的"));
                    return true;

                case R.id.nav_event_audit:
                    tabLayout.removeAllTabs();
                    tabLayout.addTab(tabLayout.newTab().setText("待审核"));
                    tabLayout.addTab(tabLayout.newTab().setText("不通过"));
                    return true;

                default:
            }
            return false;
        });
    }

    /**
     * 在选择的 Tab 变更时更新响应的数据
     */
    private void initTabSelectedEventData() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String tabName = tab.getText().toString();
                LogUtil.e("===TabSelected===", tabName);
                switch (tabName) {
                    case "待接单":
                        initEventList(EventStatusUtil.UNACCEPTED, EventStatusUtil.AS_CLIENT);
                        break;
                    case "进行中":
                        initEventList(EventStatusUtil.ONGOING, EventStatusUtil.AS_BOTH);
                        break;
                    case "我发布的":
                        initEventList(EventStatusUtil.DONE, EventStatusUtil.AS_CLIENT);
                        break;
                    case "我接受的":
                        initEventList(EventStatusUtil.ACCEPTER_SET_DONE, EventStatusUtil.AS_ACCEPTER);
                        break;
                    case "待审核":
                        initEventList(EventStatusUtil.PENDING_APPROVAL, EventStatusUtil.AS_CLIENT);
                        break;
                    case "不通过":
                        initEventList(EventStatusUtil.NOT_PASS, EventStatusUtil.AS_CLIENT);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    /**
     * 初始化 Event List
     *
     * @param eventStatus Event 状态
     * @param role        用户当前角色
     */
    private void initEventList(String eventStatus, int role) {
        new Thread(() -> {
            // 从 SharedPreference 中取出 User ID
            SharedPreferences preferences = getSharedPreferences("defaultUser", MODE_PRIVATE);
            int prefUserId = preferences.getInt("userId", -1);
            if (role == EventStatusUtil.AS_BOTH) {
                eventList = QueryEventUtil.queryPersonalEventWithStatus(prefUserId, eventStatus, EventStatusUtil.AS_CLIENT);
                eventList.addAll(QueryEventUtil.queryPersonalEventWithStatus(prefUserId, eventStatus, EventStatusUtil.AS_ACCEPTER));
            } else {
                eventList = QueryEventUtil.queryPersonalEventWithStatus(prefUserId, eventStatus, role);
            }
            runOnUiThread(() -> {
                adapter = new EventItemAdapter(eventList);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            });
        }).start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
