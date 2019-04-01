package com.liarr.communityservice.Util;

import org.json.JSONObject;

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
}
