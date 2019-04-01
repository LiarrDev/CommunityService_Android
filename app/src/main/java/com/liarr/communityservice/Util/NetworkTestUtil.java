package com.liarr.communityservice.Util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Looper;
import android.widget.Toast;

public class NetworkTestUtil {

    /**
     * 判断当前网络是否可用
     *
     * @param context 需要判断网络是否可用的 Context
     * @return 网络是否可用
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {       // 当前网络是连接的
                return info.getState() == NetworkInfo.State.CONNECTED;      // 当前所连接的网络可用
            }
        }
        return false;
    }

    /**
     * 网络不可用的情况下弹出 Toast 进行提示
     *
     * @param context Toast 所在 Context
     */
    public static void showNetworkDisableToast(Context context) {
        LogUtil.e("==NetworkError==", context.toString() + "");
        Looper.prepare();
        Toast.makeText(context, "Your Network Seems not Working. Please Check Again.", Toast.LENGTH_LONG).show();
        Looper.loop();
    }
}
