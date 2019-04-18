package com.liarr.communityservice.Util;

public class EventStatusUtil {

    public static int PENDING_APPROVAL = 0;     // Event 待审核

    public static int UNACCEPTED = 1;           // Event 审核已通过但未接单

    public static int ONGOING = 2;              // Event 进行中

    public static int DONE = 3;                 // Event 已完成

    public static int NOT_PASS = 4;             // Event 审核不通过

    public static int ACCEPTER_SET_DONE = 5;    // Event 接单人点击完成
}
