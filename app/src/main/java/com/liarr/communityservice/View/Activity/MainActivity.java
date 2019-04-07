package com.liarr.communityservice.View.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.liarr.communityservice.R;
import com.liarr.communityservice.Util.HttpRequestUrlUtil;
import com.liarr.communityservice.Util.LogUtil;
import com.liarr.communityservice.Util.ParseJsonUtil;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    TextView nameNav;
    TextView coinText;

    ImageView star1, star2, star3, star4, star5;

    int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        userId = intent.getIntExtra("userId", -1);
        LogUtil.e("==MainGetIntentBundle==", userId + "");

        initMainView();
        initDrawer();

    }

    /**
     * 初始化主视图
     */
    private void initMainView() {
        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_user);
        }
    }

    /**
     * 初始化侧滑菜单栏
     */
    private void initDrawer() {
        drawerLayout = (DrawerLayout) findViewById(R.id.main_drawer);
        navigationView = (NavigationView) findViewById(R.id.drawer_nav);
        View headerView = navigationView.getHeaderView(0);
        nameNav = (TextView) headerView.findViewById(R.id.header_name);
        coinText = (TextView) headerView.findViewById(R.id.coin_text);
        star1 = (ImageView) headerView.findViewById(R.id.star_1);
        star2 = (ImageView) headerView.findViewById(R.id.star_2);
        star3 = (ImageView) headerView.findViewById(R.id.star_3);
        star4 = (ImageView) headerView.findViewById(R.id.star_4);
        star5 = (ImageView) headerView.findViewById(R.id.star_5);
        updateUserInfo();
        getUserInfo(userId);

        navigationView.setNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.nav_me:
                    break;

                case R.id.nav_location:
                    break;

                case R.id.nav_event:
                    break;

                case R.id.nav_sign_out:
                    Intent signOutIntent = new Intent(MainActivity.this, SignInActivity.class);
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
                LogUtil.e("==getUserInfo==", HttpRequestUrlUtil.userInfoUrl);
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
        // 为了在无网络情况下也可以正常显示用户信息所以需要初始化的时候从SharePre中调用该函数
        SharedPreferences preferences = getSharedPreferences("defaultUser", MODE_PRIVATE);
        String userName = preferences.getString("userName", "");
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
     * Menu 管理
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
