package com.liarr.communityservice.Util;

public class EventStatusUtil {

    public static String PENDING_APPROVAL = String.valueOf(0);     // Event 待审核

    public static String UNACCEPTED = String.valueOf(1);           // Event 审核已通过但未接单

    public static String ONGOING = String.valueOf(2);              // Event 进行中

    public static String DONE = String.valueOf(3);                 // Event 已完成

    public static String NOT_PASS = String.valueOf(4);             // Event 审核不通过

    public static String ACCEPTER_SET_DONE = String.valueOf(5);    // Event 接单人点击完成

    public static String PUBLISHED_BUT_NOT_ACCEPTED = String.valueOf(6);       // 自己发布但未被接单但 Event

    public static String ANYONE_SET_DONE = String.valueOf(7);       // Event 的任意一方设置完成

    public static int AS_CLIENT = 99;                               // 作为发布者

    public static int AS_ACCEPTER = 98;                             // 作为接单人

    public static int AS_BOTH_OF_CLIENT_AND_ACCEPTER = 97;                                 // 同时查询用户作为发布者和接单人时使用
}
