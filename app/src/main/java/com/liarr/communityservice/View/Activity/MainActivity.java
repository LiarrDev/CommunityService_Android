package com.liarr.communityservice.View.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.liarr.communityservice.Database.Event;
import com.liarr.communityservice.R;
import com.liarr.communityservice.Util.EventStatusUtil;
import com.liarr.communityservice.Util.HttpRequestUrlUtil;
import com.liarr.communityservice.Util.LogUtil;
import com.liarr.communityservice.Util.NetworkTestUtil;
import com.liarr.communityservice.Util.ParseJsonUtil;
import com.liarr.communityservice.Util.QueryEventUtil;
import com.liarr.communityservice.View.Adapter.EventItemAdapter;

import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.liarr.communityservice.View.Fragment.ChooseAreaFragment.SETTING_GLOBAL_LOCATION;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    TextView nameNav;
    TextView coinText;
    TextView noEventText;
    FloatingActionButton registerFab;

    LinearLayout channelNursing;
    LinearLayout channelLegwork;
    LinearLayout channelCleaning;
    LinearLayout channelEducation;
    LinearLayout channelRepast;
    LinearLayout channelRepair;

    RecyclerView recyclerView;

    AppCompatRatingBar starsView;

    int userId;
    String userName;

    private EventItemAdapter adapter;

    private List<Event> eventList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        userId = intent.getIntExtra("userId", -1);
        LogUtil.e("==MainGetIntentBundle==", userId + "");

        initMainView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        initDrawer();
        initEventList();

        // 设置 Event List 的查询状态为其他用户发布的未接单 Event
        SharedPreferences.Editor editor = getSharedPreferences("defaultUser", MODE_PRIVATE).edit();
        editor.putString("queryEventStatus", EventStatusUtil.UNACCEPTED);
        editor.apply();
    }

    /**
     * 初始化主视图
     */
    private void initMainView() {
        toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_me);
        }

        noEventText = findViewById(R.id.no_all_event_text);

        channelNursing = findViewById(R.id.channel_nursing);
        channelNursing.setOnClickListener(v -> openChannel("医护"));

        channelLegwork = findViewById(R.id.channel_legwork);
        channelLegwork.setOnClickListener(v -> openChannel("跑腿"));

        channelCleaning = findViewById(R.id.channel_cleaning);
        channelCleaning.setOnClickListener(v -> openChannel("清洁"));

        channelEducation = findViewById(R.id.channel_education);
        channelEducation.setOnClickListener(v -> openChannel("教育"));

        channelRepast = findViewById(R.id.channel_repast);
        channelRepast.setOnClickListener(v -> openChannel("餐饮"));

        channelRepair = findViewById(R.id.channel_repair);
        channelRepair.setOnClickListener(v -> openChannel("维修"));

        registerFab = findViewById(R.id.add_fab);
        registerFab.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterEventActivity.class);
            intent.setAction("new_event");
            intent.putExtra("userName", userName);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });

        recyclerView = findViewById(R.id.main_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        drawerLayout = findViewById(R.id.main_drawer);
        navigationView = findViewById(R.id.drawer_nav);
        View headerView = navigationView.getHeaderView(0);
        nameNav = headerView.findViewById(R.id.header_name);
        coinText = headerView.findViewById(R.id.coin_text);
        starsView = headerView.findViewById(R.id.stars_view);
    }

    /**
     * 初始化侧滑菜单栏
     */
    private void initDrawer() {

        updateUserInfo();
        getUserInfo(userId);

        // 从 SharedPreference 中取出 Location
        SharedPreferences preferences = getSharedPreferences("defaultUser", MODE_PRIVATE);
        String prefLocation = preferences.getString("location", "");
        if (!TextUtils.isEmpty(prefLocation.trim())) {
            navigationView.getMenu().getItem(1).setTitle(prefLocation);
        }

        navigationView.setNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.nav_me:
                    Intent meIntent = new Intent(this, MeActivity.class);
                    startActivity(meIntent);
                    break;

                case R.id.nav_location:
                    Intent locationIntent = new Intent(this, LocationActivity.class);
                    locationIntent.setAction(SETTING_GLOBAL_LOCATION);
                    startActivity(locationIntent);
                    break;

                case R.id.nav_event:
                    Intent eventIntent = new Intent(this, PersonalEventActivity.class);
                    startActivity(eventIntent);
                    break;

                case R.id.nav_sign_out:
                    Intent signOutIntent = new Intent(this, SignInActivity.class);
                    SharedPreferences.Editor editor = getSharedPreferences("defaultUser", MODE_PRIVATE).edit();
                    editor.clear();
                    editor.apply();
                    startActivity(signOutIntent);
                    finish();
                    break;

                case R.id.nav_about:
                    Intent aboutIntent = new Intent(MainActivity.this, AboutActivity.class);
                    startActivity(aboutIntent);
                    break;

                default:
            }
            drawerLayout.closeDrawers();
            return true;
        });
    }

    /**
     * 加载 Event List
     */
    private void initEventList() {
        noEventText.setVisibility(View.GONE);
        new Thread(() -> {
            // 从 SharedPreference 中取出 Location
            SharedPreferences preferences = getSharedPreferences("defaultUser", MODE_PRIVATE);
            String prefLocation = preferences.getString("location", "");
            if (!NetworkTestUtil.isNetworkAvailable(this)) {
                NetworkTestUtil.showNetworkDisableToast(this);
            } else {
                eventList = QueryEventUtil.queryUnacceptedEvent(userId, prefLocation);
                runOnUiThread(() -> {
                    if (eventList.size() <= 0) {
                        noEventText.setVisibility(View.VISIBLE);
                    }
                    adapter = new EventItemAdapter(eventList);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                });
            }
        }).start();
    }

    /**
     * 获取用户个人信息
     *
     * @param userId 用户的 uid
     */
    private void getUserInfo(int userId) {
        LogUtil.e("==InsideFun==", "getUserInfo()");
        new Thread(() -> {
            try {
                OkHttpClient client = new OkHttpClient();
                RequestBody requestBody = new FormBody.Builder()
                        .add("uid", String.valueOf(userId))
                        .build();
                LogUtil.e("==getUserInfo==", String.valueOf(userId));
                Request request = new Request.Builder()
                        .url(HttpRequestUrlUtil.userInfoUrl)
                        .post(requestBody)
                        .build();
                Response response = client.newCall(request).execute();
                String responseContent = response.body().string();
                LogUtil.e("==UserInfo==", responseContent);
                ParseJsonUtil.parseUserInfoJson(this, responseContent);
                runOnUiThread(this::updateUserInfo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * 更新用户个人信息显示
     */
    private void updateUserInfo() {
        // 为了在无网络情况下也可以正常显示用户信息所以需要初始化的时候从 SharePreference 中调用该函数
        SharedPreferences preferences = getSharedPreferences("defaultUser", MODE_PRIVATE);
        userName = preferences.getString("userName", "");
        int coin = preferences.getInt("coin", 0);
        float point = preferences.getFloat("point", 0);
        nameNav.setText(userName);
        coinText.setText(String.valueOf(coin));
        starsView.setRating(point / 2);
    }

    /**
     * 打开对应频道查看 Event
     *
     * @param channelName 频道名称
     */
    private void openChannel(String channelName) {
        // 从 SharedPreference 中取出 Location
        SharedPreferences preferences = getSharedPreferences("defaultUser", MODE_PRIVATE);
        String prefLocation = preferences.getString("location", "");

        Intent intent = new Intent(this, EventListActivity.class);
        intent.putExtra("channel", channelName);
        intent.putExtra("location", prefLocation);
        intent.putExtra("userId", userId);
        startActivity(intent);
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
            drawerLayout.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 如果触摸 BACK 键时 Drawer 打开了就把它关闭
     */
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();
        } else {
            super.onBackPressed();
        }
    }
}