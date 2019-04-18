package com.liarr.communityservice.Util;

import com.liarr.communityservice.Database.Event;

import java.io.IOException;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class QueryEventUtil {

    public static List<Event> queryUnacceptedEvent(String location, int status) {
        List<Event> eventList = null;
        try {
            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = new FormBody.Builder()
                    .add("city", location)
                    .add("status", String.valueOf(status))
                    .build();
            Request request = new Request.Builder()
                    .url(HttpRequestUrlUtil.eventListUrl)
                    .post(requestBody)
                    .build();
            Response response = client.newCall(request).execute();
            String responseContent = response.body().string();
            eventList = ParseJsonUtil.parseEventListJson(responseContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return eventList;
    }
}
