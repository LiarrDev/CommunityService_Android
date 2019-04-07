package com.liarr.communityservice.Util;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONObject;

import static android.content.Context.MODE_PRIVATE;

public class ParseJsonUtil {

    /**
     * 解析注册或登陆返回的 JSON，并返回对应的 Code
     *
     * @param json 注册或登陆返回的 JSON 字符串
     * @return 注册或登陆的状态 Code
     */
    public static String parseSignUpOrSignInCodeJson(String json) {
        String code = null;
        try {
            JSONObject jsonObject = new JSONObject(json);
            code = jsonObject.getString("code");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return code;
    }

    /**
     * 解析登录返回的 JSON 获取用户的 UID
     *
     * @param json 登录返回的 JSON
     * @return 用户的 UID
     */
    public static int parseUserIdJson(String json) {
        int uid = 0;
        try {
            JSONObject jsonObject = new JSONObject(json);
            String user = jsonObject.getString("user");
            LogUtil.e("===SignUpOrSignInUser===", user);
            JSONObject userJsonObject = new JSONObject(user);
            uid = userJsonObject.getInt("uid");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return uid;
    }

    /**
     * 解析获取用户信息返回的 JSON 并把相应的内容存到 SharedPreferences 中
     *
     * @param json 获取用户信息返回到 JSON
     */
    public static void parseUserInfoJson(Context context, String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            String user = jsonObject.getString("user");
            JSONObject userJsonObject = new JSONObject(user);
            int coin = userJsonObject.getInt("coin");
            int doneEventNum = userJsonObject.getInt("doneNum");
            double point = userJsonObject.getDouble("point");
            String userName = userJsonObject.getString("userName");
            String tel = userJsonObject.getString("tel");

            SharedPreferences.Editor editor = context.getSharedPreferences("defaultUser", MODE_PRIVATE).edit();
            editor.putInt("coin", coin);
            editor.putInt("doneEventNum", doneEventNum);
            editor.putFloat("point", (float) point);
            editor.putString("userName", userName);
            editor.putString("tel", tel);
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
