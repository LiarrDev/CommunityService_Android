package com.liarr.communityservice.Util;

public class HttpRequestUrlUtil {

    private static String DOMAIN = "http://120.76.62.5:8080";               // 域名

    public static String signInUrl = DOMAIN + "/aaaa/login";                // 登录

    public static String signUpUrl = DOMAIN + "/aaaa/register";             // 注册

    public static String userInfoUrl = DOMAIN + "/aaaa/showUserInfo";       // 展示个人信息

    public static String updateUserInfoUrl = DOMAIN + "/aaaa/userUpdate";   // 修改个人信息

    public static String eventListUrl = DOMAIN + "/aaaa/listEvent";         // 查询 Event

    public static String locationUrl = "http://guolin.tech/api/china";      // 查询地点
}