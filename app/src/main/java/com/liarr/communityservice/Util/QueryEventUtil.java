package com.liarr.communityservice.Util;

import com.liarr.communityservice.Database.Event;

import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class QueryEventUtil {

    /**
     * 查询待接单的 Event
     *
     * @param uid      当前用户的 UserID
     * @param location 用户设定的 Location
     * @return 重新组装过的 List
     */
    public static List<Event> queryUnacceptedEvent(int uid, String location) {
        List<Event> eventList = null;
        try {
            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = new FormBody.Builder()
                    .add("clientId", String.valueOf(uid))
                    .add("city", location)
                    .add("status", EventStatusUtil.UNACCEPTED)
                    .add("isMarket", String.valueOf(1))     // 不查询当前用户发布的 Event
                    .build();
            Request request = new Request.Builder()
                    .url(HttpRequestUrlUtil.eventListUrl)
                    .post(requestBody)
                    .build();
            Response response = client.newCall(request).execute();
            String responseContent = response.body().string();
            LogUtil.e("==EventResponse==", responseContent);
            eventList = ParseJsonUtil.parseEventListJson(responseContent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return eventList;
    }

    /**
     * 根据 Channel 查询待接单的 Event
     *
     * @param channel  用户选择的 Channel
     * @param uid      当前用户的 UserID
     * @param location 用户设定的 Location
     * @return 重新组装过的 List
     */
    public static List<Event> queryUnacceptedEventWithChannel(String channel, int uid, String location) {
        List<Event> eventList = null;
        try {
            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = new FormBody.Builder()
                    .add("clientId", String.valueOf(uid))
                    .add("category", channel)
                    .add("city", location)
                    .add("status", EventStatusUtil.UNACCEPTED)
                    .add("isMarket", String.valueOf(1))     // 不查询当前用户发布的 Event
                    .build();
            Request request = new Request.Builder()
                    .url(HttpRequestUrlUtil.eventListUrl)
                    .post(requestBody)
                    .build();
            Response response = client.newCall(request).execute();
            String responseContent = response.body().string();
            eventList = ParseJsonUtil.parseEventListJson(responseContent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return eventList;
    }

    /**
     * 根据 Eid 查询待接单的 Event
     *
     * @param eid Event ID
     * @return Event
     */
    public static Event queryUnacceptedEventWithEid(int eid) {
        Event event = null;
        try {
            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = new FormBody.Builder()
                    .add("eid", String.valueOf(eid))
                    .build();
            Request request = new Request.Builder()
                    .url(HttpRequestUrlUtil.eventListUrl)
                    .post(requestBody)
                    .build();
            Response response = client.newCall(request).execute();
            String responseContent = response.body().string();
            LogUtil.e("==QueryUnacceptedEventWithEid==", responseContent);
            event = ParseJsonUtil.parseEventDetailJson(responseContent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return event;
    }
}
