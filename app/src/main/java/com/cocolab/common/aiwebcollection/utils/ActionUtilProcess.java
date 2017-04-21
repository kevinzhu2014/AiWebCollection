package com.cocolab.common.aiwebcollection.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.cocolab.common.aiwebcollection.MainActivity;
import com.cocolab.common.aiwebcollection.activity.BrowerActivity;
import com.google.zxing.common.StringUtils;

/**
 * Created by zhushengui on 2017/4/19.
 */

public class ActionUtilProcess {
    public static void openUrl(Context context, final String url){
        if(TextUtils.isEmpty(url)){
            return;
        }
        String processUrl = url;
        if(url.startsWith("file://")) {
            //本地文件，读取后直接显示html内容
            processUrl = url.substring(7);
            try {
                //尝试HtmlViewer打开
                Uri uri = Uri.parse("content://com.android.htmlfileprovider/" + processUrl);
                Intent intent2 = new Intent();
                intent2.setData(uri);
                intent2.setClassName("com.android.htmlviewer", "com.android.htmlviewer.HTMLViewerActivity");
                context.startActivity(intent2);
            } catch (Exception e) {
                e.printStackTrace();

                //尝试webview来打开
                Intent intent = new Intent(context, BrowerActivity.class);
                String content_url = "file://" + processUrl;
                intent.putExtra("web_url", content_url);
                context.startActivity(intent);
            }
        }else{
            Intent intent = new Intent(context, BrowerActivity.class);
            intent.putExtra("web_url", processUrl);
            context.startActivity(intent);
        }
    }
}
