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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.liarr.communityservice.Database.Event;
import com.liarr.communityservice.R;
import com.liarr.communityservice.Util.HttpRequestUrlUtil;
import com.liarr.communityservice.Util.LogUtil;
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
    FloatingActionButton registerFab;

    LinearLayout channelNursing;
    LinearLayout channelLegwork;
    LinearLayout channelCleaning;
    LinearLayout channelEducation;
    LinearLayout channelRepast;
    LinearLayout channelRepair;

    RecyclerView recyclerView;

    ImageView star1, star2, star3, star4, star5;

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
            intent.putExtra("userName", userName);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });


        recyclerView = findViewById(R.id.main_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        // TODO: 把星星换成 RatingBar
        drawerLayout = findViewById(R.id.main_drawer);
        navigationView = findViewById(R.id.drawer_nav);
        View headerView = navigationView.getHeaderView(0);
        nameNav = headerView.findViewById(R.id.header_name);
        coinText = headerView.findViewById(R.id.coin_text);
        star1 = headerView.findViewById(R.id.star_1);
        star2 = headerView.findViewById(R.id.star_2);
        star3 = headerView.findViewById(R.id.star_3);
        star4 = headerView.findViewById(R.id.star_4);
        star5 = headerView.findViewById(R.id.star_5);
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
                    locationIntent.putExtra("action", SETTING_GLOBAL_LOCATION);
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
        new Thread(() -> {
            // 从 SharedPreference 中取出 Location
            SharedPreferences preferences = getSharedPreferences("defaultUser", MODE_PRIVATE);
            String prefLocation = preferences.getString("location", "");
            eventList = QueryEventUtil.queryUnacceptedEvent(userId, prefLocation);
            runOnUiThread(() -> {
                adapter = new EventItemAdapter(eventList);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            });
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
        coinText.setText(coin + "");
        // 设置第 1 颗星
        if (point >= 0.5 && point < 1.5) {
            star1.setImageResource(R.drawable.ic_star_half);
        } else if (point >= 1.5) {
            star1.setImageResource(R.drawable.ic_star_full);
        }
        // 设置第 2 颗星
        if (point >= 2.5 && point < 3.5) {
            star2.setImageResource(R.drawable.ic_star_half);
        } else if (point >= 3.5) {
            star2.setImageResource(R.drawable.ic_star_full);
        }
        // 设置第 3 颗星
        if (point >= 4.5 && point < 5.5) {
            star3.setImageResource(R.drawable.ic_star_half);
        } else if (point >= 5.5) {
            star3.setImageResource(R.drawable.ic_star_full);
        }
        // 设置第 4 颗星
        if (point >= 6.5 && point < 7.5) {
            star4.setImageResource(R.drawable.ic_star_half);
        } else if (point >= 7.5) {
            star4.setImageResource(R.drawable.ic_star_full);
        }
        // 设置第 5 颗星
        if (point >= 8.5 && point < 9.5) {
            star5.setImageResource(R.drawable.ic_star_half);
        } else if (point >= 9.5) {
            star5.setImageResource(R.drawable.ic_star_full);
        }
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
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;

            default:
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
