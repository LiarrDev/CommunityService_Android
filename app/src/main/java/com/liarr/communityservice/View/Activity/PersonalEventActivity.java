package com.liarr.communityservice.View.Activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.liarr.communityservice.Database.Event;
import com.liarr.communityservice.R;
import com.liarr.communityservice.Util.EventStatusUtil;
import com.liarr.communityservice.Util.ListUtil;
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
    AppCompatTextView noRecordText;

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

        noRecordText = findViewById(R.id.no_record_text);

        tabLayout = findViewById(R.id.personal_event_tab);
        tabLayout.removeAllTabs();
        // 刚进入当前页面需要进行的预加载
        tabLayout.addTab(tabLayout.newTab().setText("待接单"));
        tabLayout.addTab(tabLayout.newTab().setText("进行中"));
        initEventList(EventStatusUtil.UNACCEPTED, EventStatusUtil.AS_CLIENT);

        // 设置 Event List 的查询状态为当前用户发布的待接单的 Event
        SharedPreferences.Editor editor = getSharedPreferences("defaultUser", MODE_PRIVATE).edit();
        editor.putString("queryEventStatus", EventStatusUtil.PUBLISHED_BUT_NOT_ACCEPTED);
        editor.apply();

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

    @Override
    protected void onStart() {
        super.onStart();
        initTabSelectedEventData();
    }

    /**
     * 在选择的 Tab 变更时更新响应的数据
     */
    private void initTabSelectedEventData() {
        // 及时变更数据
        SharedPreferences preferences = getSharedPreferences("defaultUser", MODE_PRIVATE);
        String queryEventStatus = preferences.getString("queryEventStatus", "");
        if (queryEventStatus.equals(EventStatusUtil.PUBLISHED_BUT_NOT_ACCEPTED)) {
            initEventList(EventStatusUtil.UNACCEPTED, EventStatusUtil.AS_CLIENT);
        } else if (queryEventStatus.equals(EventStatusUtil.ONGOING)) {
            initEventList(EventStatusUtil.ONGOING, EventStatusUtil.AS_BOTH_OF_CLIENT_AND_ACCEPTER);
        } else if (queryEventStatus.equals(EventStatusUtil.DONE)) {
            initEventList(EventStatusUtil.DONE, EventStatusUtil.AS_CLIENT);
        } else if (queryEventStatus.equals(EventStatusUtil.ANYONE_SET_DONE)) {
            initEventList(EventStatusUtil.ANYONE_SET_DONE, EventStatusUtil.AS_ACCEPTER);
        } else if (queryEventStatus.equals(EventStatusUtil.PENDING_APPROVAL)) {
            initEventList(EventStatusUtil.PENDING_APPROVAL, EventStatusUtil.AS_CLIENT);
        } else if (queryEventStatus.equals(EventStatusUtil.NOT_PASS)) {
            initEventList(EventStatusUtil.NOT_PASS, EventStatusUtil.AS_CLIENT);
        }
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String tabName = tab.getText().toString();
                LogUtil.e("===TabSelected===", tabName);
                SharedPreferences.Editor editor = getSharedPreferences("defaultUser", MODE_PRIVATE).edit();
                switch (tabName) {
                    case "待接单":
                        initEventList(EventStatusUtil.UNACCEPTED, EventStatusUtil.AS_CLIENT);
                        // 设置 Event List 的查询状态为当前用户发布的待接单的 Event
                        editor.putString("queryEventStatus", EventStatusUtil.PUBLISHED_BUT_NOT_ACCEPTED);
                        editor.apply();
                        break;
                    case "进行中":
                        initEventList(EventStatusUtil.ONGOING, EventStatusUtil.AS_BOTH_OF_CLIENT_AND_ACCEPTER);
                        // 设置 Event List 的查询状态为当前用户参与的进行中的 Event
                        editor.putString("queryEventStatus", EventStatusUtil.ONGOING);
                        editor.apply();
                        break;
                    case "我发布的":
                        initEventList(EventStatusUtil.DONE, EventStatusUtil.AS_CLIENT);
                        // 设置 Event List 的查询状态为当前用户发布的已完成的 Event
                        editor.putString("queryEventStatus", EventStatusUtil.DONE);
                        editor.apply();
                        break;
                    case "我接受的":
                        initEventList(EventStatusUtil.ANYONE_SET_DONE, EventStatusUtil.AS_ACCEPTER);
                        // 设置 Event List 的查询状态为当前用户接受的待接单的 Event
                        editor.putString("queryEventStatus", EventStatusUtil.ANYONE_SET_DONE);
                        editor.apply();
                        break;
                    case "待审核":
                        initEventList(EventStatusUtil.PENDING_APPROVAL, EventStatusUtil.AS_CLIENT);
                        // 设置 Event List 的查询状态为当前用户发布的待审核的 Event
                        editor.putString("queryEventStatus", EventStatusUtil.PENDING_APPROVAL);
                        editor.apply();
                        break;
                    case "不通过":
                        initEventList(EventStatusUtil.NOT_PASS, EventStatusUtil.AS_CLIENT);
                        // 设置 Event List 的查询状态为当前用户发布的审核不通过的 Event
                        editor.putString("queryEventStatus", EventStatusUtil.NOT_PASS);
                        editor.apply();
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
        noRecordText.setVisibility(View.GONE);
        new Thread(() -> {
            // 从 SharedPreference 中取出 User ID
            SharedPreferences preferences = getSharedPreferences("defaultUser", MODE_PRIVATE);
            int prefUserId = preferences.getInt("userId", -1);
            if (role == EventStatusUtil.AS_BOTH_OF_CLIENT_AND_ACCEPTER) {
                // 查询当前用户作为发布者且接单人已点击完成但发布者还未点击完成的 Event
                eventList = QueryEventUtil.queryPersonalEventWithStatus(prefUserId, EventStatusUtil.ACCEPTER_SET_DONE, EventStatusUtil.AS_CLIENT);
                // 查询当前用户作为接单人且双方未点击完成的 Event
                eventList.addAll(QueryEventUtil.queryPersonalEventWithStatus(prefUserId, eventStatus, EventStatusUtil.AS_CLIENT));
                // 查询当前用户作为发布者且双方未点击完成的 Event
                eventList.addAll(QueryEventUtil.queryPersonalEventWithStatus(prefUserId, eventStatus, EventStatusUtil.AS_ACCEPTER));
                LogUtil.e("==OnGoingListSizeBefore==", String.valueOf(eventList.size()));
                eventList = ListUtil.removeDuplicate(eventList);            // 不加这句会经常出现 Item 多次重复加载的情况，加上这句则极少出现
                LogUtil.e("==OnGoingListSizeAfter==", String.valueOf(eventList.size()));
            } else if (eventStatus.equals(EventStatusUtil.ANYONE_SET_DONE)) {
                // 已完成状态下同时查询作为接单人点击已完成和双方点击已完成的 Event
                eventList = QueryEventUtil.queryPersonalEventWithStatus(prefUserId, EventStatusUtil.ACCEPTER_SET_DONE, role);
                eventList.addAll(QueryEventUtil.queryPersonalEventWithStatus(prefUserId, EventStatusUtil.DONE, role));
//                eventList = ListUtil.removeDuplicate(eventList);          // 加上这句会偶尔出现加载不全的情况
            } else {
                eventList = QueryEventUtil.queryPersonalEventWithStatus(prefUserId, eventStatus, role);
            }
            runOnUiThread(() -> {
                if (eventList.size() <= 0) {
                    noRecordText.setVisibility(View.VISIBLE);
                }
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