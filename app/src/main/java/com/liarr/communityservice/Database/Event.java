package com.liarr.communityservice.Database;

public class Event {

    private int eid;                // Event ID

    private String eventName;       // Event 名称

    private String category;        // Event 分类

    private String eventContent;    // Event 内容

    private String eventTime;       // Event 时间

    private String city;            // Event 所在市

    private String county;          // Event 所在县

    private String street;          // Event 所在街道

    private String address;         // Event 具体地址

    private int clientId;           // Event 发布者的 ID

    private String clientName;      // Event 发布者的 Name

    private int acceptId;           // Event 接单人的 ID

    private String acceptName;      // Event 接单人的 Name

    private int coin;               // Event 发布者提供的 Coin

    private int status;             // Event 当前的状态。待审核：0，已通过：1，进行中：2，已完成：3，不通过：4，接单人点击完成：5

    private int point;              // 对已完成 Event 的评分

    private String comment;         // 对已完成 Event 的评论

    public Event(int eid, String eventName, String category, String eventTime, String city, String county, int coin) {
        this.eid = eid;
        this.eventName = eventName;
        this.category = category;
        this.eventTime = eventTime;
        this.city = city;
        this.county = county;
        this.coin = coin;
    }

    public int getEid() {
        return eid;
    }

    public String getEventName() {
        return eventName;
    }

    public String getCategory() {
        return category;
    }

    public String getEventContent() {
        return eventContent;
    }

    public String getEventTime() {
        return eventTime;
    }

    public String getCity() {
        return city;
    }

    public String getCounty() {
        return county;
    }

    public String getStreet() {
        return street;
    }

    public String getAddress() {
        return address;
    }

    public int getClientId() {
        return clientId;
    }

    public String getClientName() {
        return clientName;
    }

    public int getAcceptId() {
        return acceptId;
    }

    public String getAcceptName() {
        return acceptName;
    }

    public int getCoin() {
        return coin;
    }

    public int getStatus() {
        return status;
    }

    public int getPoint() {
        return point;
    }

    public String getComment() {
        return comment;
    }
}
