package com.liarr.communityservice.Util;

import org.json.JSONObject;

public class ParseJsonUtil {

    /**
     * 解析注册或登陆返回的 JSON，并将其状态转为 Boolean 值
     * @param json 注册或登陆返回的 JSON 字符串
     * @return 注册或登陆成功为 TRUE，注册或登陆失败为 FALSE
     */
    public static boolean parseSignUpOrSignInStateJson(String json) {
        boolean state = false;
        try {
            JSONObject jsonObject = new JSONObject(json);
            String msg = jsonObject.getString("msg");
            LogUtil.e("===SignUpOrSignInMsg===", msg);
            if (msg.equals("success")) {
                state = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return state;
    }

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
}
