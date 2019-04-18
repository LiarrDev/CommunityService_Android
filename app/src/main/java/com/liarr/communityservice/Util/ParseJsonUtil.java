package com.liarr.communityservice.Util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.liarr.communityservice.Database.City;
import com.liarr.communityservice.Database.County;
import com.liarr.communityservice.Database.Province;

import org.json.JSONArray;
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
     * @param json 获取用户信息返回的 JSON
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

    /**
     * 解析和处理服务器返回的省级数据
     *
     * @param json 请求地区返回的 JSON
     * @return 处理成功或失败
     */
    public static boolean handleProvinceJson(String json) {
        if (!TextUtils.isEmpty(json)) {
            try {
                JSONArray allProvinces = new JSONArray(json);
                for (int i = 0; i < allProvinces.length(); i++) {
                    JSONObject provinceObject = allProvinces.getJSONObject(i);
                    Province province = new Province();
                    province.setProvinceName(provinceObject.getString("name"));
                    province.setProvinceCode(provinceObject.getInt("id"));
                    province.save();
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 解析和处理服务器返回的市级数据
     *
     * @param json       请求地区返回的 JSON
     * @param provinceId 省 ID
     * @return 处理成功或失败
     */
    public static boolean handleCityJson(String json, int provinceId) {
        if (!TextUtils.isEmpty(json)) {
            try {
                JSONArray allCities = new JSONArray(json);
                for (int i = 0; i < allCities.length(); i++) {
                    JSONObject cityObject = allCities.getJSONObject(i);
                    City city = new City();
                    city.setCityName(cityObject.getString("name"));
                    city.setCityCode(cityObject.getInt("id"));
                    city.setProvinceId(provinceId);
                    city.save();
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 解析和处理服务器返回的县级数据
     *
     * @param json   请求地区返回的 JSON
     * @param cityId 市 ID
     * @return 处理成功或失败
     */
    public static boolean handleCountyJson(String json, int cityId) {
        if (!TextUtils.isEmpty(json)) {
            try {
                JSONArray allCounties = new JSONArray(json);
                for (int i = 0; i < allCounties.length(); i++) {
                    JSONObject countyObject = allCounties.getJSONObject(i);
                    County county = new County();
                    county.setCountyName(countyObject.getString("name"));
                    county.setCityId(cityId);
                    county.save();
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
