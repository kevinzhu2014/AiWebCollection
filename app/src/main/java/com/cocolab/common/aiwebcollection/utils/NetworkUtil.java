package com.cocolab.common.aiwebcollection.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

/**
 * Created by zhushengui on 2017/4/21.
 */

public class NetworkUtil {
    /**
     * 是否有网
     *
     * @param ctx
     * @return
     */
    public static boolean isNetworkAvailable(Context ctx) {
        try {
            ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = cm.getActiveNetworkInfo();
            return (info != null && info.isAvailable());
        } catch (Exception e) {
            QDLog.exception(e);
            return false;
        }
    }

    /**
     * 是否是wifi
     *
     * @param ctx
     * @return
     */
    public static boolean isWifiAvailable(Context ctx) {
        try {
            if (isNetworkAvailable(ctx)) {
                ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo info = cm.getActiveNetworkInfo();
                if (info != null && info.isAvailable() && info.getType() == ConnectivityManager.TYPE_WIFI) {
                    return true;
                }
            }
        } catch (Exception ex) {
            QDLog.exception(ex);
        }
        return false;
    }

    /**
     * 获取网络端口
     *
     * @param ctx
     * @return
     */
    public static String getNetworkAccessPoint(Context ctx) {
        if (isNetworkAvailable(ctx)) {
            ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = cm.getActiveNetworkInfo();
            if (info != null && info.isAvailable()) {
                return info.getTypeName() + "-" + info.getExtraInfo();
            }
        }
        return "";
    }


    /**
     * 没有网络
     */
    public static final String NETWORKTYPE_INVALID = "no_connection";
    /**
     * wap网络
     */
    public static final String NETWORKTYPE_WAP = "wap";
    /**
     * 2G网络
     */
    public static final String NETWORKTYPE_2G = "2G";
    /**
     * 3G和3G以上网络，或统称为快速网络
     */
    public static final String NETWORKTYPE_3G = "3G";
    /**
     * wifi网络
     */
    public static final String NETWORKTYPE_WIFI = "wifi";


    static String mNetWorkType;

    /**
     * 获取网络状态，wifi,wap,2g,3g.
     *
     * @param context 上下文
     * @return String 网络状态 {@link #NETWORKTYPE_3G}*{@link #NETWORKTYPE_INVALID},{@link #NETWORKTYPE_WAP}* <p>{@link #NETWORKTYPE_WIFI}
     */
    public static String getNetWorkType(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            String type = networkInfo.getTypeName();
            if (type.equalsIgnoreCase("WIFI")) {
                mNetWorkType = NETWORKTYPE_WIFI;
            } else if (type.equalsIgnoreCase("MOBILE")) {
                String proxyHost = android.net.Proxy.getDefaultHost();
                mNetWorkType = TextUtils.isEmpty(proxyHost) ? NETWORKTYPE_3G : NETWORKTYPE_WAP;
            }
        } else {
            mNetWorkType = NETWORKTYPE_INVALID;
        }
        return mNetWorkType;
    }
}
