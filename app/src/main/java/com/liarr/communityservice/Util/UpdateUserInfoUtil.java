package com.liarr.communityservice.Util;

import android.content.Context;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UpdateUserInfoUtil {

    public static boolean submitChangesToServer(Context context, int userId, String userName, String tel, String password, int doneEventNum, float point, int coin) {
        boolean changed = false;
        try {
            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = new FormBody.Builder()
                    .add("user.uid", String.valueOf(userId))
                    .add("user.userName", userName)
                    .add("user.tel", tel)
                    .add("user.password", password)
                    .add("user.doneNum", String.valueOf(doneEventNum))
                    .add("user.point", String.valueOf(point))
                    .add("user.coin", String.valueOf(coin))
                    .build();
            Request request = new Request.Builder()
                    .url(HttpRequestUrlUtil.updateUserInfoUrl)
                    .post(requestBody)
                    .build();
            Response response = client.newCall(request).execute();
            String responseContent = response.body().string();
            LogUtil.e("==ChangeUserInfo==", responseContent);
            changed = ParseJsonUtil.parseJsonMessage(responseContent);
            if (changed) {          // 修改成功则同步到本地
                ParseJsonUtil.parseUserInfoJson(context, responseContent);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return changed;
    }
}