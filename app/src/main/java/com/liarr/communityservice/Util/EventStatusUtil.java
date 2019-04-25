package com.liarr.communityservice.Util;

public class EventStatusUtil {

    public static String PENDING_APPROVAL = String.valueOf(0);     // Event 待审核

    static String UNACCEPTED = String.valueOf(1);           // Event 审核已通过但未接单

    public static String ONGOING = String.valueOf(2);              // Event 进行中

    public static String DONE = String.valueOf(3);                 // Event 已完成

    public static String NOT_PASS = String.valueOf(4);             // Event 审核不通过

    public static String ACCEPTER_SET_DONE = String.valueOf(5);    // Event 接单人点击完成
}
